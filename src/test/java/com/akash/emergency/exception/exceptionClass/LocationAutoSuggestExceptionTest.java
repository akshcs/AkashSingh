package com.akash.emergency.exception.exceptionClass;

import com.akash.emergency.dto.autoSuggest.EmergencySuggestion;
import com.akash.emergency.exception.exceptionClass.LocationAutoSuggestException;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class LocationAutoSuggestExceptionTest {

    @Test
    public void testLocationAutoSuggestException() {
        String threeWordAddress = "index.home.raft";
        List<EmergencySuggestion> suggestions = new ArrayList<>();
        suggestions.add(new EmergencySuggestion("Country1", "Place1", "Words1"));
        suggestions.add(new EmergencySuggestion("Country2", "Place2", "Words2"));

        LocationAutoSuggestException exception = new LocationAutoSuggestException(threeWordAddress, suggestions);

        assertEquals(threeWordAddress, exception.getThreeWordAddress());
        assertEquals("3wa not recognised: " + threeWordAddress, exception.getMessage());
        assertEquals(suggestions, exception.getSuggestions());
    }
}
