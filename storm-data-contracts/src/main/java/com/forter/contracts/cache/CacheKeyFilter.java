package com.forter.contracts.cache;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.forter.contracts.ContractConverter;
import java.lang.reflect.Field;
import java.util.*;

/**
 * Creates cache key using input contract fields annotated with {@link CacheKey}.
 */
public class CacheKeyFilter {

    private Set<String> cachedKeys = new HashSet<>();
    private ContractConverter converter = ContractConverter.instance();

    public CacheKeyFilter(Class<?> cachedClass) {
        for (Field field : cachedClass.getDeclaredFields()) {
            if (field.isAnnotationPresent(CacheKey.class)) {
                cachedKeys.add(field.getName());
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
            if (!cachedKeys.contains(fieldsIterator.next())) {
                fieldsIterator.remove();
            }
        }
        return filteredInput;
    }


}
