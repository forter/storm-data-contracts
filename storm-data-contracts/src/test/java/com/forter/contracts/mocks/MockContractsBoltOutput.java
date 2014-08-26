package com.forter.contracts.mocks;

import com.google.common.base.Optional;
import org.hibernate.validator.valuehandling.UnwrapValidatedValue;

import javax.validation.constraints.Max;
import javax.validation.constraints.NotNull;

/**
 * Output for {@link com.forter.contracts.BaseContractsBoltExecutorTest}
 */
public class MockContractsBoltOutput {

    @NotNull
    public Integer output1;

    @Max(10)
    @UnwrapValidatedValue
    public Optional<Integer> optionalOutput2;
}
