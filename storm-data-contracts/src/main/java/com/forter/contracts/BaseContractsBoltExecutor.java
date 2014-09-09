package com.forter.contracts;

import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.*;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Tuple;
import backtype.storm.utils.Utils;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.forter.contracts.reflection.ContractsBoltReflector;
import com.forter.contracts.validation.ValidatedContract;
import com.forter.contracts.validation.ContractValidator;
import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import static com.google.common.collect.Iterables.filter;
import static com.google.common.collect.Iterables.isEmpty;
import static com.google.common.collect.Iterables.transform;

/**
 * Bolt base class that uses Data Objects for input and output.
 */
public class BaseContractsBoltExecutor<TInput, TOutput, TContractsBolt extends IContractsBolt<TInput, TOutput>>
        implements IRichBolt {

    private final TContractsBolt delegate;
    private transient ContractFactory<TInput> inputFactory;
    private transient ContractsBoltReflector reflector;
    private transient OutputCollector collector;
    private transient TOutput defaultOutput;
    private BaseContractsBoltExecutor.IsInvalidPredicate isInvalidPredicate = new IsInvalidPredicate();
    private BaseContractsBoltExecutor.ValidateContractTransformation validationTransformation = new ValidateContractTransformation();

    public BaseContractsBoltExecutor(TContractsBolt contractsBolt) {
        this.delegate = contractsBolt;
    }

    @Override
    public void prepare(Map stormConf, TopologyContext context, OutputCollector collector) {
        reflector = new ContractsBoltReflector(delegate);
        inputFactory = new ContractFactory(reflector.getInputClass());
        this.collector = collector;
        delegate.prepare(stormConf, context);
        defaultOutput = delegate.createDefaultOutput();
        Preconditions.checkNotNull(defaultOutput, "getDefaultOutput cannot return null. Use Optional.absent() instead of null.");
        final ValidatedContract<TOutput> validationResult = ContractValidator.instance().validate(defaultOutput);
        Preconditions.checkState(
                validationResult.isValid(),
                "Default output failed contract validation: %s",
                validationResult.toString());
    }

    @Override
    public void execute(Tuple inputTuple) {
        boolean fail = false;
        TOutput output = defaultOutput;
        final Object id = inputTuple.getValue(0);
        try {
            final Object data = inputTuple.getValue(1);
            ValidatedContract validatedInputContract = transformAndValidateInput(data);

            if (!validatedInputContract.isValid()) {
                collector.reportError(new ContractViolationException(validatedInputContract));
                fail = true;
            }
            else {
                TInput input = (TInput) validatedInputContract.getContract();
                output = delegate.execute(input);
            }

        } catch (RuntimeException e) {
            collector.reportError(e);
            fail = true;
        }
        finally {
            Iterable<Object> outputContracts;
            Iterable<ValidatedContract> invalidOutputContracts;


            if (output == null) {
                outputContracts = ImmutableList.of();
                invalidOutputContracts = ImmutableList.of(validationTransformation.apply(output));
            }
            else {
                outputContracts = iterableContracts(output);
                invalidOutputContracts =
                        filter(transform(outputContracts,
                                        validationTransformation),
                                isInvalidPredicate);
            }

            if (isEmpty(invalidOutputContracts)) {
                for (Object contract : outputContracts) {
                    emit(inputTuple, id, contract);
                }
            }
            else {
                emit(inputTuple, id, defaultOutput);
                for (ValidatedContract invalidContract: invalidOutputContracts) {
                    collector.reportError(new ContractViolationException(invalidContract));
                }
                fail = true;
            }

            if (fail) {
                collector.fail(inputTuple);
            }
            else {
                collector.ack(inputTuple);
            }
        }
    }

    @Override
    public void cleanup() {
        delegate.cleanup();
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

    private void emit(Tuple inputTuple, Object id, Object contract) {
        ArrayList<Object> tuple = Lists.newArrayList(id, transformOutput(contract));
        collector.emit(Utils.DEFAULT_STREAM_ID, inputTuple, tuple);
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
}
