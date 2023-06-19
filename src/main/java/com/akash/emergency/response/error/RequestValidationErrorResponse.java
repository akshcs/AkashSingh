package com.akash.emergency.response.error;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RequestValidationErrorResponse extends ErrorResponse {
    private String message;
    private List<String> errors;
}
