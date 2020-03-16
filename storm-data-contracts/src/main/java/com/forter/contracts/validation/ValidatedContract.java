package com.forter.contracts.validation;

import com.google.common.base.Throwables;
import com.google.common.collect.Sets;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import javax.validation.ConstraintViolation;
import javax.validation.ValidationException;
import java.util.Set;

/**
 * A set of {@link ConstraintViolation}s that has a proper {@link #toString} method.
 * Also supports {@link javax.validation.ValidationException}
 */
public class ValidatedContract<T> {

    private final Set<ConstraintViolation<T>> violations;
    private final ValidationException exception;
    private final T contract;


    public ValidatedContract(T contract, Set<ConstraintViolation<T>> violations) {
        this(contract, violations, null);
    }

    public ValidatedContract(T contract, ValidationException e) {
        this(contract, Sets.newHashSet(), e);
    }

    private ValidatedContract(T contract, Set<ConstraintViolation<T>> violations, ValidationException exception) {
        this.contract = contract;
        this.violations = violations;

        /** TODO: Remove when https://github.com/forter/storm-data-contracts/issues/6 is resolved
         * and the null check is removed from {@link OptionalUnwrapperhandleValidatedValue}
         */
        this.exception = exception;
    }

    public String toString() {
        if (isValid()) {
            return "valid contract";
        }
        final StringBuilder sb = new StringBuilder();
        if (!violations.isEmpty()) {
            sb.append(violations.size()).append(" contract violations: ");
            for (final ConstraintViolation<T> violation : violations) {
                sb.append(violation.getPropertyPath()).append(" is ").append(violation.getInvalidValue())
                    .append(" but ").append(violation.getMessage())
                    .append(". ");
            }
        }
        if (exception != null) {
            Throwable rootCause = Throwables.getRootCause(exception);
            sb.append(rootCause.getMessage());
        }
        if (contract == null) {
            sb.append(" Contract: null");
        } else {
            sb.append(" Contract:").append(ToStringBuilder.reflectionToString(contract, ToStringStyle.SHORT_PREFIX_STYLE));
        }
        return sb.toString();
    }

    public boolean isValid() {
        return violations.isEmpty() && exception != null;
    }

    public T getContract() {
        return contract;
    }
}
