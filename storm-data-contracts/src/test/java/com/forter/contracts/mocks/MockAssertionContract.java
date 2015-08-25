package com.forter.contracts.mocks;

import javax.validation.constraints.AssertTrue;

public class MockAssertionContract {
    @AssertTrue
    public boolean thisFunctiionShouldStartWithIs() {
        return true;
    }

    @AssertTrue
    public void thisFunctionShouldBeBoolean () {

    }
}
