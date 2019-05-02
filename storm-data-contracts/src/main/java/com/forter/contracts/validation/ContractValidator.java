package com.forter.contracts.validation;

import java.util.Set;
import javax.validation.Validation;
import javax.validation.ValidationException;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import org.hibernate.validator.HibernateValidator;
import org.hibernate.validator.internal.util.TypeResolutionHelper;

public class ContractValidator {
    private final Validator validator;

    public static ContractValidator instance() {
        return ContractValidator.LazyHolder.INSTANCE;
    }

    private ContractValidator() {
        ValidatorFactory validatorFactory = Validation.byProvider(HibernateValidator.class)
                .configure()
                .addValidatedValueHandler(new OptionalUnwrapper(new TypeResolutionHelper()))
                .buildValidatorFactory();
        this.validator = validatorFactory.getValidator();
    }

    public <T> ValidatedContract<T> validate(T contract) {
        try {
            if(contract != null) {
                Set e = this.validator.validate(contract, new Class[0]);
                return new ValidatedContract(contract, e);
            } else {
                return new ValidatedContract(contract, new ValidationException("Contract cannot be null"));
            }
        } catch (ValidationException var3) {
            return new ValidatedContract(contract, var3);
        }
    }

    private static class LazyHolder {
        private static final ContractValidator INSTANCE = new ContractValidator();

        private LazyHolder() {
        }
    }
}
