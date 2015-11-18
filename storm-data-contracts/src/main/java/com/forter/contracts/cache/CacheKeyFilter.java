package com.forter.contracts.cache;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.forter.contracts.ContractConverter;
import com.google.common.base.Throwables;

import java.lang.reflect.Field;
import java.util.*;

/**
 * Creates cache key using input contract fields annotated with {@link CacheKey}.
 */
public class CacheKeyFilter {

    private Set<String> cachedKeys = new HashSet<>();
    private Map<String, List<CacheKeyTransformer>> cachedKeysTransformers = new HashMap<>();
    private ContractConverter converter = ContractConverter.instance();

    public CacheKeyFilter(Class<?> cachedClass) {
        for (Field field : cachedClass.getDeclaredFields()) {
            if (field.isAnnotationPresent(CacheKey.class)) {
                CacheKey keyAnnotation = field.getAnnotation(CacheKey.class);
                cachedKeys.add(field.getName());
                if (keyAnnotation.transformers().length > 0) {
                    List<CacheKeyTransformer> transformers = new ArrayList<>(keyAnnotation.transformers().length);
                    for (Class<? extends CacheKeyTransformer> transformer : keyAnnotation.transformers()) {
                        try {
                            transformers.add(transformer.newInstance());
                        } catch (Exception e) {
                            Throwables.propagate(e);
                        }
                    }
                    cachedKeysTransformers.put(field.getName(), transformers);
                }
            }
        }
    }

    public Map<String, Object> createKey(ObjectNode input) {
        //return null if no CacheKey is present
        if(this.cachedKeys.size() == 0) {
            return new HashMap<>();
        }

        Map<String, Object> filteredInput = ContractConverter.instance().convertObjectNodeToMap(input);
        return createKeyFromMap(filteredInput);
    }

    public Map<String, Object> createKey(Object input) {
        //return null if no CacheKey is present
        if(this.cachedKeys.size() == 0) {
            return new HashMap<>();
        }

        Map<String, Object> filteredInput = new HashMap<>(converter.convertContractToMap(input));
        return createKeyFromMap(filteredInput);
    }

    private Map<String, Object> createKeyFromMap(Map<String, Object> filteredInput) {
        Iterator<String> fieldsIterator = filteredInput.keySet().iterator();
        while (fieldsIterator.hasNext()) {
            String field = fieldsIterator.next();
            if (!cachedKeys.contains(field)) {
                fieldsIterator.remove();
            } else {
                if (cachedKeysTransformers.containsKey(field)) {
                    Object fieldValue = filteredInput.get(field);
                    for (CacheKeyTransformer transformer : cachedKeysTransformers.get(field)) {
                        fieldValue = transformer.transform(fieldValue);
                    }
                    filteredInput.put(field, fieldValue);
                }
            }
        }
        return filteredInput;
    }

}
