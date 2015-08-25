package com.forter.contracts.mocks;

import backtype.storm.task.TopologyContext;
import com.forter.contracts.IContractsBolt;
import java.util.Map;

public class MockAssertionBolt implements IContractsBolt<MockAssertionContract, MockAssertionContract> {

    @Override
    public void prepare(Map stormConf, TopologyContext context) {

    }

    @Override
    public MockAssertionContract execute(MockAssertionContract assertionContract) {
        return null;
    }

    @Override
    public void cleanup() {

    }

    @Override
    public Map<String, Object> getComponentConfiguration() {
        return null;
    }

    @Override
    public MockAssertionContract createDefaultOutput() {
        return null;
    }
}

