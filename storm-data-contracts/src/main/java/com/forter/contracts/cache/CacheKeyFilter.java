package com.forter.contracts.cache;

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

    public Map<String, Object> createKey(Object input) {
        Map<String, Object> filteredInput = new HashMap<>(converter.convertContractToMap(input));
        Iterator<String> fieldsIterator = filteredInput.keySet().iterator();
        while (fieldsIterator.hasNext()) {
            if (!cachedKeys.contains(fieldsIterator.next())) {
                fieldsIterator.remove();
            }
        }
        return filteredInput;
    }

}
