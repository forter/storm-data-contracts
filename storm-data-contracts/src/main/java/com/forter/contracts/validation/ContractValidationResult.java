package com.forter.contracts.validation;

import com.google.common.collect.Sets;

import javax.validation.ConstraintViolation;
import java.util.Set;

/**
 * A set of {@link ConstraintViolation}s that has a proper {@link #toString} method
 */
public class ContractValidationResult<T> {

    private final Set<ConstraintViolation<T>> violations;

    /**
     * creates a valid contract
     */
    public ContractValidationResult() {
        this.violations = Sets.newHashSet();
    }

    public ContractValidationResult(Set<ConstraintViolation<T>> violations) {
        this.violations = violations;
    }

    public String toString() {
        if (isValid()) {
            return "valid contract";
        }
        final StringBuffer sb = new StringBuffer();
        sb.append(violations.size()).append(" contract violations: ");
        for (final ConstraintViolation violation : violations) {
            sb.append(violation.getPropertyPath()).append(" is ").append(violation.getInvalidValue())
                    .append(" but ").append(violation.getMessage())
                    .append(". ");
        }
        final String message = sb.toString();
        return message;
    }

    public Set<ConstraintViolation<T>> getViolations() {
        return violations;
    }

    public boolean isValid() {
        return violations.isEmpty();
    }
}