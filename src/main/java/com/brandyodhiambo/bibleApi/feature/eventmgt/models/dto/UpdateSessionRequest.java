package com.brandyodhiambo.bibleApi.feature.eventmgt.models.dto;

import com.brandyodhiambo.bibleApi.feature.eventmgt.models.RecurrencePattern;
import com.brandyodhiambo.bibleApi.feature.eventmgt.models.SessionType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateSessionRequest {
    
    private String title;
    
    private String description;
    
    private LocalDate sessionDate;
    
    private LocalTime startTime;
    
    private LocalTime endTime;
    
    private String location;
    
    private SessionType type;
    
    private RecurrencePattern recurrencePattern;
    
    private LocalDate recurrenceEndDate;
}