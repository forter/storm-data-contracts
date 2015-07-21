package com.forter.contracts.mocks;

import com.forter.contracts.cache.CacheDAO;
import com.google.common.base.Optional;

/**
 * Mock {@link com.forter.contracts.cache.CacheDAO}.
 */
public class MockCacheDAO implements CacheDAO<MockContractsBoltInput, MockContractsBoltOutput> {

    public boolean saved = false;
    public boolean fetched = false;

    @Override
    public Optional<MockContractsBoltOutput> get(MockContractsBoltInput key) {
        if (saved) {
            MockContractsBoltOutput output = new MockContractsBoltOutput();
            output.output1 = -1;
            output.optionalOutput2 = Optional.absent();
            fetched = true;
            return Optional.of(output);
        }
        return Optional.absent();
    }

    @Override
    public void save(MockContractsBoltOutput record, MockContractsBoltInput mockContractsBoltInput, long startTime) {
        saved = true;
    }
}
