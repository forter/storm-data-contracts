package com.forter.contracts;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.forter.contracts.mocks.MockInput;
import com.google.common.base.Optional;
import org.testng.annotations.Test;

import java.io.IOException;

import static org.fest.assertions.api.Assertions.assertThat;

/**
 * Unit Tests for {@link ContractConverter}
 */
public class ObjectConverterTest {

    ObjectMapper mapper = new ObjectMapper();

    @Test
    public void testJsonNodeToObjectMissingOptionalValue() throws IOException {
        ObjectNode node = (ObjectNode) mapper.readTree("{\"x\":\"-1\"}");
        ContractFactory<MockInput> factory = new ContractFactory(MockInput.class);
        MockInput input = ContractConverter.instance().convertObjectNodeToContract(node, factory);
        assertThat(input.x).isEqualTo(-1);
        assertThat(input.y.isPresent()).isFalse();
    }
}
