package com.forter.contracts.validation;

import org.apache.commons.validator.routines.InetAddressValidator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * Checks that the specified value is a valid ip address.
 * Currently only ipv4 is supported.
 * Returns isValid true if the value is null. Use @NotNull to explicitly reject null values.
 */
public class IpAddressValidator  implements ConstraintValidator<IpAddress, Object>{

    @Override
    public void initialize(IpAddress constraintAnnotation) {
        //TODO: Retrieve ipv4 / ipv6 constraints
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        return value == null || InetAddressValidator.getInstance().isValidInet4Address(String.valueOf(value));
    }
}
