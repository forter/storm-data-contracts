package com.forter.contracts.mocks;

import com.google.common.base.Optional;
import org.hibernate.validator.valuehandling.UnwrapValidatedValue;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;

public class MockInput {

    @NotNull
    @DecimalMin(value="0", inclusive=true)
    public Integer x;

    @UnwrapValidatedValue
    @DecimalMin(value="1", inclusive=true)
    public Optional<Integer> y;

    @NotNull
    public Integer z;
}
