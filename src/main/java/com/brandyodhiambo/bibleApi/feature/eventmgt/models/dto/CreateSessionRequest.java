package com.brandyodhiambo.bibleApi.feature.eventmgt.models.dto;

import com.brandyodhiambo.bibleApi.feature.eventmgt.models.RecurrencePattern;
import com.brandyodhiambo.bibleApi.feature.eventmgt.models.SessionType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateSessionRequest {
    
    @NotBlank(message = "Title is required")
    private String title;
    
    private String description;
    
    @NotNull(message = "Session date is required")
    private LocalDate sessionDate;
    
    @NotNull(message = "Start time is required")
    private LocalTime startTime;
    
    private LocalTime endTime;
    
    private String location;
    
    @NotNull(message = "Session type is required")
    private SessionType type;
    
    @NotNull(message = "Group ID is required")
    private Long groupId;
    
    private RecurrencePattern recurrencePattern = RecurrencePattern.NONE;
    
    private LocalDate recurrenceEndDate;
}