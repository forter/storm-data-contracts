package com.forter.contracts.cache;

import com.google.common.base.Optional;

import java.util.Map;

/**
 * DAO accessing cache for external service results.
 */
public interface CacheDAO<TOutput> {

    Optional<TOutput> get(Map<String, Object> key);

    void save(TOutput record, Map<String, Object> inputKey, long startTimeMillis);
}
