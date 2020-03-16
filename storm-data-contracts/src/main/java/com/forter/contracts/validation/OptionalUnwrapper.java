package com.forter.contracts.validation;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import org.hibernate.validator.internal.util.TypeResolutionHelper;

/**
 * Unwraps an {@code Optional} and returns the wrapped value and type.
 *
 */
public class OptionalUnwrapper extends TypeResolverBasedValueUnwrapper<Optional<?>> {


    public OptionalUnwrapper(TypeResolutionHelper typeResolutionHelper) {
        super(typeResolutionHelper);
    }

    @Override
    public Object handleValidatedValue(Optional<?> value) {
        Preconditions.checkNotNull(value, "Value cannot be null");
        return value.orNull();
    }
}