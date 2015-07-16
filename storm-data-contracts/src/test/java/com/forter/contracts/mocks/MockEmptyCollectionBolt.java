package com.forter.contracts.mocks;

import backtype.storm.task.TopologyContext;
import backtype.storm.tuple.Tuple;
import com.forter.contracts.IContractsBolt;
import com.google.common.collect.Lists;

import java.util.Collection;
import java.util.Map;

/**
 * Mocks {@link com.forter.contracts.IContractsBolt}
 */
public class MockEmptyCollectionBolt implements IContractsBolt<MockContractsBoltInput,Collection<MockContractsBoltOutput>> {

    @Override
    public void prepare(Map stormConf, TopologyContext context) {

    }

    @Override
    public Collection<MockContractsBoltOutput> execute(MockContractsBoltInput input) {
        return Lists.newArrayList();
    }

    @Override
    public void cleanup() {

    }

    @Override
    public Map<String, Object> getComponentConfiguration() {
        return null;
    }

    @Override
    public Collection<MockContractsBoltOutput> createDefaultOutput() {
        return Lists.newArrayList();
    }

    @Override
    public Tuple getCurrentTuple() {
        return null;
    }

    @Override
    public void setCurrentTuple(Tuple tuple) {

    }
}
