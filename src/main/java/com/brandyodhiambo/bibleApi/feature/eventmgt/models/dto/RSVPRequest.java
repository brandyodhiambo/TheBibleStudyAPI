package com.brandyodhiambo.bibleApi.feature.eventmgt.models.dto;

import com.brandyodhiambo.bibleApi.feature.eventmgt.models.RSVPStatus;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RSVPRequest {
    
    @NotNull(message = "RSVP status is required")
    private RSVPStatus status;
    
    private String comment;
}