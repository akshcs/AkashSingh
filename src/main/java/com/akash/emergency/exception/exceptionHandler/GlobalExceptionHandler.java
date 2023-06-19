package com.akash.emergency.exception.exceptionHandler;

import com.akash.emergency.exception.exceptionClass.*;
import com.akash.emergency.response.AutoSuggestLocationResponse;
import com.akash.emergency.response.error.ErrorResponse;
import com.akash.emergency.response.error.RequestValidationErrorResponse;
import com.akash.emergency.response.error.UnableToFind3waErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.ArrayList;
import java.util.List;

@ControllerAdvice
public class GlobalExceptionHandler {
    private final String inValidFormatMessage = "invalid format";
    private final String ThreeWordAddressClassName = "ThreeWordAddress";

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleRequestValidationException(MethodArgumentNotValidException ex) {
        List<String> errorMessages = getErrorMessagesFromMethodArgumentNotValidException(ex);
        ResponseEntity.BodyBuilder response = ResponseEntity.status(HttpStatus.BAD_REQUEST);
        return returnErrorResponseValidationException(ex, response, errorMessages);
    }

    @ExceptionHandler(UnableToFind3waException.class)
    public ResponseEntity<UnableToFind3waErrorResponse> handleUnableToFind3waException(UnableToFind3waException ex) {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).body(UnableToFind3waErrorResponse.builder()
                .build());
    }

    @ExceptionHandler(LocationUnserviceableException.class)
    public ResponseEntity<ErrorResponse> handleLocationUnserviceableException(LocationUnserviceableException ex) {
        return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(new ErrorResponse(ex.getMessage()));
    }

    @ExceptionHandler(Invalid3waException.class)
    public ResponseEntity<ErrorResponse> handleInvalid3waException(Invalid3waException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse(ex.getMessage()));
    }

    @ExceptionHandler(LocationAutoSuggestException.class)
    public ResponseEntity<AutoSuggestLocationResponse> handleLocationAutoSuggestException(LocationAutoSuggestException ex) {
        return ResponseEntity.status(HttpStatus.SEE_OTHER).body(new
                AutoSuggestLocationResponse(ex.getMessage(), ex.getSuggestions()));
    }

    @ExceptionHandler(InvalidLanguageCodeException.class)
    public ResponseEntity<ErrorResponse> handleInvalidLanguageCodeException(InvalidLanguageCodeException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new
                ErrorResponse(ex.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new
                ErrorResponse(ex.getMessage()));
    }

    private List<String> getErrorMessagesFromMethodArgumentNotValidException(MethodArgumentNotValidException ex){
        BindingResult bindingResult = ex.getBindingResult();
        List<FieldError> fieldErrors = bindingResult.getFieldErrors();
        List<String> errorMessages = new ArrayList<>();
        for (FieldError fieldError : fieldErrors) {
            errorMessages.add(fieldError.getDefaultMessage());
        }
        return errorMessages;
    }

    private boolean isInvalidThreeWordAddress(List<String> errorMessages, MethodArgumentNotValidException ex){
        return ex.getParameter().getParameterType().getName().contains(ThreeWordAddressClassName) &&
                errorMessages.size() == 1 && errorMessages.get(0).contains(inValidFormatMessage);
    }

    private ResponseEntity<ErrorResponse> returnErrorResponseValidationException(MethodArgumentNotValidException ex,
                                                                                 ResponseEntity.BodyBuilder response, List<String> errorMessages){
        if(isInvalidThreeWordAddress(errorMessages, ex)){
            return response.body(new ErrorResponse
                    (errorMessages.get(0)));
        } else {
            return response.body(new RequestValidationErrorResponse
                    ("Incorrect Request", errorMessages));
        }
    }
}