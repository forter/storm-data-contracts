package com.forter.contracts.validation;

import com.google.common.base.Throwables;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.hibernate.validator.HibernateValidator;

import javax.validation.*;
import java.util.Set;

/**
 * Validates Plain Old Java Objects using {@link org.hibernate.validator.HibernateValidator}
 */
public class ContractValidator {

    private static class LazyHolder {
        private static final ContractValidator INSTANCE = new ContractValidator();
    }

    public static ContractValidator instance() {
        return LazyHolder.INSTANCE;
    }

    private final Validator validator;

    private ContractValidator() {
        ValidatorFactory validatorFactory = Validation.byProvider(HibernateValidator.class)
                .configure()
                .addValidatedValueHandler(new OptionalUnwrapper())
                .buildValidatorFactory();
        validator = validatorFactory.getValidator();
    }

    public <T> ContractValidationResult<T> validate(T contract) {
        try {
            Set<ConstraintViolation<T>> violations = validator.validate(contract);
            return new ContractValidationResult(contract, violations);
        }
        catch (ValidationException e) {
            return new ContractValidationResult<T>(contract, e);
        }

    }

}
