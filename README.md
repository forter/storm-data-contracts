storm-data-contracts
====================

This project let's you write Storm Bolts in Java with strict data contracts:

Exceptions
----------
All exceptions are reported to storm.

Strongly Typed
--------------
Bolt input and output are POJOs
```
public class MyBolt implements IContractsBolt<MyBoltInput,Collection<MyBoltOutput>> {
    @Override
    public Collection<MyBoltOutput> executeValidInput(MyBoltInput input) {
        MyBoltOutput output = new MyBoltOutput();
        if (input.y.isPresent()) {
            output.z = input.y.get() + input.x;
        }
        else {
            output.z = "default" + input.x;
        }
        return Lists.newArrayList(output);
    }

    @Override
    public Collection<MyBoltOutput> executeInvalidInput(MyBoltInput input, ContractValidationResult violations) {
        return Lists.newArrayList();
    }
}
```

Input and Output Data Contracts
-------------------------------
Support Guava Optional and Hibernate Validator for strict data contracts
```
public class MyBoltInput {
    @NotNull
    @Min(0)
    public Integer x;

    @UnwrapValidatedValue
    @Pattern(regexp="\\p{L}*")
    public Optional<String> y;
}

public class MyBoltOutput {
    @NotNull
    public String z;
}
```

CSV driven unit tests 
---------------------
CSV file header is used to inject data into MyBoltInput and expected MyBoltOutput during unit tests

*src/test/resources/MyTest.csv*

```
input.x,input.y,output.z
1,prefix,prefix1
2,__NULL__,default2
```

*src/test/java/MyTest.java*

```
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

    //reads from src/main/resources/MyBoltTest.csv
    @Test(dataProviderClass=TestDataProvider.class, dataProvider="csv")
    public void test(MyBoltInput input, MyBoltOutput expectedOutput) {
        Collection<MyBoltOutput> outputs = bolt.executeValidInput(input);
        MyBoltOutput output = Iterables.getOnlyElement(outputs);
        assertReflectionEquals(expectedOutput, output);
    }
}
```

Adding Bolt into a Topology
---------------------------
```
// Using Storm's TopologyBuilder
TopologyBuilder builder = new TopologBuilder();
builder.setBolt("myContractsBolt",new BaseContractsBoltExecutor(new MyContractsBolt()))

// Using com.forter.monitoring.MonitoredTopologyBuilder
MonitoredTopologyBuilder builder = new MonitoredTopologBuilder();
builder.registerRichBolt(IContractsBolt.class, BaseContractsBoltExecutor.class);
builder.setBolt("myContractsBolt",new MyContractsBolt());
```

**input**

Bolt expects a pair tuple (such as [id, data]). 
The second item of the pair is expected to be one of the following:
* `MyBoltInput` - the expected input type, will be validated by the bolt.
* `ValidContract<MyBoltInput>` - a wrapper for the expected input type, will not be validated.
* `ObjectNode` - a weakly typed object (Jackson parsed JSON object similar to Map). Converted to MyBoltInput and validated.
* `Map` or `SomeOtherBoltInput` - converted into an `ObjectNode` and then converted into MyBoltInput and validated.

**output**

The bolt emits a pair tuple (such as [id, data]).
The second item of the pair is of type `ValidContract<MyBoltOutput>`

Transform the output either using the OOP way - deriving from BaseContractsBoltExecutor:
```
public class ToMapContractsBoltExecutor<TInput, TOutput, TContractsBolt extends IContractsBolt<TInput, TOutput>> extends BaseContractsBoltExecutor<TInput, TOutput, TContractsBolt> {

    public ToMapContractsBoltExecutor(TContractsBolt contractsBolt) {
        super(contractsBolt);
    }

    @Override
    protected Object transformOutput(Object output) {
        return ContractConverter.instance().convertContractToMap(output);
    }
}
```

Or transform the output the Storm way, with another bolt:
```
public abstract class ValidContractToMapConverterBolt extends BaseBasicBolt {

    @Override
    public void execute(Tuple input, BasicOutputCollector collector) {
        ValidContract<?> validContract = (ValidContract<?>) input.getValue(1);
        Map map = ContractConverter.instance().convertContractToMap(validContract.getContract());
        collector.emit(Lists.newArrayList(input.getValue(0), map));
    }
```


Maven
-----
```
    <dependencies>
        <dependency>
            <groupId>com.forter</groupId>
            <artifactId>storm-data-contracts</artifactId>
            <version>0.1-SNAPSHOT</version>
            <scope>compile</scope>
        </dependency>
        <!-- Annotation dependencies -->
        <dependency>
            <groupId>javax.validation</groupId>
            <artifactId>validation-api</artifactId>
            <version>1.1.0.Final</version>
        </dependency>
        <dependency>
            <groupId>org.hibernate</groupId>
            <artifactId>hibernate-validator</artifactId>
            <version>5.1.2.Final</version>
        </dependency>
        <!-- testing dependencies -->
        <dependency>
            <groupId>com.forter</groupId>
            <artifactId>storm-data-contracts-testng</artifactId>
            <version>0.1-SNAPSHOT</version>
            <scope>test</scope>
        </dependency>
        <dependency>
            <groupId>org.unitils</groupId>
            <artifactId>unitils-core</artifactId>
            <scope>test</scope>
        </dependency>
    </dependencies>
    <repositories>
        <repository>
            <id>forter-public</id>
            <name>forter public</name>
            <url>http://oss.forter.com/repository</url>
            <releases>
                <checksumPolicy>fail</checksumPolicy>
            </releases>
            <snapshots>
                <checksumPolicy>fail</checksumPolicy>
                <enabled>true</enabled>
            </snapshots>
        </repository>
    </repositories>
```
