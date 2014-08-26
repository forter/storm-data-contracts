package com.forter.contracts;

import com.forter.contracts.validation.ValidContract;

/**
 *  Rich Bolt wrapper for {@link com.forter.contracts.IContractsBolt} that outputs a {@link com.forter.contracts.validation.ValidContract}
 *  which is a marker wrapper so that the internal object does not need to be validated again.
 */
public class ToValidContractContractsBoltExecutor<TInput, TOutput, TContractsBolt extends IContractsBolt<TInput, TOutput>> extends BaseContractsBoltExecutor<TInput, TOutput, TContractsBolt> {

    public ToValidContractContractsBoltExecutor(TContractsBolt contractsBolt) {
        super(contractsBolt);
    }

    @Override
    protected Object transformOutput(Object output) {
        return new ValidContract(output);
    }
}
