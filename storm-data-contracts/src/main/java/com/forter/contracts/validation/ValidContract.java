package com.forter.contracts.validation;

/**
 * Marks that the object has been validated.
 */
public class ValidContract<T> {

    private T contract;

    public ValidContract(T contract) {
        this.contract = contract;
    }

    public T getContract() {
        return contract;
    }
}