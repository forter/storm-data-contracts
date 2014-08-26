package com.forter.contracts.testng;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import com.forter.contracts.ContractConverter;
import com.forter.contracts.ContractFactory;
import com.forter.contracts.validation.ContractValidationResult;
import com.forter.contracts.validation.ContractValidator;
import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.collect.Iterators;
import com.google.common.io.Resources;
import com.google.common.reflect.Invokable;
import org.testng.annotations.DataProvider;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.Iterator;
import java.util.Map;

/**
 * TestNG data provider
 */
public class TestDataProvider {

    /**
     * Expects a csv file with headers matching those of the input attributes and output attributes.
     * For example, given an input object with x attribute and output object with y attribute the csv file would be:
     *
     * input.x,output.y
     * 1,2
     * 3,4
     *
     */
    @DataProvider(name = "csv")
    public static Iterator<Object[]> getDataFromCsvFile(Method testMethod) throws IOException {
        Invokable<?, Object> invokable = Invokable.from(testMethod);
        final Class<?> inputClass = invokable.getParameters().get(0).getType().getRawType();
        final Class<?> outputClass = invokable.getParameters().get(1).getType().getRawType();
        return csvToContracts(inputClass, outputClass, getCsvIterator(invokable));
    }

    private static MappingIterator<Object> getCsvIterator(Invokable<?, Object> invokable) throws IOException {
        CsvMapper csvMapper = new CsvMapper();
        CsvSchema bootstrapSchema = CsvSchema.emptySchema().withHeader();
        String resourceName = invokable.getDeclaringClass().getName().replace('.', File.separatorChar)+".csv";
        URL csvFile = Resources.getResource(resourceName);
        return csvMapper.reader(Map.class).with(bootstrapSchema).readValues(csvFile);
    }

    private static Iterator<Object[]> csvToContracts(Class<?> inputClass, Class<?> outputClass, MappingIterator<Object> csvIterator) {
        final ContractFactory<?> outputFactory = new ContractFactory(outputClass);
        final ContractFactory<?> inputFactory = new ContractFactory(inputClass);
        return Iterators.transform(csvIterator, new Function<Object, Object[]>() {
            @Override
            public Object[] apply(Object o) {
                final ObjectNode inputNode = JsonNodeFactory.instance.objectNode();
                final ObjectNode outputNode = JsonNodeFactory.instance.objectNode();
                for (Map.Entry<String, Object> entry : ((Map<String, Object>) o).entrySet()) {
                    String value = (String) entry.getValue();
                    if (entry.getKey().startsWith("input.")) {
                        inputNode.put(entry.getKey().substring("input.".length()), value);
                    } else if (entry.getKey().startsWith("output.")) {
                        outputNode.put(entry.getKey().substring("output.".length()), value);
                    } else {
                        Preconditions.checkArgument(false, "CSV header " + entry.getKey() + " must start with " + "input. or output.");
                    }
                }
                nullify(inputNode);
                nullify(outputNode);
                final Object input = createAndValidateContract(inputNode, inputFactory);
                final Object output = createAndValidateContract(outputNode, outputFactory);
                return new Object[]{input, output};
            }
        });
    }

    private static void nullify(ObjectNode node) {
        Iterator<String> iterator = node.fieldNames();
        while(iterator.hasNext()) {
            String fieldName = iterator.next();
            TextNode textNode = (TextNode) node.get(fieldName);
            if (textNode.textValue().equals("__NULL__")) {
                node.set(fieldName, null);
            }
        }
    }

    private static Object createAndValidateContract(ObjectNode node, ContractFactory<?> factory) {
        Object contract = factory.newInstance();
        ContractConverter.instance().updateObjectNodeToContract(node, contract);
        validate(contract);
        return contract;
    }

    private static void validate(Object contract) {
        ContractValidationResult<Object> inputValidationResult = ContractValidator.instance().validate(contract);
        if (!inputValidationResult.isValid()) {
            throw new AssertionError("Input validation assertion error: " + inputValidationResult.toString());
        }
    }
}
