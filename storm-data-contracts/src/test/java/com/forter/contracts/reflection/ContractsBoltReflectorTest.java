package com.forter.contracts.reflection;

import com.forter.contracts.mocks.*;
import org.testng.annotations.Test;

import static org.fest.assertions.api.Assertions.assertThat;

/**
 * Tests {@link ContractsBoltReflector}
 */
public class ContractsBoltReflectorTest {

    @Test
    public void testNotNullContract() {
        ContractsBoltReflector reflector = new ContractsBoltReflector(new MockContractsBolt());
        assertNotNullContract(reflector);
    }

    @Test
    public void testInheritedMethod() {
        ContractsBoltReflector reflector = new ContractsBoltReflector(new MockContractsBoltSubclass());
        assertNotNullContract(reflector);
    }

    @Test
    public void testOptionalContract() {
        ContractsBoltReflector reflector = new ContractsBoltReflector(new MockOptionalContractsBolt());
        assertThat(reflector.getExecuteReturnType()).isEqualTo(ContractsBoltReflector.EXECUTE_RETURN_TYPE.OPTIONAL_CONTRACT);
        assertThat(reflector.getInputClass()).isEqualTo(MockContractsBoltInput.class);
    }

    @Test
    public void testCollectionContract() {
        ContractsBoltReflector reflector = new ContractsBoltReflector(new MockCollectionContractsBolt());
        assertThat(reflector.getExecuteReturnType()).isEqualTo(ContractsBoltReflector.EXECUTE_RETURN_TYPE.COLLECTION_CONTRACTS);
        assertThat(reflector.getInputClass()).isEqualTo(MockContractsBoltInput.class);
    }

    @Test(expectedExceptions = IllegalStateException.class)
    public void testMissingNotNullAnnotation() {
        new ContractsBoltReflector(new MockMissingNotNullBolt());
    }

    @Test(expectedExceptions = IllegalStateException.class)
    public void testMissingUnwrapAnnotation() {
        new ContractsBoltReflector(new MockMissingUnwrapBolt());
    }

    @Test
    public void testOutputFields() {
        ContractsBoltReflector reflector = new ContractsBoltReflector(new MockContractsBolt());
        assertThat(reflector.getOutputFields()).containsExactly("output1","optionalOutput2");
    }

    private static void assertNotNullContract(ContractsBoltReflector reflector) {
        assertThat(reflector.getExecuteReturnType()).isEqualTo(ContractsBoltReflector.EXECUTE_RETURN_TYPE.NOT_NULL_CONTRACT);
        assertThat(reflector.getInputClass()).isEqualTo(MockContractsBoltInput.class);
    }
}
