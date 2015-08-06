package com.forter.contracts.validation;

import com.forter.contracts.mocks.*;
import com.google.common.base.Optional;
import org.testng.annotations.Test;

import javax.validation.ConstraintViolation;
import java.util.LinkedList;
import java.util.Set;

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
        assertThat(validationResult.isValid()).isFalse();
    }

    @Test
    public void testNullOptional() {
        MockContractsBoltInput contract = new MockContractsBoltInput();
        contract.input1 = 1;
        contract.optionalInput2 = null;
        ValidatedContract<MockContractsBoltInput> validationResult = ContractValidator.instance().validate(contract);
        assertThat(validationResult.isValid()).isFalse();
    }


    @Test
    public void testOptionalAbsent() {
        MockContractsBoltInput contract = new MockContractsBoltInput();
        contract.input1 = 1;
        contract.optionalInput2 = Optional.absent();
        ValidatedContract<MockContractsBoltInput> validationResult = ContractValidator.instance().validate(contract);
        assertThat(validationResult.isValid()).isTrue();
    }

    @Test
    public void testWithListValid() {
        MockContractsWithListOutput contract = new MockContractsWithListOutput();
        contract.output1 = 1;
        contract.optionalOutput2 = Optional.absent();
        contract.listOutput = new LinkedList<>();

        ValidatedContract<MockContractsWithListOutput> validationResult = ContractValidator.instance().validate(contract);
        assertThat(validationResult.isValid()).isTrue();
    }

    @Test
    public void testWithListNullViolation() {
        MockContractsWithListOutput contract = new MockContractsWithListOutput();
        contract.output1 = 1;
        contract.optionalOutput2 = Optional.absent();
        contract.listOutput = null;

        ValidatedContract<MockContractsWithListOutput> validationResult = ContractValidator.instance().validate(contract);
        assertThat(validationResult.isValid()).isFalse();
    }

    @Test
    void testOneOfViolation() {
        MockContractsWithOneOf contract = new MockContractsWithOneOf();
        contract.value = "not_in_the_list";
        ValidatedContract<?> validationResult = ContractValidator.instance().validate(contract);
        assertThat(validationResult.isValid()).isFalse();
    }

    @Test
    void testOneOfValid() {
        MockContractsWithOneOf contract = new MockContractsWithOneOf();
        contract.value = "in_the_list";
        ValidatedContract<?> validationResult = ContractValidator.instance().validate(contract);
        assertThat(validationResult.isValid()).isTrue();
    }

    @Test
    void testIpAddressViolation() {
        MockContractsWithIpAddress contract = new MockContractsWithIpAddress();
        contract.value = "127.0..";
        ValidatedContract<?> validationResult = ContractValidator.instance().validate(contract);
        assertThat(validationResult.isValid()).isFalse();
    }

    @Test
    void testIpAddressValid() {
        MockContractsWithIpAddress contract = new MockContractsWithIpAddress();
        contract.value = "127.0.0.1";
        ValidatedContract<?> validationResult = ContractValidator.instance().validate(contract);
        assertThat(validationResult.isValid()).isTrue();
    }

    @Test
    void testLongIpV6AddressValid() {
        MockContractsWithIpAddress contract = new MockContractsWithIpAddress();
        contract.value = "2001:0db8:85a3:0000:0000:8a2e:0370:7334";
        ValidatedContract<?> validationResult = ContractValidator.instance().validate(contract);
        assertThat(validationResult.isValid()).isTrue();
    }

    @Test
    void testShortIpV6AddressValid() {
        MockContractsWithIpAddress contract = new MockContractsWithIpAddress();
        contract.value = "2001:db8:85a3:0:0:8a2e:370:7334";
        ValidatedContract<?> validationResult = ContractValidator.instance().validate(contract);
        assertThat(validationResult.isValid()).isTrue();
    }

    @Test
    void testSuperShortIpV6AddressValid() {
        MockContractsWithIpAddress contract = new MockContractsWithIpAddress();
        contract.value = "2001:db8:85a3::8a2e:370:7334";
        ValidatedContract<?> validationResult = ContractValidator.instance().validate(contract);
        assertThat(validationResult.isValid()).isTrue();
    }

    @Test
    void testInvalidSuperShortIpV6AddressValid() {
        MockContractsWithIpAddress contract = new MockContractsWithIpAddress();
        contract.value = "2001:db8:85a3:::8a2e:370:7334";
        ValidatedContract<?> validationResult = ContractValidator.instance().validate(contract);
        assertThat(validationResult.isValid()).isFalse();
    }

}
