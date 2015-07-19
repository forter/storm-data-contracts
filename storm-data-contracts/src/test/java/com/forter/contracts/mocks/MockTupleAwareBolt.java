package com.forter.contracts.mocks;

import backtype.storm.task.TopologyContext;
import backtype.storm.tuple.Tuple;
import com.forter.contracts.IContractsBolt;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Optional;

import java.util.Map;

/**
 * A mock for testing tuple awareness of {@link com.forter.contracts.IContractsBolt}
 */
public class MockTupleAwareBolt implements IContractsBolt<MockContractsBoltInput, MockContractsBoltOutput> {

    @VisibleForTesting Tuple currentTuple;

    @Override
    public void prepare(Map stormConf, TopologyContext context) {

    }

    @Override
    public MockContractsBoltOutput execute(MockContractsBoltInput mockContractsBoltInput) {
        MockContractsBoltOutput output = new MockContractsBoltOutput();
        output.output1 = 1;
        output.optionalOutput2 = Optional.absent();
        return output;
    }

    @Override
    public void cleanup() {

    }

    @Override
    public Map<String, Object> getComponentConfiguration() {
        return null;
    }

    @Override
    public MockContractsBoltOutput createDefaultOutput() {
        MockContractsBoltOutput output = new MockContractsBoltOutput();
        output.output1 = 1;
        output.optionalOutput2 = Optional.absent();
        return output;
    }

    @Override
    public Tuple getCurrentTuple() {
        return currentTuple;
    }

    @Override
    public void setCurrentTuple(Tuple tuple) {
        this.currentTuple = tuple;
    }
}
