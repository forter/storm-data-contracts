package com.forter.contracts;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.datatype.guava.GuavaModule;
import com.forter.contracts.validation.ValidContract;
import com.google.common.base.Throwables;

import java.util.Map;

/**
 * Converts {@link com.fasterxml.jackson.databind.node.ObjectNode} to a Contract
 */
public class ContractConverter {

    private static class LazyHolder {
        private static final ContractConverter INSTANCE = new ContractConverter();
    }

    public static ContractConverter instance() {
        return LazyHolder.INSTANCE;
    }

    private final ObjectMapper mapper;

    private ContractConverter() {
        mapper = new ObjectMapper();
        mapper.setVisibilityChecker(mapper.getSerializationConfig().getDefaultVisibilityChecker()
                .withFieldVisibility(JsonAutoDetect.Visibility.ANY)
                .withGetterVisibility(JsonAutoDetect.Visibility.NONE)
                .withSetterVisibility(JsonAutoDetect.Visibility.NONE)
                .withCreatorVisibility(JsonAutoDetect.Visibility.NONE));
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

        mapper.registerModule(new GuavaModule());
    }

    public <T> T convertObjectNodeToContract(ObjectNode node, ContractFactory<T> factory) {
        return updateObjectNodeToContract(node,factory.newInstance());
    }

    private <T> T updateObjectNodeToContract(ObjectNode node, T defaultValues) {
        try {
            return mapper
                    .readerForUpdating(defaultValues)
                    .treeToValue(node, (Class<? extends T>) defaultValues.getClass());
        } catch (JsonProcessingException e) {
            throw Throwables.propagate(e);
        }
    }

    public <T> T convertContractToContract(Object contract, ContractFactory<T> factory) {
        return updateContractToContract(contract, factory.newInstance());
    }

    private <T> T updateContractToContract(Object contract, T defaultValues) {
        ObjectNode node = convertContractToObjectNode(contract);
        return updateObjectNodeToContract(node, defaultValues);
    }

    public ObjectNode convertContractToObjectNode(Object contract) {
        if (contract instanceof ValidContract) {
            contract = ((ValidContract) contract).getContract();
        }
        return mapper.valueToTree(contract);
    }

    public Map convertContractToMap(Object contract) {
        if (contract instanceof ValidContract) {
            contract = ((ValidContract) contract).getContract();
        }
        return mapper.convertValue(contract, Map.class);
    }

    public String convertContractToJsonString(Object contract) {
        try {
            return mapper.writeValueAsString(contract);
        } catch (JsonProcessingException e) {
            throw Throwables.propagate(e);
        }
    }

}
