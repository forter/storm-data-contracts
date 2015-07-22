package com.forter.contracts.mocks;

import com.forter.contracts.cache.Cached;
import com.google.common.base.Optional;
import org.hibernate.validator.valuehandling.UnwrapValidatedValue;

import javax.validation.constraints.Max;
import javax.validation.constraints.NotNull;

/**
 * Input for {@link com.forter.contracts.BaseContractsBoltExecutorTest}
 */
@Cached("mock")
public class MockContractsBoltInput {

    @Max(10)
    @NotNull
    public Integer input1;

    @Max(10)
    @UnwrapValidatedValue
    public Optional<Integer> optionalInput2;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MockContractsBoltInput)) return false;
        MockContractsBoltInput that = (MockContractsBoltInput) o;
        if (input1 != null ? !input1.equals(that.input1) : that.input1 != null) {
            return false;
        }
        if (optionalInput2 != null ? !optionalInput2.equals(that.optionalInput2) : that.optionalInput2 != null) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int result = input1 != null ? input1.hashCode() : 0;
        result = 31 * result + (optionalInput2 != null ? optionalInput2.hashCode() : 0);
        return result;
    }
}
