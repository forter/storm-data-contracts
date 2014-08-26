package com.forter.contracts;

import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.IRichBolt;
import backtype.storm.tuple.Tuple;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.forter.contracts.mocks.MockContractsBolt;
import com.forter.contracts.mocks.MockContractsBoltInput;
import com.forter.contracts.mocks.MockContractsBoltOutput;
import com.forter.contracts.validation.ValidContract;
import com.google.common.base.Optional;
import org.mockito.ArgumentCaptor;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.List;
import java.util.Map;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

/**
 * Unit Tests for {@link BaseContractsBoltExecutor}
 */
public class ToSomethingContractsBoltTest {

    final String id = "1";
    MockContractsBoltInput input;
    IContractsBolt contractsBolt;
    @BeforeClass
    public void before() {
        input = new MockContractsBoltInput();
        input.input1 = -1;
        input.optionalInput2 = Optional.of(-1);
        contractsBolt = new MockContractsBolt(true);
    }
    @Test
    public void testToMapContractsBolt() {
        OutputCollector collector = execute(input, new ToMapContractsBoltExecutor(contractsBolt));
        List<Object> output = captureOutput(collector);
        Map outputMap = (Map) output.get(1);
        assertThat(outputMap.get("output1")).isEqualTo(-1);
        assertThat(outputMap.get("optionalOutput2")).isEqualTo(-1);
    }

    @Test
    public void testToObjectNodeContractsBolt() {
        OutputCollector collector = execute(input, new ToObjectNodeContractsBoltExecutor(contractsBolt));
        List<Object> output = captureOutput(collector);
        ObjectNode outputMap = (ObjectNode) output.get(1);
        assertThat(outputMap.get("output1").asInt()).isEqualTo(-1);
        assertThat(outputMap.get("optionalOutput2").asInt()).isEqualTo(-1);
    }

    @Test
    public void testToValidContractContractsBolt() {
        OutputCollector collector = execute(input, new ToValidContractContractsBoltExecutor(contractsBolt));
        List<Object> output = captureOutput(collector);
        MockContractsBoltOutput outputContract = ((ValidContract<MockContractsBoltOutput>) output.get(1)).getContract();
        assertThat(outputContract.output1).isEqualTo(-1);
        assertThat(outputContract.optionalOutput2).isEqualTo(Optional.of(-1));
    }

    private OutputCollector execute(MockContractsBoltInput input, IRichBolt bolt) {
        final OutputCollector collector = mock(OutputCollector.class);
        bolt.prepare(mock(Map.class), mock(TopologyContext.class), collector);
        Tuple tuple = mock(Tuple.class);
        when(tuple.getValue(0)).thenReturn(id);
        when(tuple.getValue(1)).thenReturn(input);
        bolt.execute(tuple);
        return collector;
    }

    private List<Object> captureOutput(OutputCollector collector) {
        ArgumentCaptor<List> actualOutput = ArgumentCaptor.forClass(List.class);
        verify(collector).emit((String)any(), (Tuple)any(), actualOutput.capture());
        return (List<Object>) actualOutput.getValue();
    }
}
