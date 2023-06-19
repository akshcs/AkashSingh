package com.akash.emergency.integrationTest;

import com.akash.emergency.dto.autoSuggest.EmergencySuggestion;
import com.akash.emergency.dto.location.Location;
import com.akash.emergency.dto.threeWords.ThreeWordAddress;
import com.akash.emergency.exception.exceptionClass.LocationAutoSuggestException;
import com.akash.emergency.exception.exceptionClass.LocationUnserviceableException;
import com.akash.emergency.exception.exceptionClass.UnableToFind3waException;
import com.akash.emergency.rest.EmergencyResource;
import com.akash.emergency.service.intf.EmergencyService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(EmergencyResource.class)
public class EmergencyResourceIntegrationTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private EmergencyService emergencyService;
    private ThreeWordAddress expectedAddress;
    private Location location;

    private List<EmergencySuggestion> suggestions;
    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        expectedAddress = new ThreeWordAddress("table.book.chair");
        location = new Location(51.515,-0.142);
        suggestions = new ArrayList<>();
        suggestions.add(new EmergencySuggestion("GB",
                "Roydon, Essex", "able.book.chair"));
    }

    // Coordinates to Location
    @Test
    public void coordTo3wa_ValidLocation_ReturnsThreeWordAddress() throws Exception {
        when(emergencyService.coordTo3wa(any())).thenReturn(expectedAddress);
        mockMvc.perform(MockMvcRequestBuilders.post("/coord-to-3wa")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"lat\": 51.515, \"lng\": -0.142}"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.3wa").
                        value("table.book.chair"));
    }

    @Test
    public void coordTo3wa_InValidLatitude() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/coord-to-3wa")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"lat\": 91.515, \"lng\": -0.142}"))
                .andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").
                        value("Incorrect Request"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors").
                        value("Latitude must be between -90.0 and 90.0"));
    }

    @Test
    public void coordTo3wa_NullLatitude() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/coord-to-3wa")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"lat\": null, \"lng\": -0.142}"))
                .andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").
                        value("Incorrect Request"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors").
                        value("Latitude must not be null/empty"));
    }

    @Test
    public void coordTo3wa_NullLongitude() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/coord-to-3wa")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"lat\": 51.515, \"lng\": null}"))
                .andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").
                        value("Incorrect Request"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors").
                        value("Longitude must not be null/empty"));
    }

    @Test
    public void coordTo3wa_NullLatitude_NullLongitude() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/coord-to-3wa")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"lat\": null, \"lng\": null}"))
                .andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").
                        value("Incorrect Request"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors").isArray());
    }

    @Test
    public void coordTo3wa_LocationOutSideAllowedCountries() throws Exception {
        doThrow(new LocationUnserviceableException("Serviceable countries with ISO 3166-1 alpha-2 codes are : GB")).
                when(emergencyService).coordTo3wa(any());
        mockMvc.perform(MockMvcRequestBuilders.post("/coord-to-3wa")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"lat\": 51.515, \"lng\": -0.142}"))
                .andExpect(status().isNotAcceptable())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").
                        value("Serviceable countries with ISO 3166-1 alpha-2 codes are : GB"));
    }

    @Test
    public void coordTo3wa_UnableToFindLocation() throws Exception {
        doThrow(new UnableToFind3waException("Some Error", new RuntimeException())).
                when(emergencyService).coordTo3wa(any());
        mockMvc.perform(MockMvcRequestBuilders.post("/coord-to-3wa")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"lat\": 51.515, \"lng\": -0.142}"))
                .andExpect(status().isNotImplemented())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").
                        value("Coordinates supplied do not convert to a 3wa"));
    }

    //  Location to Coordinates
    @Test
    public void _3waToCoord_ValidThreeWordAddress_ReturnsCoordinate() throws Exception {
        when(emergencyService._3waToCoord(any())).thenReturn(location);
        mockMvc.perform(MockMvcRequestBuilders.post("/3wa-to-coord")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"3wa\": \"table.book.chair\"}"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.lat").
                        value(51.515))
                .andExpect(MockMvcResultMatchers.jsonPath("$.lng").
                        value(-0.142));
    }

    @Test
    public void _3waToCoord_NullThreeWordAddress() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/3wa-to-coord")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"3wa\": null}"))
                .andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").
                        value("Incorrect Request"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors").
                        value("3wa must not be null"));
    }

    @Test
    public void _3waToCoord_EmptyThreeWordAddress() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/3wa-to-coord")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"3wa\": \"\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").
                        value("3wa address supplied has invalid format"));
    }

    @Test
    public void _3waToCoord_InvalidThreeWordAddress() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/3wa-to-coord")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"3wa\": \"klae.dkasj\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").
                        value("3wa address supplied has invalid format"));
    }

    @Test
    public void _3waToCoord_ThreeWordAddress_OutsideServiceableCountries() throws Exception {
        doThrow(new LocationAutoSuggestException(expectedAddress.getThreeWordAddress(),
                suggestions)).when(emergencyService)._3waToCoord(expectedAddress);
        mockMvc.perform(MockMvcRequestBuilders.post("/3wa-to-coord")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"3wa\": \"table.book.chair\"}"))
                .andExpect(status().isSeeOther())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").
                        value("3wa not recognised: table.book.chair"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.suggestions").isArray())
                .andExpect(MockMvcResultMatchers.jsonPath("$.suggestions[0].country").
                        value("GB"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.suggestions[0].nearestPlace").
                        value("Roydon, Essex"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.suggestions[0].words").
                        value("able.book.chair"));
    }

    @Test
    public void _3waToCoord_ThreeWordAddress_NoCoordinatesAvailable() throws Exception {
        doThrow(new LocationAutoSuggestException(expectedAddress.getThreeWordAddress(),
                suggestions)).when(emergencyService)._3waToCoord(expectedAddress);
        mockMvc.perform(MockMvcRequestBuilders.post("/3wa-to-coord")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"3wa\": \"table.book.chair\"}"))
                .andExpect(status().isSeeOther())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").
                        value("3wa not recognised: table.book.chair"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.suggestions").isArray())
                .andExpect(MockMvcResultMatchers.jsonPath("$.suggestions[0].country").
                        value("GB"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.suggestions[0].nearestPlace").
                        value("Roydon, Essex"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.suggestions[0].words").
                        value("able.book.chair"));
    }

    //  Location to Location From other Language
    @Test
    public void _3waLanguageConvert_ValidThreeWordAddress_ReturnsThreeWordAddress() throws Exception {
        when(emergencyService._3waLanguageConvert(any(), any())).thenReturn(expectedAddress);
        mockMvc.perform(MockMvcRequestBuilders.post("/language-convert?target_language=FR")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"3wa\": \"able.book.chair\"}"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.3wa").
                        value("table.book.chair"));
    }

    @Test
    public void _3waLanguageConvert_NullThreeWordAddress() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/language-convert?target_language=FR")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"3wa\": null}"))
                .andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").
                        value("Incorrect Request"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors").
                        value("3wa must not be null"));
    }

    @Test
    public void _3waLanguageConvert_EmptyThreeWordAddress() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/language-convert?target_language=FR")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"3wa\": \"\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").
                        value("3wa address supplied has invalid format"));
    }

    @Test
    public void _3waLanguageConvert_InvalidThreeWordAddress() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/language-convert?target_language=FR")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"3wa\": \"klae.dkasj\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").
                        value("3wa address supplied has invalid format"));
    }

    @Test
    public void _3waLanguageConvert_ThreeWordAddress_OutsideServiceableCountries() throws Exception {
        doThrow(new LocationAutoSuggestException(expectedAddress.getThreeWordAddress(),
                suggestions)).when(emergencyService)._3waLanguageConvert(expectedAddress, "FR");
        mockMvc.perform(MockMvcRequestBuilders.post("/language-convert?target_language=FR")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"3wa\": \"table.book.chair\"}"))
                .andExpect(status().isSeeOther())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").
                        value("3wa not recognised: table.book.chair"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.suggestions").isArray())
                .andExpect(MockMvcResultMatchers.jsonPath("$.suggestions[0].country").
                        value("GB"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.suggestions[0].nearestPlace").
                        value("Roydon, Essex"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.suggestions[0].words").
                        value("able.book.chair"));
    }

    @Test
    public void _3waLanguageConvert_ThreeWordAddress_NoCoordinatesAvailable() throws Exception {
        doThrow(new LocationAutoSuggestException(expectedAddress.getThreeWordAddress(),
                suggestions)).when(emergencyService)._3waLanguageConvert(expectedAddress, "FR");
        mockMvc.perform(MockMvcRequestBuilders.post("/language-convert?target_language=FR")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"3wa\": \"table.book.chair\"}"))
                .andExpect(status().isSeeOther())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").
                        value("3wa not recognised: table.book.chair"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.suggestions").isArray())
                .andExpect(MockMvcResultMatchers.jsonPath("$.suggestions[0].country").
                        value("GB"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.suggestions[0].nearestPlace").
                        value("Roydon, Essex"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.suggestions[0].words").
                        value("able.book.chair"));
    }
}