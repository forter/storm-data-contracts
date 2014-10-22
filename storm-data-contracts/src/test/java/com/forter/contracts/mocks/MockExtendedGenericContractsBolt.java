package com.forter.contracts.mocks;

import com.google.common.base.Optional;

/**
 * Mock for a bolt that extends an execute method without implementing it.
 *
 * @see com.forter.contracts.reflection.ContractsBoltReflectorTest#testGenericInheritedMethod()
 */
public class MockExtendedGenericContractsBolt extends MockGenericContractsBolt<MockContractsBoltInput, MockContractsBoltOutput> {
    // Instead of copying the methods content.
    private final MockContractsBolt delegate = new MockContractsBolt();

    @Override
    protected MockContractsBoltOutput innerExecute(MockContractsBoltInput input) {
        return delegate.execute(input);
    }

    @Override
    public MockContractsBoltOutput createDefaultOutput() {
        return delegate.createDefaultOutput();
    }
}
