package com.forter.contracts;

import backtype.storm.task.TopologyContext;
import backtype.storm.topology.BasicOutputCollector;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.ReportedFailedException;
import backtype.storm.topology.base.BaseBasicBolt;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Tuple;
import backtype.storm.utils.Utils;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.forter.contracts.reflection.ContractsBoltReflector;
import com.forter.contracts.validation.ContractValidator;
import com.forter.contracts.validation.ValidatedContract;
import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import static com.google.common.collect.Iterables.*;

/**
 * Bolt base class that uses Data Objects for input and output.
 */
public class BaseContractsBoltExecutor<TInput, TOutput, TContractsBolt extends IContractsBolt<TInput, TOutput>> extends BaseBasicBolt {
    private final TContractsBolt delegate;
    private transient ContractFactory<TInput> inputFactory;
    private transient ContractsBoltReflector reflector;
    private transient TOutput defaultOutput;
    private transient BaseContractsBoltExecutor.IsInvalidPredicate isInvalidPredicate;
    private transient BaseContractsBoltExecutor.ValidateContractTransformation validationTransformation;
    private transient String id;

    public BaseContractsBoltExecutor(TContractsBolt contractsBolt) {
        this.delegate = contractsBolt;
    }

    @Override
    public void prepare(Map stormConf, TopologyContext context) {
        this.reflector = new ContractsBoltReflector(this.delegate);
        this.validationTransformation = new ValidateContractTransformation();
        this.isInvalidPredicate = new IsInvalidPredicate();
        this.inputFactory = new ContractFactory(this.reflector.getInputClass());
        this.delegate.prepare(stormConf, context);
        this.defaultOutput = this.delegate.createDefaultOutput();
        this.id = context.getThisComponentId();
        Preconditions.checkNotNull(this.defaultOutput, "getDefaultOutput cannot return null. Use Optional.absent() instead of null.");
        final ValidatedContract<TOutput> validationResult = ContractValidator.instance().validate(this.defaultOutput);
        Preconditions.checkState(
                validationResult.isValid(),
                "Default output failed contract validation: %s",
                validationResult.toString());
    }

    @Override
    public void execute(Tuple inputTuple, BasicOutputCollector collector) {
        TOutput output = defaultOutput;

        RuntimeException exception = null;

        final Object id = inputTuple.getValue(0);
        try {
            final Object data = inputTuple.getValue(1);
            ValidatedContract validatedInputContract = transformAndValidateInput(data);

            if (!validatedInputContract.isValid()) {
                throw new ContractViolationException(validatedInputContract);
            }
            else {
                TInput input = (TInput) validatedInputContract.getContract();
                output = delegate.execute(input);
            }
        } catch (ContractViolationException cve) {
            final List<ContractViolationException> violations = Lists.newArrayList(cve);
            exception = new ContractViolationReportedFailedException(violations, this.id);
        } catch (RuntimeException e) {
            exception = new ReportedFailedException(e);
        } finally {
            Iterable<Object> outputContracts;
            Iterable<ValidatedContract> invalidOutputContracts;

            if (output == null) {
                outputContracts = ImmutableList.of();
                invalidOutputContracts = ImmutableList.of(validationTransformation.apply(output));
            }
            else {
                outputContracts = iterableContracts(output);
                invalidOutputContracts = filter(transform(outputContracts, validationTransformation), isInvalidPredicate);
            }

            if (isEmpty(invalidOutputContracts)) {
                for (Object contract : outputContracts) {
                    emit(id, contract, collector);
                }
            }
            else {
                emit(id, defaultOutput, collector);

                final List<ContractViolationException> violations = Lists.newArrayList();

                for (ValidatedContract invalidContract : invalidOutputContracts) {
                    violations.add(new ContractViolationException(invalidContract));
                }

                exception = new ContractViolationReportedFailedException(violations, this.id);
            }
        }

        if (exception != null) {
            throw exception;
        }
    }

    @Override
    public void cleanup() {
        delegate.cleanup();
    }

    public TContractsBolt getContractBolt() {
        return this.delegate;
    }

    private ValidatedContract transformAndValidateInput(final Object contract) {

        TInput input = transformInput(contract);
        return ContractValidator.instance().validate(input);
    }

    private boolean isOfTypeInput(Object contract) {
        return reflector.getInputClass().equals(contract.getClass());
    }

    /**
     * Override to support different input formats.
     */
    protected TInput transformInput(Object contract) {

        if (contract == null) {
            return null;
        }
        if (isOfTypeInput(contract)) {
            return (TInput) contract;
        }
        else {
            //lock is needed to ensure no threads are iterating over the contract in parallel
            synchronized (contract) {
                if (contract instanceof ObjectNode) {
                    return ContractConverter.instance().convertObjectNodeToContract((ObjectNode) contract, inputFactory);
                } else {
                    return ContractConverter.instance().convertContractToContract(contract, inputFactory);
                }
            }
        }
    }

    private void emit(Object id, Object contract, BasicOutputCollector collector) {
        ArrayList<Object> tuple = Lists.newArrayList(id, transformOutput(contract));
        collector.emit(Utils.DEFAULT_STREAM_ID, tuple);
    }

    private Iterable<Object> iterableContracts(TOutput output) {

        switch (reflector.getExecuteReturnType()) {
            case NOT_NULL_CONTRACT:
                return ImmutableList.of((Object)output);

            case OPTIONAL_CONTRACT:
                Optional optional = (Optional) output;
                if (optional.isPresent()) {
                    return ImmutableList.of((Object)optional.get());
                }
                return ImmutableList.of();

            case COLLECTION_CONTRACTS:
                return (Collection) output;

            default:
                Preconditions.checkState(false, "Unsupported executionReturnType " + reflector.getExecuteReturnType());
                //suppress compiler warning
                return null;
        }
    }

    /**
     * Override this method to provide different output formats.
     */
    protected Object transformOutput(Object output) {
        return output;
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        declarer.declare(new Fields("id", "validatedContract"));
    }

    @Override
    public Map<String, Object> getComponentConfiguration() {
        return delegate.getComponentConfiguration();
    }

    protected ContractsBoltReflector getReflector() {
        return reflector;
    }

    static class IsInvalidPredicate implements Predicate<ValidatedContract>, Serializable {

        @Override
        public boolean apply(ValidatedContract input) {
            return !input.isValid();
        }
    }

    static class ValidateContractTransformation implements Function<Object, ValidatedContract>, Serializable {
        @Override
        public ValidatedContract apply(Object input) {
            return ContractValidator.instance().validate(input);
        }
    }

    public static class ContractViolationReportedFailedException extends ReportedFailedException {
        private final List<ContractViolationException> violations;

        public ContractViolationReportedFailedException(List<ContractViolationException> violations, String id) {
            super("There were some contract violations in '" + id + "' bolt." + getViolationsString(violations));
            this.violations = violations;
        }

        private static String getViolationsString(List<ContractViolationException> violations) {
            StringBuilder sb = new StringBuilder();

            if (!violations.isEmpty()) {
                sb.append(" ");
                sb.append("Detected violations were:").append("\n");
                for (ContractViolationException cve : violations) {
                    sb.append(" - ").append(cve.getMessage()).append("\n");
                }
            }

            return sb.toString();
        }

        public List<ContractViolationException> getContractViolations() {
            return violations;
        }
    }
}
