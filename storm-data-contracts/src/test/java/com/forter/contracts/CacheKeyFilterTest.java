package com.forter.contracts;

import com.forter.contracts.cache.CacheKeyFilter;
import com.forter.contracts.mocks.MockContractsBoltInput;
import com.google.common.base.Optional;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.Map;

import static org.fest.assertions.api.Assertions.assertThat;

public class CacheKeyFilterTest {

    private CacheKeyFilter filter;

    @BeforeClass
    public void beforeClass() {
        filter = new CacheKeyFilter(MockContractsBoltInput.class);
    }

    @Test
    public void testFilter() {
        MockContractsBoltInput input = new MockContractsBoltInput();
        input.input1 = 1;
        input.optionalInput2 = Optional.of(1);
        Map<String, Object> filteredKey = filter.createKey(input);
        assertThat(filteredKey.get("input1")).isEqualTo(2);
        assertThat(filteredKey.containsKey("optionalInput2")).isFalse();
    }

}
