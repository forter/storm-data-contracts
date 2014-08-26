package com.forter.contracts.validation;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.reflect.TypeToken;
import org.hibernate.validator.spi.valuehandling.ValidatedValueUnwrapper;

import java.lang.reflect.Type;

/**
 * Hibernate Validator support for Guava {@link com.google.common.base.Optional}
 *
 * Note:
 * The fact that we handle all Objects and not just Optional is a workaround for a bug I found in
 * ValidatorImpl#setValidatedValueHandlerToValueContextIfPresent() that does not perform valueContext.setValidatedValueHandler( null )
 * I am working with the hibernate-dev mailing list to fix this.
 */
public class OptionalUnwrapper extends ValidatedValueUnwrapper<Object> {

    @Override
    public Object handleValidatedValue(Object optional) {
        Preconditions.checkNotNull(optional, "Optional cannot be null");
        if (optional instanceof Optional) {
            return ((Optional)optional).orNull();
        }
        else {
            //patch for bugfix
            return optional;
        }

    }

    @Override
    public Type getValidatedValueType(Type valueType) {

        final TypeToken<?> typeToken = TypeToken.of(valueType);
        if (typeToken.getRawType().isAssignableFrom(Optional.class)) {
            Type rawType = typeToken.resolveType(Optional.class.getTypeParameters()[0]).getType();
            return rawType;
        }
        else {
            //patch for bugfix
            return valueType;
        }
    }
}