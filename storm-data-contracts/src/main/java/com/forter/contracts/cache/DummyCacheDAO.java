package com.forter.contracts.cache;

import com.google.common.base.Optional;

import java.util.Map;

/**
 * Dummy {@link com.forter.contracts.cache.CacheDAO} that doesn't really do anything.
 */
public class DummyCacheDAO<TOutput> implements CacheDAO<TOutput> {

    @Override
    public Optional<TOutput> get(Map<String, Object> key) {
        return Optional.absent();
    }

    @Override
    public void save(TOutput record, Map<String, Object> inputKey, long startTimeMillis) {}
}
