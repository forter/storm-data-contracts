package com.forter.contracts.validation;

import org.hibernate.validator.HibernateValidator;
import org.hibernate.validator.internal.util.TypeResolutionHelper;

import javax.validation.*;
import java.util.Set;

public class ContractValidator {
    private final Validator validator;

    private ContractValidator() {
        ValidatorFactory validatorFactory = Validation.byProvider(HibernateValidator.class)
            .configure()
            .addValidatedValueHandler(new OptionalUnwrapper(new TypeResolutionHelper()))
            .addValidatedValueHandler(new NativeOptionalUnwrapper(new TypeResolutionHelper()))
            .buildValidatorFactory();
        this.validator = validatorFactory.getValidator();
    }

    public static ContractValidator instance() {
        return ContractValidator.LazyHolder.INSTANCE;
    }

    public <T> ValidatedContract<T> validate(T contract) {
        try {
            if (contract != null) {
                Set<ConstraintViolation<T>> e = this.validator.validate(contract);
                return new ValidatedContract<>(contract, e);
            } else {
                return new ValidatedContract<>(null, new ValidationException("Contract cannot be null"));
            }
        } catch (ValidationException exception) {
            return new ValidatedContract<>(contract, exception);
        }
    }

    private static class LazyHolder {
        private static final ContractValidator INSTANCE = new ContractValidator();

        private LazyHolder() {
        }
    }
}
