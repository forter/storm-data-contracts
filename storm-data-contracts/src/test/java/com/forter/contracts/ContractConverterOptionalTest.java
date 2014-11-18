package com.forter.contracts;

import static org.fest.assertions.api.Assertions.assertThat;

import com.forter.contracts.mocks.MockOptionalIntegerInput;

import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.base.Optional;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.IOException;

/**
 * Tests for {@link com.forter.contracts.ContractConverter}
 */
public class ContractConverterOptionalTest {

    private ObjectMapper objectMapper;
    private ContractFactory<MockOptionalIntegerInput> contractFactory;
    private ContractConverter contractConverter;

    @BeforeClass
    public void setUpAllTests() {
        objectMapper = new ObjectMapper();
        contractFactory = new ContractFactory<>(MockOptionalIntegerInput.class);
    }

    @BeforeMethod
    public void setUp() throws Exception {
        contractConverter = ContractConverter.instance();
    }

    @Test
    public void testConvertInteger() throws IOException {
        TreeNode node = objectMapper.readTree("{\"optionalInteger\":123}");
        MockOptionalIntegerInput input =
                contractConverter.convertObjectNodeToContract((ObjectNode) node, contractFactory);
        assertThat(input.optionalInteger).isEqualTo(Optional.of(123));
    }

    @Test
    public void testConvertIntegerString() throws IOException {
        TreeNode node = objectMapper.readTree("{\"optionalInteger\":\"123\"}");
        MockOptionalIntegerInput input =
                contractConverter.convertObjectNodeToContract((ObjectNode) node, contractFactory);
        assertThat(input.optionalInteger).isEqualTo(Optional.of(123));
    }

    @Test
    public void testConvertNullString() throws IOException {
        TreeNode node = objectMapper.readTree("{\"optionalInteger\":\"null\"}");
        MockOptionalIntegerInput input =
                contractConverter.convertObjectNodeToContract((ObjectNode) node, contractFactory);
        assertThat(input.optionalInteger).isEqualTo(Optional.<Integer>absent());
    }

    @Test
    public void testConvertEmptyString() throws IOException {
        TreeNode node = objectMapper.readTree("{\"optionalInteger\":\"\"}");
        MockOptionalIntegerInput input =
                contractConverter.convertObjectNodeToContract((ObjectNode) node, contractFactory);
        assertThat(input.optionalInteger).isEqualTo(Optional.<Integer>absent());
    }

    @Test
    public void testConvertNull() throws IOException {
        TreeNode node = objectMapper.readTree("{\"optionalInteger\":null}");
        MockOptionalIntegerInput input =
                contractConverter.convertObjectNodeToContract((ObjectNode) node, contractFactory);
        assertThat(input.optionalInteger).isEqualTo(Optional.<Integer>absent());
    }
}
