package com.forter.contracts.cache;

import com.google.common.base.Optional;

/**
 * DAO accessing cache for external service results.
 */
public interface CacheDAO<TInput, TOutput> {

    public Optional<TOutput> get(TInput key);

    public void save(TOutput record, TInput input, long startTimeMillis);

}
