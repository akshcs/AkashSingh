package com.akash.emergency.dto.location;
import org.junit.jupiter.api.Test;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import java.util.Iterator;

import static org.assertj.core.api.Assertions.assertThat;

public class LocationTest {

    private final Validator validator;

    public LocationTest() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    public void testLocationValidation_ValidLocation() {
        Location location = new Location(37.7749, -122.4194);
        var violations = validator.validate(location);
        assertThat(violations).isEmpty();
    }

    @Test
    public void testLocationValidation_InvalidLatitude() {
        Location location = new Location(100.0, -122.4194);
        var violations = validator.validate(location);
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).isEqualTo("Latitude must be between -90.0 and 90.0");
    }

    @Test
    public void testLocationValidation_NullLatitude() {
        Location location = new Location(null, -122.4194);
        var violations = validator.validate(location);
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).isEqualTo("Latitude must not be null/empty");
    }

    @Test
    public void testLocationValidation_NullLongitude() {
        Location location = new Location(37.7749, null);
        var violations = validator.validate(location);
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).isEqualTo("Longitude must not be null/empty");
    }

    @Test
    public void testLocationValidation_NullLatitude_NullLongitude() {
        Location location = new Location(null, null);
        var violations = validator.validate(location);
        assertThat(violations).hasSize(2);
        Iterator<ConstraintViolation<Location>> it = violations.iterator();
        assertThat(it.next().getMessage()).contains("must not be null/empty");
        if(it.hasNext()){
            assertThat(it.next().getMessage()).contains("must not be null/empty");
        }
    }
}






