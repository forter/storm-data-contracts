package com.forter.contracts.mocks;

import com.google.common.base.Optional;

import javax.validation.constraints.Max;
import javax.validation.constraints.NotNull;

/**
 * Input for {@link com.forter.contracts.BaseContractsBoltExecutorTest}
 */
public class MockMissingUnwrapInput {

    @Max(10)
    @NotNull
    public Integer input1;

    //@UnwrapValidatedValue
    public Optional<Integer> optionalInput2;
}
