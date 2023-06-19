package com.akash.emergency.exception.exceptionClass;

import com.akash.emergency.dto.autoSuggest.EmergencySuggestion;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LocationAutoSuggestException extends RuntimeException{
    private String threeWordAddress;
    private String message;
    private List<EmergencySuggestion> suggestions;
    public LocationAutoSuggestException(String threeWordAddress, List<EmergencySuggestion> suggestions){
        this.suggestions = suggestions;
        this.threeWordAddress = threeWordAddress;
        this.message = getMessageForThreeWordAddress();
    }

    private String getMessageForThreeWordAddress() {
        return new StringBuilder("3wa not recognised: ").append(threeWordAddress).toString();
    }
}
