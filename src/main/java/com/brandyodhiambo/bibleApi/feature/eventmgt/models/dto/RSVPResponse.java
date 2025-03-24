package com.brandyodhiambo.bibleApi.feature.eventmgt.models.dto;

import com.brandyodhiambo.bibleApi.feature.eventmgt.models.RSVPStatus;
import com.brandyodhiambo.bibleApi.feature.usermgt.models.dto.UserSummary;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RSVPResponse {
    
    private Long id;
    
    private Long sessionId;
    
    private String sessionTitle;
    
    private UserSummary user;
    
    private RSVPStatus status;
    
    private String comment;
    
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;
}