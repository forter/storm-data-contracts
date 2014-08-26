package com.forter.contracts.validation;

import org.hibernate.validator.HibernateValidator;

import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

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

    public ContractValidator() {
        ValidatorFactory validatorFactory = Validation.byProvider(HibernateValidator.class)
                .configure()
                .addValidatedValueHandler(new OptionalUnwrapper())
                .buildValidatorFactory();
        validator = validatorFactory.getValidator();
    }

    public <T> ContractValidationResult<T> validate(T pojo) {
        return new ContractValidationResult(validator.validate(pojo));
    }

}
