package com.forter.contracts;

import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.*;
import backtype.storm.topology.base.BaseBasicBolt;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Tuple;
import backtype.storm.utils.Utils;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.forter.contracts.reflection.ContractBoltReflector;
import com.forter.contracts.validation.ContractValidationResult;
import com.forter.contracts.validation.ContractValidator;
import com.forter.contracts.validation.ValidContract;
import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

/**
 * Bolt base class that uses Data Objects for input and output.
 */
public class BaseContractsBoltExecutor<TInput, TOutput, TContractsBolt extends IContractsBolt<TInput, TOutput>>
        implements IRichBolt {

    private final TContractsBolt delegate;
    private transient ContractFactory<TInput> inputFactory;
    private transient ContractBoltReflector reflector;
    private transient OutputCollector collector;

    public BaseContractsBoltExecutor(TContractsBolt contractsBolt) {
        this.delegate = contractsBolt;
    }

    @Override
    public void prepare(Map stormConf, TopologyContext context, OutputCollector collector) {
        reflector = new ContractBoltReflector(delegate);
        inputFactory = new ContractFactory(reflector.getInputClass());
        this.collector = collector;
        delegate.prepare(stormConf, context);
    }

    @Override
    public void execute(Tuple tuple) {
        try {
            final Object id = tuple.getValue(0);
            final Object data = tuple.getValue(1);
            final TOutput output = delegateExecution(data);
            emitOutput(collector, tuple, id, output);
        }
        catch (Throwable t) {
            throw new ReportedFailedException(t);
        }
    }

    @Override
    public void cleanup() {
        delegate.cleanup();
    }

    private TOutput delegateExecution(Object data) {
        TInput input;
        ContractValidationResult<TInput> inputViolations;
        if (data instanceof ValidContract) {
            Object contract = ((ValidContract) data).getContract();
            if (reflector.getInputClass().equals(contract.getClass())) {
                inputViolations = new ContractValidationResult();
                input = (TInput) contract;
            }
            else {
                input = convertContractToContract(data);
                inputViolations = validatedInput(input);
            }
        }
        else if (data instanceof ObjectNode) {
            input = convertObjectNodeToContract((ObjectNode) data);
            inputViolations = validatedInput(input);
        }
        else if (reflector.getInputClass().equals(data.getClass())) {
            input = (TInput) data;
            inputViolations = validatedInput(input);
        }
        else {
            input = convertContractToContract(data);
            inputViolations = validatedInput(input);
        }


        final TOutput output;
        if (inputViolations.isValid()) {
            output = delegate.executeValidInput(input);
        }
        else {
            output = delegate.executeInvalidInput(input, inputViolations);
        }
        return output;
    }

    private TInput convertContractToContract(Object data) {
        return ObjectNodeConverter.instance().updateContractToContract((ObjectNode) data, inputFactory.newInstance());
    }

    private TInput convertObjectNodeToContract(ObjectNode data) {
        return ObjectNodeConverter.instance().updateObjectNodeToContract((ObjectNode) data, inputFactory.newInstance());
    }

    private ContractValidationResult<TInput> validatedInput(TInput input) {
        return ContractValidator.instance().validate(input);
    }

    private void emitOutput(OutputCollector collector, Tuple inputTuple, Object id, TOutput output) {
        Preconditions.checkNotNull(output, "Bolt output cannot be null");
        switch (reflector.getExecuteReturnType()) {
            case NOT_NULL_CONTRACT:
                emitContract(collector, inputTuple, id, output);
                break;

            case OPTIONAL_CONTRACT:
                Optional optional = (Optional) output;
                if (optional.isPresent()) {
                    emitContract(collector, inputTuple, id, optional.get());
                }
                break;

            case COLLECTION_CONTRACTS:
                for(Object contract : (Collection) output) {
                    emitContract(collector, inputTuple, id, contract);
                }
                break;

            default:
                Preconditions.checkState(false, "Unsupported executionReturnType " + reflector.getExecuteReturnType());
        }
    }

    private void emitContract(OutputCollector collector, Tuple inputTuple, Object id, Object output) {
        validateOutput(output);
        ArrayList<Object> tuple = Lists.newArrayList(id, transformOutput(output));
        collector.emit(Utils.DEFAULT_STREAM_ID, inputTuple, tuple);
    }

    /**
     * Override this method to provide different output formats.
     */
    protected Object transformOutput(Object output) {
        return output;
    }

    private void validateOutput(Object output) {
        final ContractValidationResult<Object> outputViolations = ContractValidator.instance().validate(output);
        Preconditions.checkState(outputViolations.isValid(), outputViolations.toString());
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        declarer.declare(new Fields("id","validatedContract"));
    }

    @Override
    public Map<String, Object> getComponentConfiguration() {
        return null;
    }
}
