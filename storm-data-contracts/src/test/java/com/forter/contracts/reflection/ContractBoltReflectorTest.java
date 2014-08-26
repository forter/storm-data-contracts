package com.forter.contracts.reflection;

import com.forter.contracts.mocks.*;
import org.testng.annotations.Test;

import static org.fest.assertions.api.Assertions.assertThat;

/**
 * Tests {@link com.forter.contracts.reflection.ContractBoltReflector}
 */
public class ContractBoltReflectorTest {

    @Test
    public void testNotNullContract() {
        ContractBoltReflector reflector = new ContractBoltReflector(new MockContractsBolt());
        assertThat(reflector.getExecuteReturnType()).isEqualTo(ContractBoltReflector.EXECUTE_RETURN_TYPE.NOT_NULL_CONTRACT);
        assertThat(reflector.getInputClass()).isEqualTo(MockContractsBoltInput.class);
    }

    @Test
    public void testOptionalContract() {
        ContractBoltReflector reflector = new ContractBoltReflector(new MockOptionalContractsBolt());
        assertThat(reflector.getExecuteReturnType()).isEqualTo(ContractBoltReflector.EXECUTE_RETURN_TYPE.OPTIONAL_CONTRACT);
        assertThat(reflector.getInputClass()).isEqualTo(MockContractsBoltInput.class);
    }

    @Test
    public void testCollectionContract() {
        ContractBoltReflector reflector = new ContractBoltReflector(new MockCollectionContractsBolt());
        assertThat(reflector.getExecuteReturnType()).isEqualTo(ContractBoltReflector.EXECUTE_RETURN_TYPE.COLLECTION_CONTRACTS);
        assertThat(reflector.getInputClass()).isEqualTo(MockContractsBoltInput.class);
    }

    @Test(expectedExceptions = IllegalStateException.class)
    public void testMissingNotNullAnnotation() {
        new ContractBoltReflector(new MockMissingNotNullBolt());
    }

    @Test(expectedExceptions = IllegalStateException.class)
    public void testMissingUnwrapAnnotation() {
        new ContractBoltReflector(new MockMissingUnwrapBolt());
    }

    @Test
    public void testOutputFields() {
        ContractBoltReflector reflector = new ContractBoltReflector(new MockContractsBolt());
        assertThat(reflector.getOutputFields()).containsExactly("output1","optionalOutput2");
    }
}
