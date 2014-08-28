package com.forter.contracts.mocks;

import backtype.storm.task.TopologyContext;
import com.forter.contracts.IContractsBolt;
import com.forter.contracts.validation.ContractValidationResult;

import java.util.Map;

/**
 * Mocks {@link com.forter.contracts.IContractsBolt}
 */
public class MockNullContractsBolt implements IContractsBolt<MockContractsBoltInput,MockContractsBoltOutput> {

    @Override
    public void prepare(Map stormConf, TopologyContext context) {

    }

    @Override
    public MockContractsBoltOutput executeValidInput(MockContractsBoltInput input) {
        return null; //illegal output
    }

    @Override
    public MockContractsBoltOutput executeInvalidInput(MockContractsBoltInput input, ContractValidationResult violations) {
        throw new UnsupportedOperationException("not implemented in mock");
    }

    @Override
    public void cleanup() {

    }

    @Override
    public Map<String, Object> getComponentConfiguration() {
        return null;
    }
}
