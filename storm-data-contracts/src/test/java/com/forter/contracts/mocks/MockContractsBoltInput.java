package com.forter.contracts.mocks;

import com.google.common.base.Optional;
import org.hibernate.validator.valuehandling.UnwrapValidatedValue;

import javax.validation.constraints.Max;
import javax.validation.constraints.NotNull;

/**
 * Input for {@link com.forter.contracts.BaseContractsBoltExecutorTest}
 */
public class MockContractsBoltInput {

    @Max(10)
    @NotNull
    public Integer input1;

    @Max(10)
    @UnwrapValidatedValue
    public Optional<Integer> optionalInput2;
}
