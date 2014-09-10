package com.forter.contracts.validation;

import com.google.common.base.Preconditions;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.List;

/**
 * Checks against one of the specified values.
 * Returns isValid true if the value is null. Use @NotNull to explicitly reject null values.
 */
public class OneOfValidator  implements ConstraintValidator<OneOf, Object>{

    private List<String> values;

    @Override
    public void initialize(OneOf constraintAnnotation) {
        values = Arrays.asList(constraintAnnotation.value());
        Preconditions.checkArgument(values.size() > 0, "Empty list input found in @OneOf annotation");
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        return value == null || values.contains(String.valueOf(value));
    }
}
