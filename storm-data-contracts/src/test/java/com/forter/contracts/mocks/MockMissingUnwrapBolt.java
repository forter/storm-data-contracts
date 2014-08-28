package com.forter.contracts.mocks;

import backtype.storm.task.TopologyContext;
import com.forter.contracts.IContractsBolt;
import com.forter.contracts.validation.ContractValidationResult;

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
    public MockContractsBoltOutput executeValidInput(MockMissingUnwrapInput input) {
        throw new UnsupportedOperationException("not implemented in mock");
    }

    @Override
    public MockContractsBoltOutput executeInvalidInput(MockMissingUnwrapInput input, ContractValidationResult violations) {
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
}
