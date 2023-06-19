package com.akash.emergency.response.error;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UnableToFind3waErrorResponse {
    @Builder.Default
    private String message = "Coordinates supplied do not convert to a 3wa";
}
