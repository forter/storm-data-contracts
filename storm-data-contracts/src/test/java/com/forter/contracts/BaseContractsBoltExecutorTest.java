package com.forter.contracts;

import backtype.storm.task.TopologyContext;
import backtype.storm.task.OutputCollector;
import backtype.storm.topology.ReportedFailedException;
import backtype.storm.tuple.Tuple;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.forter.contracts.mocks.*;
import com.google.common.base.Optional;
import com.google.common.base.Throwables;
import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.mockito.ArgumentCaptor;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

/**
 * Unit Tests for {@link BaseContractsBoltExecutor}
 */
public class BaseContractsBoltExecutorTest {

    ObjectMapper mapper = new ObjectMapper();
    final String id = "1";


    @Test
    public void testContractsBolt() {
        //mock copies input to output
        ObjectNode data = parseJson("{\"input1\":-1,\"optionalInput2\":-1}");
        MockContractsBoltOutput output = new MockContractsBoltOutput();
        output.output1 = -1;
        output.optionalOutput2 = Optional.of(-1);
        IContractsBolt contractsBolt = new MockContractsBolt(true);
        OutputCollector collector = execute(data, contractsBolt);
        assertEmitEquals(collector, output);
    }

    @Test
    public void testCollectionContractBolt() {
        //mock copies input to output twice
        ObjectNode data = parseJson("{\"input1\":-1,\"optionalInput2\":-1}");
        MockCollectionContractsBolt contractsBolt = new MockCollectionContractsBolt(true);
        OutputCollector collector = execute(data, contractsBolt);
        assertNumberOfEmits(collector, 2);
    }

    @Test
    public void testOptionalContractsBolt() {
        //mock copies input to output
        String input = "{\"input1\":-1,\"optionalInput2\":-1}";
        MockContractsBoltOutput output = new MockContractsBoltOutput();
        output.output1 = -1;
        output.optionalOutput2 = Optional.of(-1);
        runMockOptionalContractsBoltTest(input, output);
    }

    @Test(expectedExceptions = ReportedFailedException.class)
    public void testNullOutput() {
        //passing false to this mock returns null
        ObjectNode data = parseJson("{\"input1\":-1,\"optionalInput2\":-1}");
        IContractsBolt contractsBolt = new MockContractsBolt(false);
        execute(data, contractsBolt);
    }

    @Test(expectedExceptions = ReportedFailedException.class)
    public void testNullOptionalOutput() {
        //passing false to this mock returns null
        ObjectNode data = parseJson("{\"input1\":-1,\"optionalInput2\":-1}");
        IContractsBolt contractsBolt = new MockNullOptionalContractsBolt();
        execute(data, contractsBolt);
    }
    
    @Test(expectedExceptions = ReportedFailedException.class)
    public void testInvalidOutput() {
        //optionalInput2 must be at most 10 and mock copies input to output resulting in invalid output
        ObjectNode data = parseJson("{\"input1\":-1,\"optionalInput2\":100}");
        IContractsBolt contractsBolt = new MockContractsBolt(true);
        execute(data, contractsBolt);
    }

    @Test
    public void testInvalidInput() {
        //input1 must be at most 10
        String input = "{\"input1\":10000,\"optionalInput2\":-1}";
        MockContractsBoltOutput output = new MockContractsBoltOutput();
        output.output1 = 0;
        output.optionalOutput2 = Optional.absent();
        runMockOptionalContractsBoltTest(input, output);
    }

    @Test
    public void testNullInput() {
        //optional should accept json null token
        String input = "{\"input1\":-1,\"optionalInput2\":null}";
        MockContractsBoltOutput output = new MockContractsBoltOutput();
        output.output1 = -1;
        output.optionalOutput2 = Optional.absent();
        runMockOptionalContractsBoltTest(input, output);
    }

    @Test
    public void testMissingInput() {
        //optional should accept missing json value
        String input = "{\"input1\":-1}";
        MockContractsBoltOutput output = new MockContractsBoltOutput();
        output.output1 = -1;
        output.optionalOutput2 = Optional.absent();
        runMockOptionalContractsBoltTest(input, output);
    }

    @Test
    public void testAbsentOutput() {
        //passing false to this mock returns Optional.absent() which means emit nothing
        ObjectNode data = parseJson("{}");
        MockOptionalContractsBolt contractsBolt = new MockOptionalContractsBolt(false);
        OutputCollector collector = execute(data, contractsBolt);
        assertNumberOfEmits(collector, 0);
    }

    @Test
    public void testEmptyCollectionOutput() {
        //passing false to this mock returns empty list which means emit nothing
        ObjectNode data = parseJson("{\"input1\":-1,\"optionalInput2\":-1}");
        MockCollectionContractsBolt contractsBolt = new MockCollectionContractsBolt(false);
        OutputCollector collector = execute(data, contractsBolt);
        assertNumberOfEmits(collector, 0);
    }

    private void runMockOptionalContractsBoltTest(String input, MockContractsBoltOutput expectedOutput) {
        ObjectNode data = parseJson(input);
        MockOptionalContractsBolt contractsBolt = new MockOptionalContractsBolt(true);
        OutputCollector collector = execute(data, contractsBolt);
        assertEmitEquals(collector, expectedOutput);
    }

    private void assertNumberOfEmits(OutputCollector collector, int times) {
        verify(collector, times(times)).emit((String)any(), (Tuple)any(), (List<Object>) any());
    }

    private OutputCollector execute(ObjectNode input, IContractsBolt bolt) {
        BaseContractsBoltExecutor baseContractsBoltExecutor = new BaseContractsBoltExecutor(bolt);
        OutputCollector collector = mock(OutputCollector.class);
        baseContractsBoltExecutor.prepare(mock(Map.class), mock(TopologyContext.class), collector);
        Tuple tuple = mock(Tuple.class);
        when(tuple.getValue(0)).thenReturn(id);
        when(tuple.getValue(1)).thenReturn(input);

        baseContractsBoltExecutor.execute(tuple);
        return collector;
    }

    private void assertEmitEquals(OutputCollector collector, Object expectedOutput) {
        ArgumentCaptor<List> actualOutput = ArgumentCaptor.forClass(List.class);
        verify(collector).emit((String)any(), (Tuple)any(), actualOutput.capture());
        List<Object> emittedObjects = (List<Object>) actualOutput.getValue();
        Object actual = emittedObjects.get(1);
        String actualString =  ReflectionToStringBuilder.toString(actual,
                ToStringStyle.SHORT_PREFIX_STYLE, false, false);
        String expectedString =  ReflectionToStringBuilder.toString(expectedOutput,
                ToStringStyle.SHORT_PREFIX_STYLE, false, false);
        assertThat(actualString).isEqualTo(expectedString);
    }

    private ObjectNode parseJson(String input) {
        try {
            return (ObjectNode) mapper.readTree(input);
        } catch (IOException e) {
            throw Throwables.propagate(e);
        }
    }
}
