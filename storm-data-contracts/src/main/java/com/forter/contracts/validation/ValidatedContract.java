package com.forter.contracts.validation;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
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
    private final Optional<ValidationException> exception;
    private final T contract;

    /**
     * creates a valid contract
     */
    public ValidatedContract(T contract) {
        this(contract, Sets.<ConstraintViolation<T>>newHashSet());
    }


    public ValidatedContract(T contract, Set<ConstraintViolation<T>> violations) {
        this(contract, violations, Optional.<ValidationException>absent());
    }

    public ValidatedContract(T contract, ValidationException e) {
        this(contract, Sets.<ConstraintViolation<T>>newHashSet(), Optional.of(e));
    }

    private ValidatedContract(T contract, Set<ConstraintViolation<T>> violations, Optional<ValidationException> exception) {
        this.contract = contract;
        this.violations = violations;

        /** TODO: Remove when https://github.com/forter/storm-data-contracts/issues/6 is resolved
         * and the null check is removed from {@link OptionalUnwrapper#handleValidatedValue}
         */
        this.exception = exception;
    }

    public String toString() {
        if (isValid()) {
            return "valid contract";
        }
        final StringBuffer sb = new StringBuffer();
        if (!violations.isEmpty()) {
            sb.append(violations.size()).append(" contract violations: ");
            for (final ConstraintViolation violation : violations) {
                sb.append(violation.getPropertyPath()).append(" is ").append(violation.getInvalidValue())
                        .append(" but ").append(violation.getMessage())
                        .append(". ");
            }
        }
        if (exception.isPresent()) {
            Throwable rootCause = Throwables.getRootCause(exception.get());
            sb.append(rootCause.getMessage());
        }
        if (contract == null) {
            sb.append(" Contract: null");
        }
        else {
            sb.append(" Contract:").append(ToStringBuilder.reflectionToString(contract, ToStringStyle.SHORT_PREFIX_STYLE));
        }
        final String message = sb.toString();
        return message;
    }

    public boolean isValid() {
        return violations.isEmpty() && !exception.isPresent();
    }

    public T getContract() {
        return contract;
    }
}