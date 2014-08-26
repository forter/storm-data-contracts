package com.forter.contracts.testng;

import com.google.common.base.Optional;
import org.hibernate.validator.valuehandling.UnwrapValidatedValue;

import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

/**
 * Input of {@link MyBolt}
 */
public class MyBoltInput {
    @NotNull
    @Min(0)
    public Integer x;

    @UnwrapValidatedValue
    @Pattern(regexp="\\p{L}*")
    public Optional<String> y;
}
