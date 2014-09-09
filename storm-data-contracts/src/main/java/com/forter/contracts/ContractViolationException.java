package com.forter.contracts;

import com.forter.contracts.validation.ValidatedContract;

/**
 * A reusable adapter class between {@link com.forter.contracts.validation.ValidatedContract} and {@link java.lang.RuntimeException}
 */
class ContractViolationException extends RuntimeException {

    private final ValidatedContract result;
    public ContractViolationException(ValidatedContract result) {
        super();
        this.result = result;
    }

    @Override
    public String getMessage() {
        //evaluate toString only when needed.
        return result.toString();
    }

    @Override
    public Throwable fillInStackTrace()
    {
        return this;
    }
}
