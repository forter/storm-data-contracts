package com.forter.contracts;

import com.google.common.base.Optional;
import org.hibernate.validator.valuehandling.UnwrapValidatedValue;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;

public class MockInputWithNonPublic {

    @NotNull
    @DecimalMin(value="0", inclusive=true)
    public Integer x;

    @UnwrapValidatedValue
    @DecimalMin(value="1", inclusive=true)
    public Optional<Integer> y;

    @NotNull
    Integer z;

    @UnwrapValidatedValue
    private Optional<String> a;

    public Optional<String> getA() {
        return a;
    }
}
