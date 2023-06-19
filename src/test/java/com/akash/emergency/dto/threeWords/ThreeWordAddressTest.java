package com.akash.emergency.dto.threeWords;

import com.akash.emergency.dto.threeWords.ThreeWordAddress;
import org.junit.jupiter.api.Test;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
public class ThreeWordAddressTest {
    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    public void testThreeWordAddressValidation_NullAddress() {
        ThreeWordAddress address = new ThreeWordAddress(null);
        Set<ConstraintViolation<ThreeWordAddress>> violations = validator.validate(address);
        assertFalse(violations.isEmpty());
        assertEquals("3wa must not be null", violations.iterator().next().getMessage());
    }

    @Test
    public void testThreeWordAddressValidation_EmptyAddress() {
        ThreeWordAddress address = new ThreeWordAddress("");
        Set<ConstraintViolation<ThreeWordAddress>> violations = validator.validate(address);
        assertFalse(violations.isEmpty());
        assertEquals("3wa address supplied has invalid format", violations.iterator().next().getMessage());
    }

    @Test
    public void testThreeWordAddressValidation_ValidAddress() {
        ThreeWordAddress address = new ThreeWordAddress("valid.address.case");
        Set<ConstraintViolation<ThreeWordAddress>> violations = validator.validate(address);
        assertTrue(violations.isEmpty());
    }

    @Test
    public void testThreeWordAddressValidation_InvalidPattern() {
        ThreeWordAddress address = new ThreeWordAddress("invalid.address");
        Set<ConstraintViolation<ThreeWordAddress>> violations = validator.validate(address);
        assertFalse(violations.isEmpty());
        assertEquals("3wa address supplied has invalid format", violations.iterator().next().getMessage());
    }
}