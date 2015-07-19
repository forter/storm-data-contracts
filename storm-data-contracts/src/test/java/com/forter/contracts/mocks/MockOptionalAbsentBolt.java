package com.forter.contracts.mocks;

import backtype.storm.task.TopologyContext;
import backtype.storm.tuple.Tuple;
import com.forter.contracts.IContractsBolt;
import com.google.common.base.Optional;

import java.util.Map;

/**
 * Mocks {@link com.forter.contracts.IContractsBolt}
 */
public class MockOptionalAbsentBolt implements IContractsBolt<MockContractsBoltInput,Optional<MockContractsBoltOutput>> {

    @Override
    public void prepare(Map stormConf, TopologyContext context) {

    }

    @Override
    public Optional<MockContractsBoltOutput> execute(MockContractsBoltInput input) {
        return Optional.absent();
    }

    @Override
    public void cleanup() {

    }

    @Override
    public Map<String, Object> getComponentConfiguration() {
        return null;
    }

    @Override
    public Optional<MockContractsBoltOutput> createDefaultOutput() {
        return Optional.absent();
    }

    @Override
    public Tuple getCurrentTuple() {
        return null;
    }

    @Override
    public void setCurrentTuple(Tuple tuple) {

    }
}
