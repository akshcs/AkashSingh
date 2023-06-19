package com.akash.emergency.exception.exceptionClass;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InvalidLanguageCodeException extends RuntimeException{
    private String languageCode;
    private String message;
    public InvalidLanguageCodeException(String languageCode){
        this.languageCode = languageCode;
        this.message = getInvalidLanguageCodeResponse(languageCode);
    }

    private String getInvalidLanguageCodeResponse(String languageCode){
        return (new StringBuilder(languageCode).append(" is not a valid language code")).toString();
    }
}
