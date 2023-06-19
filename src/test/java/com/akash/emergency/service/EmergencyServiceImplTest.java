package com.akash.emergency.service;

import com.akash.emergency.dto.location.Location;
import com.akash.emergency.dto.threeWords.ThreeWordAddress;
import com.akash.emergency.exception.exceptionClass.InvalidLanguageCodeException;
import com.akash.emergency.exception.exceptionClass.LocationAutoSuggestException;
import com.akash.emergency.exception.exceptionClass.LocationUnserviceableException;
import com.akash.emergency.exception.exceptionClass.UnableToFind3waException;
import com.akash.emergency.service.impl.EmergencyServiceImpl;
import com.akash.emergency.service.intf.EmergencyService;
import com.what3words.javawrapper.What3WordsV3;
import com.what3words.javawrapper.request.AutosuggestRequest;
import com.what3words.javawrapper.request.ConvertTo3WARequest;
import com.what3words.javawrapper.request.ConvertToCoordinatesRequest;
import com.what3words.javawrapper.request.Coordinates;
import com.what3words.javawrapper.response.Autosuggest;
import com.what3words.javawrapper.response.ConvertTo3WA;
import com.what3words.javawrapper.response.ConvertToCoordinates;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class EmergencyServiceImplTest {
    @Mock
    private What3WordsV3 what3WordsV3;
    @Mock
    private ConvertTo3WARequest.Builder builder;

    @Mock
    private AutosuggestRequest.Builder autosuggestRequestBuilder;
    @Mock
    private ConvertToCoordinatesRequest.Builder convertToCoordinatesBuilder;
    @Mock
    private Autosuggest autosuggest;
    private EmergencyService emergencyService;
    private final String words = "table.book.chair";
    private final double lat = 37.7749;
    private final double lng = -122.4194;
    private Location location;
    private com.what3words.javawrapper.response.Coordinates coordinates;
    private ThreeWordAddress threeWordAddress;
    private ConvertTo3WA convertTo3WA;
    private ConvertToCoordinates convertToCoordinates;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        createEmergencyServiceObject();
        mandatoryConditions();
        init();
    }
    private void init(){
        location = new Location(lat, lng);
        coordinates = new com.what3words.javawrapper.response.Coordinates(lat, lng);
        threeWordAddress = new ThreeWordAddress("table.book.chair");
        convertTo3WA = createConvertTo3WA("US", words);
        convertToCoordinates = createConvertToCoordinates("US", coordinates);
    }
    private void createEmergencyServiceObject(){
        String defaultLanguage = "en";
        List<String> allowedCountries = Arrays.asList("US", "CA");
        int numberOfSuggestionsPerCountry = 3;
        emergencyService = new EmergencyServiceImpl(defaultLanguage, allowedCountries, numberOfSuggestionsPerCountry, what3WordsV3);
    }

    private void mandatoryConditions(){
        when(what3WordsV3.convertTo3wa(any(Coordinates.class))).thenReturn(builder);
        when(builder.language(any())).thenReturn(builder);
        when(what3WordsV3.convertToCoordinates(anyString())).thenReturn(convertToCoordinatesBuilder);
        when(what3WordsV3.autosuggest(anyString())).thenReturn(autosuggestRequestBuilder);
        when(autosuggestRequestBuilder.clipToCountry(any())).thenReturn(autosuggestRequestBuilder);
        when(autosuggestRequestBuilder.execute()).thenReturn(autosuggest);
        when(autosuggest.getSuggestions()).thenReturn(Collections.emptyList());
    }

    @Test
    public void coordTo3wa_ValidLocation_ReturnsThreeWordAddress() {
        when(builder.execute()).thenReturn(convertTo3WA);
        ThreeWordAddress result = emergencyService.coordTo3wa(location);
        assertNotNull(result);
        assertEquals(words, result.getThreeWordAddress());
    }

    @Test
    public void coordTo3wa_UnserviceableLocation_ThrowsLocationUnserviceableException() {
        when(builder.execute()).thenReturn(createConvertTo3WA("GB", words));
        assertThrows(LocationUnserviceableException.class, () -> emergencyService.coordTo3wa(location));
    }

    @Test
    public void coordTo3wa_Null_ThrowsLocationUnserviceableException() {
        when(builder.execute()).thenReturn(null);
        assertThrows(UnableToFind3waException.class, () -> emergencyService.coordTo3wa(location));
    }

    @Test
    public void coordTo3wa_ExceptionThrown_ThrowsUnableToFind3waException() {
        when(what3WordsV3.convertTo3wa(any(Coordinates.class))).thenThrow(RuntimeException.class);
        assertThrows(UnableToFind3waException.class, () -> emergencyService.coordTo3wa(location));
    }

    @Test
    public void _3waToCoord_ValidThreeWordAddress_ReturnsLocation() {
        when((convertToCoordinatesBuilder.execute())).thenReturn(convertToCoordinates);
        Location result = emergencyService._3waToCoord(new ThreeWordAddress("table.book.chair"));
        assertNotNull(result);
        assertEquals(lat, result.getLat(), 0.001);
        assertEquals(lng, result.getLng(), 0.001);
    }

    @Test
    public void _3waToCoord_ValidThreeWordAddress_UnAllowedCountry() {
        ConvertToCoordinates convertToCoordinates = createConvertToCoordinates("GB", coordinates);
        when((convertToCoordinatesBuilder.execute())).thenReturn(convertToCoordinates);
        assertThrows(LocationAutoSuggestException.class, () -> emergencyService._3waToCoord(threeWordAddress));
    }

    @Test
    public void _3waToCoord_InValidThreeWordAddress_Null() {
        when((convertToCoordinatesBuilder.execute())).thenReturn(null);
        assertThrows(NullPointerException.class, () -> emergencyService._3waToCoord(threeWordAddress));
    }

    @Test
    public void _3waToCoord_InValidThreeWordAddress_Empty() {
        ConvertToCoordinates convertToCoordinates = createConvertToCoordinates(null, null);
        when((convertToCoordinatesBuilder.execute())).thenReturn(convertToCoordinates);
        assertThrows(LocationAutoSuggestException.class, () -> emergencyService._3waToCoord(threeWordAddress));
    }

    @Test
    public void _3waLanguageConvert_ValidThreeWordAddress_ValidLanguage() {
        when((convertToCoordinatesBuilder.execute())).thenReturn(convertToCoordinates);
        String wrd = "tableau.livre.chaise";
        String lng = "FR";
        when(builder.execute()).thenReturn(createConvertTo3WA(lng, wrd));
        ThreeWordAddress result = emergencyService._3waLanguageConvert(threeWordAddress, lng);
        assertEquals(wrd, result.getThreeWordAddress());
    }

    @Test
    public void _3waLanguageConvert_ValidThreeWordAddress_InValidLanguage() {
        when((convertToCoordinatesBuilder.execute())).thenReturn(convertToCoordinates);
        String lng = "ABR";
        when(builder.execute()).thenReturn(createConvertTo3WA(lng, null));
        assertThrows(InvalidLanguageCodeException.class, () -> emergencyService._3waLanguageConvert(threeWordAddress, lng));
    }

    @Test
    public void _3waLanguageConvert_ValidThreeWordAddress_InValidLanguage_ThrowsException() {
        when((convertToCoordinatesBuilder.execute())).thenReturn(convertToCoordinates);
        String lng = "FR";
        doThrow(new RuntimeException()).when(builder).execute();
        assertThrows(RuntimeException.class, () -> emergencyService._3waLanguageConvert(threeWordAddress, lng));
    }

    @Test
    public void _3waLanguageConvert_InValidThreeWordAddress() {
        when((convertToCoordinatesBuilder.execute())).thenReturn(convertToCoordinates);
        String lng = "ABR";
        when(builder.execute()).thenReturn(createConvertTo3WA(lng, null));
        assertThrows(InvalidLanguageCodeException.class, () -> emergencyService._3waLanguageConvert(threeWordAddress, lng));
    }

    @Test
    public void _3waLanguageConvert_ValidThreeWordAddress_UnAllowedCountry() {
        ConvertToCoordinates convertToCoordinates = createConvertToCoordinates("GB", coordinates);
        when((convertToCoordinatesBuilder.execute())).thenReturn(convertToCoordinates);
        String lng = "FR";
        assertThrows(LocationAutoSuggestException.class, () -> emergencyService._3waLanguageConvert(threeWordAddress, lng));
    }

    @Test
    public void _3waLanguageConvert_InValidThreeWordAddress_Null() {
        when((convertToCoordinatesBuilder.execute())).thenReturn(null);
        assertThrows(NullPointerException.class, () -> emergencyService._3waLanguageConvert(threeWordAddress, "FR"));
    }

    @Test
    public void _3waLanguageConvert_InValidThreeWordAddress_Empty() {
        ConvertToCoordinates convertToCoordinates = createConvertToCoordinates(null, null);
        when((convertToCoordinatesBuilder.execute())).thenReturn(convertToCoordinates);
        assertThrows(LocationAutoSuggestException.class, () -> emergencyService._3waLanguageConvert(threeWordAddress, "FR"));
    }

    private ConvertTo3WA createConvertTo3WA(String country, String word){
        return new ConvertTo3WA(country, null, null,null, word, null, null);
    }

    private ConvertToCoordinates createConvertToCoordinates(String country, com.what3words.javawrapper.response.Coordinates coordinates){
        return new ConvertToCoordinates(country, null, null, coordinates, null, null, null);
    }
}
