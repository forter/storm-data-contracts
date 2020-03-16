package com.forter.contracts.validation;

import org.hibernate.validator.internal.util.TypeResolutionHelper;

import java.util.Objects;
import java.util.Optional;

/**
 * Unwraps an {@code Optional} and returns the wrapped value and type.
 */
public class NativeOptionalUnwrapper extends TypeResolverBasedValueUnwrapper<Optional<?>> {

    public NativeOptionalUnwrapper(TypeResolutionHelper typeResolutionHelper) {
        super(typeResolutionHelper);
    }

    @Override
    public Object handleValidatedValue(Optional<?> value) {
        Objects.requireNonNull(value, "Value cannot be null");
        return value.orElse(null);
    }
}
