package com.forter.contracts.mocks;

import com.forter.contracts.validation.OneOf;

/**
 * Uses {@link com.forter.contracts.validation.OneOf}
 */
public class MockContractsWithOneOf {

    @OneOf({"in_the_list"})
    public String value;
}
