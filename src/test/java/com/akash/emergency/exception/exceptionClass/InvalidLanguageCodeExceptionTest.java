package com.akash.emergency.exception.exceptionClass;
import com.akash.emergency.exception.exceptionClass.InvalidLanguageCodeException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class InvalidLanguageCodeExceptionTest {

    @Test
    public void testInvalidLanguageCodeException() {
        String languageCode = "XYZ";
        String expectedMessage = "XYZ is not a valid language code";
        InvalidLanguageCodeException exception = new InvalidLanguageCodeException(languageCode);
        assertEquals(languageCode, exception.getLanguageCode());
        assertEquals(expectedMessage, exception.getMessage());
    }
}