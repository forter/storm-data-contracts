package com.forter.contracts.validation;

import com.forter.contracts.mocks.MockContractsBoltInput;
import com.google.common.base.Optional;
import org.testng.annotations.Test;

import static org.fest.assertions.api.Assertions.assertThat;

/**
 * Tests {@link com.forter.contracts.validation.ContractValidator}
 */
public class ContractValidatorTest {

    @Test
    public void testValid() {
        MockContractsBoltInput contract = new MockContractsBoltInput();
        contract.input1 = 1;
        contract.optionalInput2 = Optional.of(1);
        ValidatedContract<MockContractsBoltInput> validationResult  =ContractValidator.instance().validate(contract);
        assertThat(validationResult.isValid()).isTrue();
    }

    @Test
    public void testNullViolation() {
        MockContractsBoltInput contract = new MockContractsBoltInput();
        contract.input1 = null;
        contract.optionalInput2 = Optional.of(1);
        ValidatedContract<MockContractsBoltInput> validationResult  =ContractValidator.instance().validate(contract);
        validationResult.toString();
        assertThat(validationResult.isValid()).isFalse();
    }

    @Test
    public void testNullOptional() {
        MockContractsBoltInput contract = new MockContractsBoltInput();
        contract.input1 = 1;
        contract.optionalInput2 = null;
        ValidatedContract<MockContractsBoltInput> validationResult = ContractValidator.instance().validate(contract);
        validationResult.toString();
        assertThat(validationResult.isValid()).isFalse();
    }
}
