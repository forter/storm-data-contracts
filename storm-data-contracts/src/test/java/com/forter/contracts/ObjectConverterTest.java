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
        MockInput somePOJO = new MockInput();
        somePOJO.y = Optional.absent();
        assertThat(somePOJO.x).isNull();
        ContractConverter.instance().updateObjectNodeToContract(node, somePOJO);
        assertThat(somePOJO.x).isEqualTo(-1);
        assertThat(somePOJO.y.isPresent()).isFalse();
    }
}
