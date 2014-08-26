package com.forter.contracts.mocks;

import backtype.storm.task.TopologyContext;
import com.forter.contracts.IContractsBolt;
import com.forter.contracts.validation.ContractValidationResult;

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
    public MockContractsBoltOutput executeValidInput(MockContractsBoltInput input) {
        return output;
    }

    @Override
    public MockContractsBoltOutput executeInvalidInput(MockContractsBoltInput input, ContractValidationResult violations) {
        throw new UnsupportedOperationException("not implemented in mock");
    }

    @Override
    public void cleanup() {

    }
}
