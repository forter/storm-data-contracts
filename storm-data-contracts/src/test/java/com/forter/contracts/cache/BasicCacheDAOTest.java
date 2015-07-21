package com.forter.contracts.cache;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.base.Optional;
import com.google.common.base.Throwables;
import com.google.common.hash.Hashing;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.IOException;

import static org.fest.assertions.api.Assertions.assertThat;

public class BasicCacheDAOTest {

    private MockCacheDAO cacheDAO;
    private ObjectMapper mapper;

    @BeforeClass
    public void beforeClass() {
        cacheDAO = new MockCacheDAO(MockOutput.class);
        mapper = new ObjectMapper();
    }

    @Test
    public void testGet() {
        MockOutput expectedOutput = new MockOutput();
        MockInput data = new MockInput();
        Optional<MockOutput> fetched = cacheDAO.get(data);
        assertThat(cacheDAO.fetchedKey).isEqualTo("abc-" + Hashing.sha256().hashBytes("{\"a\":\"a\",\"c\":1}".getBytes()).toString());
        assertThat(fetched.isPresent()).isTrue();
        MockOutput output = fetched.get();
        assertThat(output.a).isEqualTo(expectedOutput.a);
        assertThat(output.b.isPresent()).isFalse();
        assertThat(output.c).isEqualTo(expectedOutput.c);
    }

    @Test
    public void testSave() throws Exception {
        MockInput input = new MockInput();
        MockOutput output = new MockOutput();
        cacheDAO.save(output, input, 1L);
        assertThat(cacheDAO.savedKey).isEqualTo("abc-" + Hashing.sha256().hashBytes("{\"a\":\"a\",\"c\":1}".getBytes()).toString());
        assertThat(cacheDAO.savedData.get("result")).isEqualTo(mapper.readTree(MockCacheDAO.SAVED_DATA).get("result"));
        assertThat(cacheDAO.savedData.get("startTime").asLong()).isEqualTo(1);
        assertThat(cacheDAO.savedData.get("duration").asLong()).isPositive();
    }


    @Cached("abc")
    private static class MockInput {
        @CacheKey
        private Optional<String> a = Optional.of("a");
        @CacheKey
        private Integer c = 1;
    }

    public static class MockOutput {
        private String a = "a";
        private Optional<String> b = Optional.absent();
        private Integer c = 1;
    }

    private class MockCacheDAO extends BasicCacheDAO<MockInput, MockOutput> {

        private String savedKey;
        private String fetchedKey;
        private ObjectNode savedData;
        private final static String SAVED_DATA = "{\"result\":{\"a\":\"a\",\"b\":null,\"c\":1}}";


        public MockCacheDAO(Class<MockOutput> dataClass) {
            super(dataClass);
        }

        @Override
        protected ObjectNode fetch(String key) {
            this.fetchedKey = key;
            try {
                return (ObjectNode) mapper.readTree(SAVED_DATA);
            } catch (IOException e) {
                Throwables.propagate(e);
            }
            return null;
        }

        @Override
        protected void persist(String key, ObjectNode data) {
            this.savedKey = key;
            this.savedData = data;
        }
    }

}
