package com.akash.emergency.exception.exceptionHandler;

import com.akash.emergency.dto.autoSuggest.EmergencySuggestion;
import com.akash.emergency.dto.location.Location;
import com.akash.emergency.dto.threeWords.ThreeWordAddress;
import com.akash.emergency.exception.exceptionClass.*;
import com.akash.emergency.response.AutoSuggestLocationResponse;
import com.akash.emergency.response.error.ErrorResponse;
import com.akash.emergency.response.error.RequestValidationErrorResponse;
import com.akash.emergency.response.error.UnableToFind3waErrorResponse;
import com.akash.emergency.service.impl.EmergencyServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

public class GlobalExceptionHandlerTest {
    @Mock
    private BindingResult bindingResult;
    @Mock
    private MethodArgumentNotValidException methodArgumentNotValidException;
    @Mock
    private FieldError fieldError;
    private GlobalExceptionHandler exceptionHandler;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        exceptionHandler = new GlobalExceptionHandler();
        List<FieldError> fieldErrors = new ArrayList<>();
        fieldErrors.add(fieldError);
        when(bindingResult.getFieldErrors()).thenReturn(fieldErrors);
    }

    @Test
    public void handleRequestValidationException_InvalidThreeWordAddress() throws NoSuchMethodException {
        Method method = EmergencyServiceImpl.class.getMethod("_3waToCoord", ThreeWordAddress.class);
        String inValid3waErrorMessage = "3wa address supplied has invalid format";
        updateMethodArgumentNotValidException(inValid3waErrorMessage, method);
        ResponseEntity<ErrorResponse> response = exceptionHandler.
                handleRequestValidationException(methodArgumentNotValidException);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isInstanceOf(ErrorResponse.class);
        assertThat(Objects.requireNonNull(response.getBody()).getMessage()).
                isEqualTo(inValid3waErrorMessage);
    }

    @Test
    public void handleRequestValidationException_InvalidRequest() throws NoSuchMethodException {
        Method method = EmergencyServiceImpl.class.getMethod("coordTo3wa", Location.class);
        String inValidLatitudeMessage = "Latitude must be between -90.0 and 90.0";
        updateMethodArgumentNotValidException(inValidLatitudeMessage, method);
        ResponseEntity<ErrorResponse> response = exceptionHandler.
                handleRequestValidationException(methodArgumentNotValidException);
        assertThat(response.getBody()).isInstanceOf(RequestValidationErrorResponse.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(Objects.requireNonNull(response.getBody()).
                getMessage()).isEqualTo("Incorrect Request");
        assertThat(((RequestValidationErrorResponse) response.getBody()).getErrors()).hasSize(1);
        assertThat(((RequestValidationErrorResponse) response.getBody()).getErrors().get(0)).isEqualTo(inValidLatitudeMessage);
    }

    @Test
    public void handleUnableToFind3waException() {
        UnableToFind3waException ex = new UnableToFind3waException("Some Error", new RuntimeException());
        ResponseEntity<UnableToFind3waErrorResponse> response = exceptionHandler.
                handleUnableToFind3waException(ex);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_IMPLEMENTED);
        assertThat(Objects.requireNonNull(response.getBody()).getMessage()).
                isEqualTo("Coordinates supplied do not convert to a 3wa");
    }

    @Test
    public void handleLocationUnserviceableException() {
        LocationUnserviceableException ex = new LocationUnserviceableException("Location is unserviceable");
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleLocationUnserviceableException(ex);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_ACCEPTABLE);
        assertThat(response.getBody()).isInstanceOf(ErrorResponse.class);
        assertThat(Objects.requireNonNull(response.getBody()).
                getMessage()).isEqualTo("Location is unserviceable");
    }

    @Test
    public void handleInvalid3waException() {
        Invalid3waException ex = new Invalid3waException("Invalid 3wa", new RuntimeException());
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleInvalid3waException(ex);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isInstanceOf(ErrorResponse.class);
        assertThat(Objects.requireNonNull(response.getBody()).
                getMessage()).isEqualTo("Invalid 3wa");
    }

    @Test
    public void handleLocationAutoSuggestException() {
        List<EmergencySuggestion> suggestions = new ArrayList<>();
        LocationAutoSuggestException ex = new LocationAutoSuggestException("word", suggestions);
        ResponseEntity<AutoSuggestLocationResponse> response = exceptionHandler.handleLocationAutoSuggestException(ex);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.SEE_OTHER);
        assertThat(response.getBody()).isInstanceOf(AutoSuggestLocationResponse.class);
        assertThat(Objects.requireNonNull(response.getBody()).getMessage()).isEqualTo("3wa not recognised: word");
        assertThat(response.getBody().getSuggestions()).isEqualTo(suggestions);
    }

    @Test
    public void handleInvalidLanguageCodeException() {
        InvalidLanguageCodeException ex = new InvalidLanguageCodeException("Invalid language code");
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleInvalidLanguageCodeException(ex);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isInstanceOf(ErrorResponse.class);
        assertThat(Objects.requireNonNull(response.getBody()).getMessage()).isEqualTo("Invalid language code is not a valid language code");
    }

    @Test
    public void handleException() {
        Exception ex = new Exception("Generic exception");
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleException(ex);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody()).isInstanceOf(ErrorResponse.class);
        assertThat(Objects.requireNonNull(response.getBody()).getMessage()).isEqualTo("Generic exception");
    }

    private void updateMethodArgumentNotValidException(String errorMessage, Method method) {
        List<String> errorMessages = new ArrayList<>();
        errorMessages.add(errorMessage);
        when(fieldError.getDefaultMessage()).thenReturn(errorMessage);
        MethodParameter methodParameter = new MethodParameter(method, 0);
        methodArgumentNotValidException = new MethodArgumentNotValidException(methodParameter, bindingResult);
    }
}