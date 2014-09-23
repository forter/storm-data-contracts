package com.forter.contracts.mocks;

import com.google.common.base.Optional;
import org.hibernate.validator.valuehandling.UnwrapValidatedValue;

import javax.validation.constraints.Max;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * Output for {@link com.forter.contracts.validation.ContractValidatorTest}
 */
public class MockContractsWithListOutput {

    @NotNull
    public Integer output1;

    @Max(10)
    @UnwrapValidatedValue
    public Optional<Integer> optionalOutput2;

    @NotNull
    public List<MockContractsWithListItem> listOutput;
}
