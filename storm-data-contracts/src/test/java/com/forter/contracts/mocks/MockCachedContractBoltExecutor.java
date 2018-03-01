package com.forter.contracts.mocks;

import com.forter.contracts.BaseContractsBoltExecutor;
import com.forter.contracts.IContractsBolt;
import com.forter.contracts.cache.CacheDAO;
import org.apache.storm.task.TopologyContext;

import java.util.Map;

public class MockCachedContractBoltExecutor extends BaseContractsBoltExecutor {

    public MockCacheDAO cache;

    public MockCachedContractBoltExecutor(IContractsBolt contractsBolt, MockCacheDAO cache) {
        super(contractsBolt);
        this.cache = cache;
    }

    @Override
    protected CacheDAO createCacheDAO(Map stormConf, TopologyContext context) {
        return cache;
    }
}
