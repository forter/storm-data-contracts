package com.forter.contracts;

import backtype.storm.task.TopologyContext;
import backtype.storm.tuple.Tuple;

import java.util.Map;

/**
 * A Default empty implementation of {@link com.forter.contracts.IContractsBolt} methods for convenient implementation.
 */
public abstract class BaseContractsBolt<TInput, TOutput> implements IContractsBolt<TInput, TOutput> {
    private Tuple currentTuple;

    @Override
    public void prepare(Map stormConf, TopologyContext context) {}

    @Override
    public void cleanup() {}

    @Override
    public Map<String, Object> getComponentConfiguration() {
        return null;
    }

    @Override
    public Tuple getCurrentTuple() {
        return currentTuple;
    }

    @Override
    public void setCurrentTuple(Tuple tuple) {
        currentTuple = tuple;
    }
}
