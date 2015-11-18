package com.forter.contracts.cache;

/**
 * Transforms a cache key value (for example: lowercase for strings so the cache will be case insensitive).
 */
public interface CacheKeyTransformer {

    public Object transform(Object key);
}
