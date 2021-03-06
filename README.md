storm-data-contracts 
====================

[![Build Status](https://img.shields.io/travis/forter/storm-data-contracts.svg)](https://travis-ci.org/forter/storm-data-contracts/)

This project lets you write Storm Bolts in Java with strict data contracts:

Strongly Typed
--------------
Bolt input and output are POJOs
```java
public class MyBolt implements IContractsBolt<MyBoltInput,Collection<MyBoltOutput>> {
    @Override
    public Collection<MyBoltOutput> execute(MyBoltInput input) {
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
    public Collection<MyBoltOutput> createDefaultOutput() {
        return Lists.newArrayList();
    }
}
```

Input and Output Data Contracts
-------------------------------
Support Guava Optional and Hibernate Validator for strict data contracts
```java
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


Exceptions
----------
* All input contract violations are reported to storm.
* All #execute() exceptions are reported to storm.
* All output contract violations are reported to storm, and the default output is emitted instead.


Caching
-------
BaseContractsBoltExecutor supports adding a caching mechanism via inheritance and overriding of
BaseContractsBoltExecutor#createCacheDAO.
Cached input contracts should be annotated with @Cached annotation and fields which are used as cache keys should be
annotated with @CacheKey
```java
@Cached
public class Input {

    @Max(10)
    @NotNull
    @CacheKey
    public Integer input1;

    @Max(10)
    @UnwrapValidatedValue
    public Optional<Integer> optionalInput2;
}

public class MyCacheDAO<TOutput> implements CacheDAO<TOutput> {

    public Map<Map<String, Object>, TOutput> cache = new HashMap<>();

    @Override
    public Optional<TOutput> get(Map<String, Object> input) {
        if (cache.containsKey(input)) {
            return Optional.of(cache.get(input));
        }
        return Optional.absent();
    }

    @Override
    public void save(TOutput output, Map<String, Object> inputKey, long startTimeMillis) {
        cache.put(inputKey, output);
    }
}

public class MyCachedContractBoltExecutor<TInput, TOutput, TContractsBolt extends IContractsBolt<TInput, TOutput>>
        extends BaseContractsBoltExecutor {

    @Override
    protected CacheDAO<TInput, TOutput> createCacheDAO(Map stormConf, TopologyContext context) {
        return new MyCacheDAO<TOutput>();
    }

}
```
@CacheKey supports transformation of input for cache purposes (without changing the input the bolt receives in case of
cache miss). For example:
```java
@Cached
public class Input {

    @Max(10)
    @NotNull
    @CacheKey(transformers = {LowerCaseTransformer.class})
    public String input1;
}

public class LowerCaseTransformer implements CacheKeyTransformer {

    public Object transform(Object key) {
        return ((String) key).toLowerCase();
    }
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

```java
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
    public void testExecute(MyBoltInput input, MyBoltOutput expectedOutput) {
        Collection<MyBoltOutput> outputs = bolt.execute(input);
        MyBoltOutput output = Iterables.getOnlyElement(outputs);
        assertReflectionEquals(expectedOutput, output);
    }
    
    @Test
    public void testDefaultOutput() {
        assertTrue(ContractValidator.instance().validate(bolt.createDefaultOutput()).isValid());
    }
}
```

Adding Bolt into a Topology
---------------------------
```java
TopologyBuilder builder = new TopologBuilder();
builder.setBolt("myContractsBolt",new BaseContractsBoltExecutor(new MyContractsBolt()))

```

**input**

Bolt expects a pair tuple (such as [id, data]). 
The second item of the pair is expected to be one of the following:
* `MyBoltInput` - the expected input type, will be validated by the bolt.
* `ObjectNode` - a weakly typed object (Jackson parsed JSON object similar to Map). Converted to MyBoltInput and validated.
* `Map` or `SomeOtherBoltInput` - converted into an `ObjectNode` and then converted into MyBoltInput and validated.

This behavior can be modified by overriding the BaseContractsBoltExecutor#transformInput() method.

**output**

The bolt emits a pair tuple (such as [id, data]).
The second item of the pair is a MyBoltOutput`

This behavior can be modified by overriding the BaseContractsBoltExecutor#transformOutput() method:
```java
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

Enrichment Bolts
-----
Normally, contract bolts will "absorb" any attribute that passes by them. This means that the only attributes available to any bolt connected after a contract bolt will be the attributes specified in the output of that contract bolt.
One way around this is doing an old-fashioned join, but this because very hard to maintain if dealing with a large topology.
A quick solution around this is the use of the `@EnrichmentBolt` annotation, which will indicate to the ContractBoltExecutor that this bolt is in "upsert" mode to the attributes map: it will only append (or update, if  already existent) to it and will let the other attributes bypass it for the next bolts to use.
```java
@EnrichmentBolt
public class MyEnrichmentBolt extends BaseContractBolt<MyInput, MyOutput> {
    // This bolt will allow attributes not in its input/output pass right through it
    ....
}
```

Maven
-----
```
    <dependencies>
        <dependency>
            <groupId>com.forter</groupId>
            <artifactId>storm-data-contracts</artifactId>
            <version>0.2</version>
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
            <version>0.2</version>
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
