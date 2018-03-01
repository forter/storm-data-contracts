package com.forter.contracts.mocks;

import com.forter.contracts.BaseContractsBolt;

/**
 * A Mock for testing inheriting the generic execute method.
 */
public abstract class MockGenericContractsBolt<TInput, TOutput> extends BaseContractsBolt<TInput, TOutput> {
    @Override
    public TOutput execute(TInput input) {
        return innerExecute(input);
    }

    protected abstract TOutput innerExecute(TInput input);
}
