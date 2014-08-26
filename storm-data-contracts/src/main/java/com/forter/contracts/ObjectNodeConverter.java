package com.forter.contracts;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.datatype.guava.GuavaModule;
import com.google.common.base.Throwables;

import java.util.Map;

/**
 * Converts {@link com.fasterxml.jackson.databind.node.ObjectNode} to a Contract
 */
public class ObjectNodeConverter {

    private static class LazyHolder {
        private static final ObjectNodeConverter INSTANCE = new ObjectNodeConverter();
    }

    public static ObjectNodeConverter instance() {
        return LazyHolder.INSTANCE;
    }

    private final ObjectMapper mapper;

    private ObjectNodeConverter() {
        mapper = new ObjectMapper();
        mapper.setVisibilityChecker(mapper.getSerializationConfig().getDefaultVisibilityChecker()
                .withFieldVisibility(JsonAutoDetect.Visibility.ANY)
                .withGetterVisibility(JsonAutoDetect.Visibility.NONE)
                .withSetterVisibility(JsonAutoDetect.Visibility.NONE)
                .withCreatorVisibility(JsonAutoDetect.Visibility.NONE));
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

        mapper.registerModule(new GuavaModule());
    }

    public <T> T updateObjectNodeToContract(ObjectNode node, T defaultValues) {
        try {
            return mapper
                    .readerForUpdating(defaultValues)
                    .treeToValue(node, (Class<? extends T>) defaultValues.getClass());
        } catch (JsonProcessingException e) {
            throw Throwables.propagate(e);
        }
    }

    public <T> T updateContractToContract(Object contract, T defaultValues) {
        ObjectNode node = convertContractToObjectNode(contract);
        return updateObjectNodeToContract(node, defaultValues);
    }

    public ObjectNode convertContractToObjectNode(Object contract) {
        return mapper.valueToTree(contract);
    }

    public Map convertContractToMap(Object contract) {
        return mapper.convertValue(contract, Map.class);
    }
}
