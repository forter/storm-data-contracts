package com.forter.contracts;

import backtype.storm.task.TopologyContext;
import backtype.storm.topology.BasicOutputCollector;
import backtype.storm.topology.FailedException;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.ReportedFailedException;
import backtype.storm.topology.base.BaseBasicBolt;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Tuple;
import backtype.storm.utils.Utils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.forter.contracts.cache.CacheDAO;
import com.forter.contracts.cache.CacheKeyFilter;
import com.forter.contracts.cache.Cached;
import com.forter.contracts.cache.DummyCacheDAO;
import com.forter.contracts.reflection.ContractsBoltReflector;
import com.forter.contracts.validation.ContractValidator;
import com.forter.contracts.validation.ValidatedContract;
import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

import javax.validation.ValidationException;
import java.io.Serializable;
import java.util.*;

import static com.google.common.collect.Iterables.*;

/**
 * Bolt base class that uses Data Objects for input and output.
 */
public class BaseContractsBoltExecutor<TInput, TOutput, TContractsBolt extends IContractsBolt<TInput, TOutput>> extends BaseBasicBolt {
    private final TContractsBolt delegate;
    private transient ContractFactory<TInput> inputFactory;
    private transient ContractsBoltReflector reflector;
    private transient TOutput defaultOutput;
    private transient BaseContractsBoltExecutor.IsInvalidPredicate isInvalidPredicate;
    private transient BaseContractsBoltExecutor.ValidateContractTransformation validationTransformation;
    private transient String id;
    private transient CacheDAO<? super TOutput> cache;
    private transient CacheKeyFilter cacheKeyFilter;
    private transient boolean isCacheSupported;
    private transient boolean isEnrichmentBolt;
    private transient Set<String> outputFieldsToOmit;

    public BaseContractsBoltExecutor(TContractsBolt contractsBolt) {
        this.delegate = contractsBolt;
    }

    @Override
    public void prepare(Map stormConf, TopologyContext context) {
        this.reflector = new ContractsBoltReflector(this.delegate);
        this.validationTransformation = new ValidateContractTransformation();
        this.isInvalidPredicate = new IsInvalidPredicate();
        this.inputFactory = new ContractFactory(this.reflector.getInputClass());
        this.delegate.prepare(stormConf, context);
        this.defaultOutput = this.delegate.createDefaultOutput();
        this.id = context.getThisComponentId();
        Preconditions.checkNotNull(this.defaultOutput, "getDefaultOutput cannot return null. Use Optional.absent() instead of null.");
        final ValidatedContract<TOutput> validationResult = ContractValidator.instance().validate(this.defaultOutput);
        Preconditions.checkState(
                validationResult.isValid(),
                "Default output failed contract validation: %s",
                validationResult.toString());
        this.isCacheSupported = this.reflector.getInputClass().isAnnotationPresent(Cached.class);
        this.cache = isCacheSupported ? createCacheDAO(stormConf, context) : new DummyCacheDAO<TOutput>();
        this.cacheKeyFilter = new CacheKeyFilter(this.reflector.getInputClass());
        this.isEnrichmentBolt = this.delegate.getClass().isAnnotationPresent(EnrichmentBolt.class);
        this.outputFieldsToOmit = new HashSet<>();
        if (this.isEnrichmentBolt) {
            EnrichmentBolt enrichmentAnnotation = this.delegate.getClass().getAnnotation(EnrichmentBolt.class);
            for (String field : enrichmentAnnotation.fieldsToOmit()) {
                outputFieldsToOmit.add(field);
            }
        }
    }

    @Override
    public void execute(Tuple inputTuple, BasicOutputCollector collector) {
        TOutput output = defaultOutput;
        RuntimeException exception = null;

        final Object id = inputTuple.getValue(0);
        try {
            final Object data = inputTuple.getValue(1);
            ValidatedContract validatedInputContract = transformAndValidateInput(data);

            if (!validatedInputContract.isValid()) {
                handleInputError(validatedInputContract, this.id, inputTuple);
                return;
            } else {
                TInput input = (TInput) validatedInputContract.getContract();

                Map<String, Object> cacheKeyData;
                if (data instanceof ObjectNode) {
                    cacheKeyData = cacheKeyFilter.createKey((ObjectNode) data);
                } else {
                    cacheKeyData = cacheKeyFilter.createKey(input);
                }

                Optional<? super TOutput> cachedOutput = this.cache.get(cacheKeyData);
                if (cachedOutput.isPresent()) {
                    output = (TOutput) cachedOutput.get();
                } else {
                    long startTime = System.currentTimeMillis();
                    output = delegate.execute(input);
                    this.cache.save(output, cacheKeyData, startTime);
                }

                if (this.isCacheSupported) {
                    this.reportCacheStatus(cachedOutput.isPresent(), inputTuple);
                }
            }
        } catch (FailedException cve) { // includes ContractViolationReportedFailedException
            exception = cve;
        } catch (RuntimeException e) {
            exception = new ReportedFailedException(e);
        } finally {
            Iterable<Object> outputContracts;
            Iterable<ValidatedContract> invalidOutputContracts;

            if (output == null) {
                outputContracts = ImmutableList.of();
                invalidOutputContracts = ImmutableList.of(validationTransformation.apply(output));
            } else {
                outputContracts = iterableContracts(output);
                invalidOutputContracts = filter(transform(outputContracts, validationTransformation), isInvalidPredicate);
            }

            if (isEmpty(invalidOutputContracts)) {
                for (Object contract : outputContracts) {
                    emit(id, contract, inputTuple, collector);
                }
            } else {
                emit(id, defaultOutput, inputTuple, collector);
                exception = new ContractViolationReportedFailedException(invalidOutputContracts, this.id);
            }
        }

        if (exception != null) {
            throw exception;
        }
    }

    protected void handleInputError(ValidatedContract validatedInputContract, String id, Tuple tuple) {
        // Do nothing
    }

    @Override
    public void cleanup() {
        delegate.cleanup();
    }

    public TContractsBolt getContractBolt() {
        return this.delegate;
    }

    private ValidatedContract transformAndValidateInput(final Object contract) {
        try {
            TInput input = transformInput(contract);
            return ContractValidator.instance().validate(input);
        } catch (JsonProcessingException e) {
            return new ValidatedContract(null, new ValidationException(e));
        }
    }

    private boolean isOfTypeInput(Object contract) {
        return reflector.getInputClass().equals(contract.getClass());
    }

    /**
     * Override to support different input formats.
     * If cannot convert the contract, returns {@code null}.
     */
    protected TInput transformInput(Object contract) throws JsonProcessingException {

        if (contract == null) {
            return null;
        }
        if (isOfTypeInput(contract)) {
            return (TInput) contract;
        } else {
            //lock is needed to ensure no threads are iterating over the contract in parallel
            synchronized (contract) {
                if (contract instanceof ObjectNode) {
                    return ContractConverter.instance().convertObjectNodeToContract((ObjectNode) contract, inputFactory);
                } else {
                    return ContractConverter.instance().convertContractToContract(contract, inputFactory);
                }
            }
        }
    }

    /**
     * Creates the {@link com.forter.contracts.cache.CacheDAO} used in the class in case TInput supports caching.
     */
    protected CacheDAO<TOutput> createCacheDAO(Map stormConf, TopologyContext context) {
        return new DummyCacheDAO<>();
    }

    protected void reportCacheStatus(Boolean status, Tuple tuple) {
    }

    /**
     * Override this method for different merging/enrichment strategies
     */
    protected List<Object> enrichAttributes(List<Object> update, Tuple originalInput) {
        Map<String, Object> finalAttributes = new HashMap<>();
        finalAttributes.putAll((Map<String, Object>)originalInput.getValue(1));
        for (Map.Entry<String, Object> updatedAttribute : ((Map<String, Object>)update.get(1)).entrySet()) {
            if (updatedAttribute.getValue() != null || !finalAttributes.containsKey(updatedAttribute.getKey())) {
                finalAttributes.put(updatedAttribute.getKey(), updatedAttribute.getValue());
            }
        }
        for (String field : outputFieldsToOmit) {
            finalAttributes.put(field, null);
        }
        update.set(1, finalAttributes);

        return update;
    }

    protected List<Object> createOutputTuple(Object id, Object contract) {
        return Lists.newArrayList(id, transformOutput(contract));
    }

    private void emit(Object id, Object contract, Tuple originalInput, BasicOutputCollector collector) {
        List<Object> tuple = this.createOutputTuple(id, contract);

        if(this.isEnrichmentBolt) {
            tuple = this.enrichAttributes(tuple, originalInput);
        }

        collector.emit(Utils.DEFAULT_STREAM_ID, tuple);
    }

    private Iterable<Object> iterableContracts(TOutput output) {

        switch (reflector.getExecuteReturnType()) {
            case NOT_NULL_CONTRACT:
                return ImmutableList.of((Object) output);

            case OPTIONAL_CONTRACT:
                Optional optional = (Optional) output;
                if (optional.isPresent()) {
                    return ImmutableList.of(optional.get());
                }
                return ImmutableList.of();

            case COLLECTION_CONTRACTS:
                return (Collection) output;

            default:
                Preconditions.checkState(false, "Unsupported executionReturnType " + reflector.getExecuteReturnType());
                //suppress compiler warning
                return null;
        }
    }

    /**
     * Override this method to provide different output formats.
     */
    protected Object transformOutput(Object output) {
        return output;
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        declarer.declare(new Fields("id", "validatedContract"));
    }

    @Override
    public Map<String, Object> getComponentConfiguration() {
        return delegate.getComponentConfiguration();
    }

    protected ContractsBoltReflector getReflector() {
        return reflector;
    }

    static class IsInvalidPredicate implements Predicate<ValidatedContract>, Serializable {

        @Override
        public boolean apply(ValidatedContract input) {
            return !input.isValid();
        }
    }

    static class ValidateContractTransformation implements Function<Object, ValidatedContract>, Serializable {
        @Override
        public ValidatedContract apply(Object input) {
            return ContractValidator.instance().validate(input);
        }
    }
}
