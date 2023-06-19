package com.akash.emergency.rest;

import com.akash.emergency.dto.location.Location;
import com.akash.emergency.dto.threeWords.ThreeWordAddress;
import com.akash.emergency.exception.exceptionClass.LocationAutoSuggestException;
import com.akash.emergency.exception.exceptionClass.LocationUnserviceableException;
import com.akash.emergency.exception.exceptionClass.UnableToFind3waException;
import com.akash.emergency.service.intf.EmergencyService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class EmergencyResourceTest {
    @Mock
    private EmergencyService emergencyService;

    private EmergencyResource emergencyResource;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        emergencyResource = new EmergencyResource(emergencyService);
    }

    @Test
    public void coordTo3wa_ValidLocation_ReturnsThreeWordAddress() {
        Location location = new Location(37.8749, -122.5194);
        ThreeWordAddress expectedAddress = new ThreeWordAddress("table.book.chair");
        when(emergencyService.coordTo3wa(location)).thenReturn(expectedAddress);
        ResponseEntity<ThreeWordAddress> response = emergencyResource.coordTo3wa(location);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedAddress, response.getBody());
    }

    @Test
    public void coordTo3wa_LocationUnserviceable() {
        Location location = new Location(70.0, 200.0);
        doThrow(new LocationUnserviceableException("Random Message")).when(emergencyService).coordTo3wa(location);
        assertThrows(LocationUnserviceableException.class, () -> emergencyResource.coordTo3wa(location));
    }

    @Test
    public void coordTo3wa_UnableToFind3wa() {
        Location location = new Location(80.0, 200.0);
        doThrow(new UnableToFind3waException("Random Message", new RuntimeException())).when(emergencyService).coordTo3wa(location);
        assertThrows(UnableToFind3waException.class, () -> emergencyResource.coordTo3wa(location));
    }

    @Test
    public void _3waToCoord_ValidThreeWordAddress_ReturnsLocation() {
        ThreeWordAddress threeWordAddress = new ThreeWordAddress("table.book.chair");
        Location expectedLocation = new Location(37.749, -122.494);
        when(emergencyService._3waToCoord(threeWordAddress)).thenReturn(expectedLocation);
        ResponseEntity<Location> response = emergencyResource._3waToCoord(threeWordAddress);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedLocation, response.getBody());
    }

    @Test
    public void _3waToCoord_LocationUnserviceable() {
        ThreeWordAddress threeWordAddress = new ThreeWordAddress("table.book.chair");
        doThrow(new LocationAutoSuggestException(threeWordAddress.getThreeWordAddress(), Collections.emptyList())).when(emergencyService)._3waToCoord(threeWordAddress);
        assertThrows(LocationAutoSuggestException.class, () -> emergencyResource._3waToCoord(threeWordAddress));
    }

    @Test
    public void _3waToCoord_WrongThreeWords() {
        ThreeWordAddress threeWordAddress = new ThreeWordAddress("tabgfble.bsdfgook.chgsfair");
        doThrow(new LocationAutoSuggestException(threeWordAddress.getThreeWordAddress(), Collections.emptyList())).when(emergencyService)._3waToCoord(threeWordAddress);
        assertThrows(LocationAutoSuggestException.class, () -> emergencyResource._3waToCoord(threeWordAddress));
    }

    @Test
    public void _3waLanguageConvert_ValidInput_ReturnsThreeWordAddress() {
        String targetLanguage = "fr";
        ThreeWordAddress inputAddress = new ThreeWordAddress("table.book.chair");
        ThreeWordAddress expectedAddress = new ThreeWordAddress("tableau.livre.chaise");
        when(emergencyService._3waLanguageConvert(inputAddress, targetLanguage)).thenReturn(expectedAddress);
        ResponseEntity<ThreeWordAddress> response = emergencyResource._3waLanguageConvert(targetLanguage, inputAddress);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedAddress, response.getBody());
    }

    @Test
    public void _3waLanguageConvert_LocationUnserviceable() {
        String targetLanguage = "fr";
        ThreeWordAddress inputAddress = new ThreeWordAddress("table.book.chair");
        doThrow(new LocationAutoSuggestException(inputAddress.getThreeWordAddress(), Collections.emptyList())).when(emergencyService)._3waLanguageConvert(inputAddress, targetLanguage);
        assertThrows(LocationAutoSuggestException.class, () -> emergencyResource._3waLanguageConvert(targetLanguage, inputAddress));
    }

    @Test
    public void _3waLanguageConvert_WrongThreeWords() {
        String targetLanguage = "fr";
        ThreeWordAddress inputAddress = new ThreeWordAddress("tabsgsle.boosxck.chsxcair");
        doThrow(new LocationAutoSuggestException(inputAddress.getThreeWordAddress(), Collections.emptyList())).when(emergencyService)._3waLanguageConvert(inputAddress, targetLanguage);
        assertThrows(LocationAutoSuggestException.class, () -> emergencyResource._3waLanguageConvert(targetLanguage, inputAddress));
    }
}

