package com.forter.contracts;

import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.tuple.Tuple;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.forter.contracts.mocks.*;
import com.google.common.base.Optional;
import com.google.common.base.Throwables;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.mockito.ArgumentCaptor;
import org.testng.annotations.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
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
        IContractsBolt contractsBolt = new MockContractsBolt();
        OutputCollector collector = execute(data, contractsBolt);
        verify(collector).ack((Tuple)any());
        assertEmitEquals(collector, output);
    }

    @Test
    public void testCollectionContractBolt() {
        //mock copies input to output twice
        ObjectNode data = parseJson("{\"input1\":-1,\"optionalInput2\":-1}");
        MockCollectionContractsBolt contractsBolt = new MockCollectionContractsBolt();
        OutputCollector collector = execute(data, contractsBolt);
        verify(collector, times(2)).emit((String)any(), (Tuple)any(), (List<Object>) any());
        verify(collector).ack((Tuple)any());
    }

    @Test
    public void testOptionalContractsBolt() {
        //mock copies input to output
        String input = "{\"input1\":-1,\"optionalInput2\":-1}";
        MockContractsBoltOutput output = new MockContractsBoltOutput();
        output.output1 = -1;
        output.optionalOutput2 = Optional.of(-1);
        ObjectNode data = parseJson(input);
        MockOptionalContractsBolt contractsBolt = new MockOptionalContractsBolt();
        execute(data, contractsBolt);
    }

    @Test
    public void testNullOutput() {
        ObjectNode data = parseJson("{\"input1\":-1,\"optionalInput2\":-1}");
        IContractsBolt contractsBolt = new MockNullContractsBolt();
        OutputCollector collector = execute(data, contractsBolt);
        verify(collector).reportError(any(NullPointerException.class));
        verify(collector).fail((Tuple)any());

    }

    @Test
    public void testNullOptionalOutput() {
        ObjectNode data = parseJson("{\"input1\":-1,\"optionalInput2\":-1}");
        IContractsBolt contractsBolt = new MockNullOptionalContractsBolt();
        OutputCollector collector = execute(data, contractsBolt);
        verify(collector).reportError(any(NullPointerException.class));
        verify(collector).fail((Tuple)any());
    }
    
    @Test(enabled = false) // TODO: Itai needs to fix this
    public void testInvalidOutput() {
        //optionalInput2 must be at most 10 and mock copies input to output resulting in invalid output
        ObjectNode data = parseJson("{\"input1\":-1,\"optionalInput2\":100}");
        IContractsBolt contractsBolt = new MockContractsBolt();
        OutputCollector collector = execute(data, contractsBolt);
        verify(collector).reportError(any(IllegalStateException.class));
        verify(collector).fail((Tuple)any());
    }

    @Test
    public void testInvalidInput() {
        //input1 must be at most 10
        String input = "{\"input1\":10000,\"optionalInput2\":-1}";
        MockContractsBoltOutput output = new MockContractsBoltOutput();
        output.output1 = 0;
        output.optionalOutput2 = Optional.absent();
        ObjectNode data = parseJson(input);
        MockOptionalContractsBolt contractsBolt = new MockOptionalContractsBolt();
        OutputCollector collector = execute(data, contractsBolt);
        assertEmitEquals(collector, output);
        verify(collector).fail((Tuple)any());
    }

    @Test
    public void testNullInput() {
        //optional should accept json null token
        String input = "{\"input1\":-1,\"optionalInput2\":null}";
        MockContractsBoltOutput output = new MockContractsBoltOutput();
        output.output1 = -1;
        output.optionalOutput2 = Optional.absent();
        ObjectNode data = parseJson(input);
        MockOptionalContractsBolt contractsBolt = new MockOptionalContractsBolt();
        OutputCollector collector = execute(data, contractsBolt);
        assertEmitEquals(collector, output);
        verify(collector).ack((Tuple) any());
    }

    @Test
    public void testMissingInput() {
        //optional should accept missing json value
        String input = "{\"input1\":-1}";
        MockContractsBoltOutput output = new MockContractsBoltOutput();
        output.output1 = -1;
        output.optionalOutput2 = Optional.absent();
        ObjectNode data = parseJson(input);
        MockOptionalContractsBolt contractsBolt = new MockOptionalContractsBolt();
        OutputCollector collector = execute(data, contractsBolt);
        assertEmitEquals(collector, output);
        verify(collector).ack((Tuple) any());
    }

    @Test
    public void testAbsentOutput() {
        ObjectNode data = parseJson("{}");
        IContractsBolt contractsBolt = new MockOptionalAbsentBolt();
        OutputCollector collector = execute(data, contractsBolt);
        verify(collector, times(0)).emit((String)any(), (Tuple)any(), (List<Object>) any()); //no output
        verify(collector).fail((Tuple) any()); //invalid input
    }

    @Test
    public void testEmptyCollectionOutput() {
        ObjectNode data = parseJson("{\"input1\":-1,\"optionalInput2\":-1}");
        IContractsBolt contractsBolt = new MockEmptyCollectionBolt();
        OutputCollector collector = execute(data, contractsBolt);
        verify(collector, times(0)).emit((String)any(), (Tuple)any(), (List<Object>) any());
        verify(collector).ack((Tuple)any());
    }

    @Test
    public void testSerializable() {
        IContractsBolt contractsBolt = new MockEmptyCollectionBolt();
        BaseContractsBoltExecutor bolt = new BaseContractsBoltExecutor(contractsBolt);
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             ObjectOutputStream oos = new ObjectOutputStream( baos )) {
            oos.writeObject(bolt);
        } catch (IOException e) {
            throw Throwables.propagate(e);
        }

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
