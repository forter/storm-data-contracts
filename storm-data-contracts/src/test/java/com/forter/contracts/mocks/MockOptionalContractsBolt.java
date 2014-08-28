package com.forter.contracts.mocks;

import backtype.storm.task.TopologyContext;
import com.forter.contracts.IContractsBolt;
import com.forter.contracts.validation.ContractValidationResult;
import com.google.common.base.Optional;

import java.util.Map;

/**
 * Mocks {@link com.forter.contracts.IContractsBolt}
 */
public class MockOptionalContractsBolt implements IContractsBolt<MockContractsBoltInput,Optional<MockContractsBoltOutput>> {

    @Override
    public void prepare(Map stormConf, TopologyContext context) {

    }

    @Override
    public Optional<MockContractsBoltOutput> executeValidInput(MockContractsBoltInput input) {
        final MockContractsBoltOutput output = new MockContractsBoltOutput();
        output.output1 = input.input1;
        output.optionalOutput2 = input.optionalInput2;
        return Optional.of(output);
    }

    @Override
    public Optional<MockContractsBoltOutput> executeInvalidInput(MockContractsBoltInput input, ContractValidationResult violations) {
        final MockContractsBoltOutput output = new MockContractsBoltOutput();
        output.output1 = 0;
        output.optionalOutput2 = Optional.absent();
        return Optional.of(output);
    }

    @Override
    public void cleanup() {

    }

    @Override
    public Map<String, Object> getComponentConfiguration() {
        return null;
    }
}
