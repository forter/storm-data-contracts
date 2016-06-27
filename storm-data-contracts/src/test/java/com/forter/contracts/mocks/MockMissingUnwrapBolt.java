package com.forter.contracts.mocks;

import org.apache.storm.task.TopologyContext;
import com.forter.contracts.IContractsBolt;
import com.google.common.base.Optional;

import java.util.Map;


/**
 * Mocks {@link com.forter.contracts.IContractsBolt}
 */
public class MockMissingUnwrapBolt implements IContractsBolt<MockMissingUnwrapInput,MockContractsBoltOutput> {

    @Override
    public void prepare(Map stormConf, TopologyContext context) {
        throw new UnsupportedOperationException("not implemented in mock");
    }

    @Override
    public MockContractsBoltOutput execute(MockMissingUnwrapInput input) {
        throw new UnsupportedOperationException("not implemented in mock");
    }
    @Override
    public void cleanup() {
        throw new UnsupportedOperationException("not implemented in mock");
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
}
