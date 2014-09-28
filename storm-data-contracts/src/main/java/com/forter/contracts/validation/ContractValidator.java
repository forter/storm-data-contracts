package com.forter.contracts.validation;

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
                .addValidatedValueHandler(new OptionalStringUnwrapper())
                .buildValidatorFactory();
        validator = validatorFactory.getValidator();
    }

    public <T> ValidatedContract<T> validate(T contract) {
        try {
            if (contract != null) {
                Set<ConstraintViolation<T>> violations = validator.validate(contract);
                return new ValidatedContract(contract, violations);
            }
            else {
                return new ValidatedContract(contract, new ValidationException("Contract cannot be null"));
            }
        }
        catch (ValidationException e) {
            return new ValidatedContract<T>(contract, e);
        }
    }

}
