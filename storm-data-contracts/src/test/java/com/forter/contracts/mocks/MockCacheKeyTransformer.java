package com.forter.contracts.mocks;

import com.forter.contracts.cache.CacheKeyTransformer;

public class MockCacheKeyTransformer implements CacheKeyTransformer {
    @Override
    public Object transform(Object key) {
        return (int) key + 1;
    }
}
