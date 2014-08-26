package com.forter.contracts.testng;

import backtype.storm.task.TopologyContext;
import com.google.common.collect.Iterables;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.Collection;
import java.util.Map;

import static org.mockito.Mockito.mock;
import static org.unitils.reflectionassert.ReflectionAssert.assertReflectionEquals;

/**
 * ContractsBolt testing example using a CSV file.
 * The CSV file resides in the resources folder with the exact same package and name (just with the csv extension)
 * src/test/java/com/forter/contracts/testng/MyBoltTest.java --> src/test/resources/com/forter/contracts/testng/MyBoltTest.csv
 */
public class MyBoltTest {

    private MyBolt bolt;

    @BeforeClass
    public void before() {
        bolt = new MyBolt();
        bolt.prepare(mock(Map.class),mock(TopologyContext.class));
    }

    @AfterClass
    public void after() {
        bolt.cleanup();
    }

    @Test(dataProviderClass=TestDataProvider.class, dataProvider="csv")
    public void test(MyBoltInput input, MyBoltOutput expectedOutput) {
        Collection<MyBoltOutput> outputs = bolt.executeValidInput(input);
        MyBoltOutput output = Iterables.getOnlyElement(outputs);
        assertReflectionEquals(expectedOutput, output);
    }
}
