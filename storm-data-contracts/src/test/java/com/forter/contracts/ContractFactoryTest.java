package com.forter.contracts;

import com.forter.contracts.mocks.MockInput;
import com.google.common.base.Optional;
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

    @Test
    public void testPrivates() {
        ContractFactory<MockInputWithNonPublic> f = new ContractFactory(MockInputWithNonPublic.class);
        MockInputWithNonPublic input = f.newInstance();
        assertThat(input.x).isNull();
        assertThat(input.y.isPresent()).isFalse();
        assertThat(input.z).isNull();
        assertThat(input.a.isPresent()).isFalse();
    }
}
