package com.forter.contracts.validation;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.*;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;


@Target( { METHOD, FIELD, ANNOTATION_TYPE })
@Retention(RUNTIME)
@Constraint(validatedBy = OneOfValidator.class)
@Documented
public @interface OneOf {

    String message() default "{value}";

    Class<?>[] groups() default {};

    String[] value();

    Class<? extends Payload>[] payload() default {};
}