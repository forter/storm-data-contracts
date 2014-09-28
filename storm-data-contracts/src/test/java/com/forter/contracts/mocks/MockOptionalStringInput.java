package com.forter.contracts.mocks;

import com.google.common.base.Optional;
import org.hibernate.validator.valuehandling.UnwrapValidatedValue;

import javax.validation.constraints.Size;


public class MockOptionalStringInput {
    @UnwrapValidatedValue
    @Size(min=1)
    public Optional<String> optinalInput;
}