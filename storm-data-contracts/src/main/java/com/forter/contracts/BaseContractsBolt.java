package com.forter.contracts;

import org.apache.storm.task.TopologyContext;

import java.util.Map;

/**
 * A Default empty implementation of {@link com.forter.contracts.IContractsBolt} methods for convenient implementation.
 */
public abstract class BaseContractsBolt<TInput, TOutput> implements IContractsBolt<TInput, TOutput> {
    @Override
    public void prepare(Map stormConf, TopologyContext context) {}

    @Override
    public void cleanup() {}

    @Override
    public Map<String, Object> getComponentConfiguration() {
        return null;
    }
}
