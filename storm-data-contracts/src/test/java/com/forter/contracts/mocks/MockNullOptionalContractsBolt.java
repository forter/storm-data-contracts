package com.forter.contracts.mocks;

import backtype.storm.task.TopologyContext;
import com.forter.contracts.IContractsBolt;
import com.google.common.base.Optional;

import java.util.Map;

/**
 * Mocks {@link com.forter.contracts.IContractsBolt}
 */
public class MockNullOptionalContractsBolt implements IContractsBolt<MockContractsBoltInput,MockContractsBoltOutput> {

    private final MockContractsBoltOutput output;

    public MockNullOptionalContractsBolt() {
        output = new MockContractsBoltOutput();
        output.output1 =1;
        output.optionalOutput2 = null; //illegal
    }

    @Override
    public void prepare(Map stormConf, TopologyContext context) {

    }

    @Override
    public MockContractsBoltOutput execute(MockContractsBoltInput input) {
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
        MockContractsBoltOutput defaultOutput = new MockContractsBoltOutput();
        defaultOutput.output1 =1;
        defaultOutput.optionalOutput2 = Optional.absent();
        return defaultOutput;
    }
}
