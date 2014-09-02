package com.forter.contracts.validation;

import com.google.common.base.Optional;
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
public class ContractValidationResult<T> {

    private final Set<ConstraintViolation<T>> violations;
    private final Optional<ValidationException> exception;
    private final Optional<T> contract;
    /**
     * creates a valid contract
     */
    public ContractValidationResult() {
        this(Optional.<T>absent(), Sets.<ConstraintViolation<T>>newHashSet(), Optional.<ValidationException>absent());
    }

    public ContractValidationResult(T contract, Set<ConstraintViolation<T>> violations) {
        this(Optional.fromNullable(contract), violations, Optional.<ValidationException>absent());
    }

    public ContractValidationResult(T contract, ValidationException e) {
        this(Optional.fromNullable(contract), Sets.<ConstraintViolation<T>>newHashSet(), Optional.of(e));
    }

    private ContractValidationResult(Optional<T> contract, Set<ConstraintViolation<T>> violations, Optional<ValidationException> exception) {
        this.contract = contract;
        this.violations = violations;
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

        sb.append(" Contract:").append(ToStringBuilder.reflectionToString(contract, ToStringStyle.SHORT_PREFIX_STYLE));
        final String message = sb.toString();
        return message;
    }

    public boolean isValid() {
        return violations.isEmpty() && !exception.isPresent();
    }
}