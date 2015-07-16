package com.forter.contracts;
import backtype.storm.task.TopologyContext;
import backtype.storm.tuple.Tuple;

import java.io.Serializable;
import java.util.Map;


/**
 * Bolts that processes one input and produces one or more output (strongly typed)
 * Examples :
 *      //one input, one output
 *      class MyBolt extends IContractsBolt<MyInput,MyOutput> {...}
 *
 *      //one input, zero or one output
 *      class MyBolt extends IContractsBolt<MyInput,Optional<MyOutput>> {...}
 *
 *      //one input, zero or more outputs
 *      class MyBolt extends IContractsBolt<MyInput,Collection<MyOutput>> {...}
 *
 * Note: The notation IContactsBolt with an "I" prefix is aligned with the Apache Storm probject
 */
public interface IContractsBolt<TInput, TOutput> extends Serializable {

    void prepare(Map stormConf, TopologyContext context);

    /**
     * Processes the input and returns the output.
     * All acking is managed for you. Throw a FailedException if you want to fail the input.
     * @param input the input that requires processing
     * @return the output object to emit or Optional.absent() if no emit is necessary.
     */
    TOutput execute(TInput input);


    void cleanup();

    /**
     * Declare configuration specific to this component. Only a subset of the "topology.*" configs can
     * be overridden. The component configuration can be further overridden when constructing the
     * topology using {@link backtype.storm.topology.TopologyBuilder}
     *
     */
    Map<String, Object> getComponentConfiguration();

    /**
     * Returns the output when the input is illegal or an exception occured during execution
     * All acking is managed for you. Throw an exception if you want to fail the input.
     * @return the output object to emit.
     */
    TOutput createDefaultOutput();

    /**
     * @return The current {@link backtype.storm.tuple.Tuple} the bolt is working on.
     */
    Tuple getCurrentTuple();

    /**
     * Saves the current {@link backtype.storm.tuple.Tuple} the bolt is working on.
     * @param tuple The {@link backtype.storm.tuple.Tuple} to save.
     */
    void setCurrentTuple(Tuple tuple);
}

