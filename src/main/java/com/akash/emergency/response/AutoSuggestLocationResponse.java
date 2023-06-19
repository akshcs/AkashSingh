package com.akash.emergency.response;

import com.akash.emergency.dto.autoSuggest.EmergencySuggestion;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AutoSuggestLocationResponse {
    private String message;
    private List<EmergencySuggestion> suggestions;
}
