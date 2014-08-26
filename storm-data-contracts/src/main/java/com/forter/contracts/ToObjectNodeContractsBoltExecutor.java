package com.forter.contracts;

/**
 * Rich Bolt wrapper for {@link com.forter.contracts.IContractsBolt} that outputs a {@link com.fasterxml.jackson.databind.node.ObjectNode}
 */
public class ToObjectNodeContractsBoltExecutor<TInput, TOutput, TContractsBolt extends IContractsBolt<TInput, TOutput>> extends BaseContractsBoltExecutor<TInput, TOutput, TContractsBolt> {

    public ToObjectNodeContractsBoltExecutor(TContractsBolt contractsBolt) {
        super(contractsBolt);
    }

    @Override
    protected Object transformOutput(Object output) {
        return ObjectNodeConverter.instance().convertContractToObjectNode(output);
    }
}