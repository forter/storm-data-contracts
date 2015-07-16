package com.forter.contracts.mocks;

import backtype.storm.task.TopologyContext;
import backtype.storm.tuple.Tuple;
import com.forter.contracts.IContractsBolt;
import com.google.common.base.Optional;

import java.util.Map;

/**
 * Mocks {@link com.forter.contracts.IContractsBolt}
 */
public class MockContractsBolt implements IContractsBolt<MockContractsBoltInput,MockContractsBoltOutput> {

    @Override
    public void prepare(Map stormConf, TopologyContext context) {

    }

    @Override
    public MockContractsBoltOutput execute(MockContractsBoltInput input) {
        final MockContractsBoltOutput output = new MockContractsBoltOutput();
        output.output1 = input.input1;
        output.optionalOutput2 = input.optionalInput2;
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
        output.output1= 0;
        output.optionalOutput2 = Optional.absent();
        return output;
    }

    @Override
    public Tuple getCurrentTuple() {
        return null;
    }

    @Override
    public void setCurrentTuple(Tuple tuple) {

    }
}
