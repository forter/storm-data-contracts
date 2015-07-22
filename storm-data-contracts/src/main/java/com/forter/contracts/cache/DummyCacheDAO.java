package com.forter.contracts.cache;

import com.google.common.base.Optional;

/**
 * Dummy {@link com.forter.contracts.cache.CacheDAO} that doesn't really do anything.
 */
public class DummyCacheDAO<TInput, TOutput> implements CacheDAO<TInput, TOutput> {

    @Override
    public Optional<TOutput> get(TInput key) {
        return Optional.absent();
    }

    @Override
    public void save(TOutput record, TInput input, long startTimeMillis) {}
}
