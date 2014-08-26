package com.forter.contracts;

/**
 * A Rich Bolt wrapper for {@link com.forter.contracts.IContractsBolt} that outputs a map
 */
public class ToMapContractsBoltExecutor<TInput, TOutput, TContractsBolt extends IContractsBolt<TInput, TOutput>> extends BaseContractsBoltExecutor<TInput, TOutput, TContractsBolt> {

    public ToMapContractsBoltExecutor(TContractsBolt contractsBolt) {
        super(contractsBolt);
    }

    @Override
    protected Object transformOutput(Object output) {
        return ObjectNodeConverter.instance().convertContractToMap(output);
    }
}


