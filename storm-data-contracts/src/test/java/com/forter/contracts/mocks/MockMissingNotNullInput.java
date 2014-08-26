package com.forter.contracts.mocks;

import com.google.common.base.Optional;
import org.hibernate.validator.valuehandling.UnwrapValidatedValue;

import javax.validation.constraints.Max;

/**
 * Input for {@link com.forter.contracts.BaseContractsBoltExecutorTest}
 */
public class MockMissingNotNullInput {

    @Max(10)
    //@NotNull
    public Integer input1;

    @UnwrapValidatedValue
    public Optional<Integer> optionalInput2;
}
