package com.forter.contracts.validation;

import com.forter.contracts.mocks.MockContractsBoltInput;
import com.forter.contracts.mocks.MockContractsWithListOutput;
import com.google.common.base.Optional;
import org.testng.annotations.Test;

import java.util.LinkedList;

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
        ValidatedContract<MockContractsBoltInput> validationResult  = ContractValidator.instance().validate(contract);
        assertThat(validationResult.isValid()).isTrue();
    }

    @Test
    public void testNullViolation() {
        MockContractsBoltInput contract = new MockContractsBoltInput();
        contract.input1 = null;
        contract.optionalInput2 = Optional.of(1);
        ValidatedContract<MockContractsBoltInput> validationResult = ContractValidator.instance().validate(contract);
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

    @Test
    public void testWithListValid() {
        MockContractsWithListOutput contract = new MockContractsWithListOutput();
        contract.output1 = 1;
        contract.optionalOutput2 = Optional.absent();
        contract.listOutput = new LinkedList<>();

        ValidatedContract<MockContractsWithListOutput> validationResult = ContractValidator.instance().validate(contract);
        validationResult.toString();
        assertThat(validationResult.isValid()).isTrue();
    }

    @Test
    public void testWithListNotValid() {
        MockContractsWithListOutput contract = new MockContractsWithListOutput();
        contract.output1 = 1;
        contract.optionalOutput2 = Optional.absent();
        contract.listOutput = null;

        ValidatedContract<MockContractsWithListOutput> validationResult = ContractValidator.instance().validate(contract);
        validationResult.toString();
        assertThat(validationResult.isValid()).isFalse();
    }
}
