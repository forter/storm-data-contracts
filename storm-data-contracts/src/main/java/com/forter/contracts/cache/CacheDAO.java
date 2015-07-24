package com.forter.contracts.cache;

import com.google.common.base.Optional;

import java.util.Map;

/**
 * DAO accessing cache for external service results.
 */
public interface CacheDAO<TOutput> {

    public Optional<TOutput> get(Map<String, Object> key);

    public void save(TOutput record, Map<String, Object> inputKey, long startTimeMillis);

}
