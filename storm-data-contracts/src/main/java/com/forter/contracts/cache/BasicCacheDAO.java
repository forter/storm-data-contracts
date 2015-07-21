package com.forter.contracts.cache;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.forter.contracts.ContractConverter;
import com.forter.contracts.ContractFactory;
import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.base.Throwables;
import com.google.common.hash.Hashing;

import java.lang.reflect.Field;
import java.util.*;

public abstract class BasicCacheDAO<TInput, TOutput> implements CacheDAO<TInput, TOutput> {

    public static final String RESULT_FIELD = "result";

    protected ObjectMapper mapper;
    protected ContractFactory<TOutput> outputFactory;
    protected ContractConverter converter;

    public BasicCacheDAO(Class<TOutput> outputClass) {
        this.mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS, true);
        outputFactory = new ContractFactory<>(outputClass);
        converter = ContractConverter.instance();
    }

    @Override
    public Optional<TOutput> get(TInput input) {
        String key = createKey(input);
        ObjectNode cachedData = fetch(key);
        if (cachedData == null) {
            return Optional.absent();
        }
        return Optional.of(createValue((ObjectNode) cachedData.get(RESULT_FIELD)));
    }

    @Override
    public void save(TOutput data, TInput input, long startTime) {
        String key = createKey(input);
        ObjectNode parsedData = converter.convertContractToObjectNode(data);
        ObjectNode record = createRecord(parsedData, startTime);
        persist(key, record);
    }

    public String createKey(Object input) {
        Preconditions.checkState(input.getClass().isAnnotationPresent(Cached.class));
        String type = input.getClass().getAnnotation(Cached.class).value();
        Set<String> keyFields = new HashSet<>();
        for (Field field : input.getClass().getDeclaredFields()) {
            if (field.isAnnotationPresent(CacheKey.class)) {
                keyFields.add(field.getName());
            }
        }
        Map<String, Object> key = new HashMap<>(converter.convertContractToMap(input));
        Iterator<String> fieldsIterator = key.keySet().iterator();
        while (fieldsIterator.hasNext()) {
            if (!keyFields.contains(fieldsIterator.next())) {
                fieldsIterator.remove();
            }
        }
        String keyString = null;
        try {
            keyString = mapper.writeValueAsString(key);
        } catch (JsonProcessingException e) {
            Throwables.propagate(e);
        }
        return type + "-" + Hashing.sha256().hashBytes(keyString.getBytes()).toString();
    }

    protected abstract ObjectNode fetch(String key);

    protected abstract void persist(String key, ObjectNode data);

    private ObjectNode createRecord(ObjectNode data, long startTime) {
        ObjectNode record = mapper.createObjectNode();
        record.set("result", data);
        addRecordMetadata(startTime, record);
        return record;
    }

    private void addRecordMetadata(long startTime, ObjectNode record) {
        record.put("startTime", startTime);
        record.put("duration", System.currentTimeMillis() - startTime);
        record.put("source", "json");
    }

    private TOutput createValue(ObjectNode data) {
        try {
            return converter.convertObjectNodeToContract(data, outputFactory);
        } catch (JsonProcessingException e) {
            Throwables.propagate(e);
        }
        return null;
    }

}
