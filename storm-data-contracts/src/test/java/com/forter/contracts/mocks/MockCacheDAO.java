package com.forter.contracts.mocks;

import com.forter.contracts.cache.CacheDAO;
import com.google.common.base.Optional;

import java.util.HashMap;
import java.util.Map;

/**
 * Mock {@link com.forter.contracts.cache.CacheDAO}.
 */
public class MockCacheDAO implements CacheDAO<MockContractsBoltInput, MockContractsBoltOutput> {

    public Map<MockContractsBoltInput, MockContractsBoltOutput> cache = new HashMap<>();

    @Override
    public Optional<MockContractsBoltOutput> get(MockContractsBoltInput input) {
        if (cache.containsKey(input)) {
            return Optional.of(cache.get(input));
        }
        return Optional.absent();
    }

    @Override
    public void save(MockContractsBoltOutput output, MockContractsBoltInput input, long startTimeMillis) {
        cache.put(input, output);
    }
}
