package com.forter.contracts.mocks;

import backtype.storm.task.TopologyContext;
import com.forter.contracts.BaseContractsBoltExecutor;
import com.forter.contracts.IContractsBolt;
import com.forter.contracts.cache.CacheDAO;
import com.google.common.base.Optional;

import java.util.Map;

public class MockCachedContractBoltExecutor extends BaseContractsBoltExecutor {

    public static MockCacheDAO cache;

    public MockCachedContractBoltExecutor(IContractsBolt contractsBolt) {
        super(contractsBolt);
    }

    @Override
    protected Optional<? extends CacheDAO> prepareCacheDAO(Map stormConf, TopologyContext context) {
        return Optional.of(cache);
    }
}
