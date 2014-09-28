package com.forter.contracts.validation;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.reflect.TypeToken;
import org.hibernate.validator.spi.valuehandling.ValidatedValueUnwrapper;

import java.lang.reflect.Type;

/**
 * Hibernate Validator support for Guava {@link com.google.common.base.Optional}
 * Treats Empty strings / whitespace string as null.
 * Note:
 * The fact that we handle all Objects and not just Optional is a workaround for a bug I found in
 * ValidatorImpl#setValidatedValueHandlerToValueContextIfPresent() that does not perform valueContext.setValidatedValueHandler( null )
 * I am working with the hibernate-dev mailing list to fix this.
 */
public class OptionalStringUnwrapper extends ValidatedValueUnwrapper<Object> {

    @Override
    public Object handleValidatedValue(Object optional) {
        Preconditions.checkNotNull(optional, "Value cannot be null");
        if (optional instanceof Optional) {
            Object o = ((Optional)optional).orNull();
            if(o != null && o instanceof String) {
                String trimmedInput = ((String)o).trim();
                if(trimmedInput.equals("")) {
                    return Optional.absent();
                } else {
                    return o;
                }
            }
            return o;
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