package com.forter.contracts.mocks;

import backtype.storm.task.TopologyContext;
import com.forter.contracts.IContractsBolt;
import com.forter.contracts.validation.ContractValidationResult;

import java.util.Map;

/**
 * Mocks {@link com.forter.contracts.IContractsBolt}
 */
public class MockContractsBolt implements IContractsBolt<MockContractsBoltInput,MockContractsBoltOutput> {

    private final boolean emit;

    public MockContractsBolt(boolean emit) {
        this.emit = emit;
    }

    @Override
    public void prepare(Map stormConf, TopologyContext context) {

    }

    @Override
    public MockContractsBoltOutput executeValidInput(MockContractsBoltInput input) {
        if (!emit) {
            return null; //illegal output
        }
        final MockContractsBoltOutput output = new MockContractsBoltOutput();
        output.output1 = input.input1;
        output.optionalOutput2 = input.optionalInput2;
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
