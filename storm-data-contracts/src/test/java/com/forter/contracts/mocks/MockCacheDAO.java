package com.forter.contracts.mocks;

import com.forter.contracts.cache.CacheDAO;
import com.google.common.base.Optional;

import java.util.HashMap;
import java.util.Map;

/**
 * Mock {@link com.forter.contracts.cache.CacheDAO}.
 */
public class MockCacheDAO implements CacheDAO<MockContractsBoltOutput> {

    public Map<Map<String, Object>, MockContractsBoltOutput> cache = new HashMap<>();

    @Override
    public Optional<MockContractsBoltOutput> get(Map<String, Object> key) {
        if (cache.containsKey(key)) {
            return Optional.of(cache.get(key));
        }
        return Optional.absent();
    }

    @Override
    public void save(MockContractsBoltOutput output, Map<String, Object> inputKey, long startTimeMillis) {
        cache.put(inputKey, output);
    }
}
