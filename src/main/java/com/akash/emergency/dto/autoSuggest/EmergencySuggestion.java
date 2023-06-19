package com.akash.emergency.dto.autoSuggest;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmergencySuggestion {
    private String country;
    private String nearestPlace;
    private String words;
}
