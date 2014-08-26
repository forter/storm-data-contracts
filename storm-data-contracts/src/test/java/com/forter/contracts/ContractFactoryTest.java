package com.forter.contracts;

import com.forter.contracts.mocks.MockInput;
import org.testng.annotations.Test;

import static org.fest.assertions.api.Assertions.assertThat;

/**
 * Tests {@link ContractFactory}
 */
public class ContractFactoryTest {

    @Test
    public void test() {
        ContractFactory<MockInput> f = new ContractFactory(MockInput.class);
        MockInput input = f.newInstance();
        assertThat(input.x).isNull();
        assertThat(input.y.isPresent()).isFalse();
        assertThat(input.z).isNull();
    }
}
