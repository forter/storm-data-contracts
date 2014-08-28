package com.forter.contracts.mocks;

import backtype.storm.task.TopologyContext;
import com.forter.contracts.IContractsBolt;
import com.forter.contracts.validation.ContractValidationResult;
import com.google.common.base.Optional;
import com.google.common.collect.Lists;

import java.util.Collection;
import java.util.Map;

/**
 * Mocks {@link com.forter.contracts.IContractsBolt}
 */
public class MockCollectionContractsBolt implements IContractsBolt<MockContractsBoltInput,Collection<MockContractsBoltOutput>> {

    private final MockContractsBoltOutput output;

    public MockCollectionContractsBolt() {
        output = new MockContractsBoltOutput();
        output.output1 =1;
        output.optionalOutput2 = Optional.absent();
    }

    @Override
    public void prepare(Map stormConf, TopologyContext context) {

    }

    @Override
    public Collection<MockContractsBoltOutput> executeValidInput(MockContractsBoltInput input) {
        return Lists.newArrayList(output,output);
    }

    @Override
    public Collection<MockContractsBoltOutput> executeInvalidInput(MockContractsBoltInput input, ContractValidationResult violations) {
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
