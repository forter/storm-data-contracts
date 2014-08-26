package com.forter.contracts;
import backtype.storm.task.TopologyContext;
import com.forter.contracts.validation.ContractValidationResult;

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
public interface IContractsBolt<TInput, TOutput> {

    void prepare(Map stormConf, TopologyContext context);

    /**
     * Processes the input and returns the output.
     * All acking is managed for you. Throw a FailedException if you want to fail the input.
     * @param input the input that requires processing
     * @return the output object to emit or Optional.absent() if no emit is necessary.
     */
    TOutput executeValidInput(TInput input);

    /**
     * Returns the output when the input is illegal
     * All acking is managed for you. Throw a FailedException if you want to fail the input.
     * @return the output object to emit.
     */
    TOutput executeInvalidInput(TInput input, ContractValidationResult violations);

    void cleanup();
}

