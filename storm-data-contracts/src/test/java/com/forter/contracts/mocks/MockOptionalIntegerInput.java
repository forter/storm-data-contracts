package com.forter.contracts.mocks;

import com.google.common.base.Optional;
import org.hibernate.validator.valuehandling.UnwrapValidatedValue;

import javax.validation.constraints.Min;

/**
 * A mock to test the {@link com.google.common.base.Optional} converter.
 */
public class MockOptionalIntegerInput {
    @UnwrapValidatedValue
    @Min(0)
    public Optional<Integer> optionalInteger;
}
