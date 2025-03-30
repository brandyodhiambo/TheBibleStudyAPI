package com.brandyodhiambo.bibleApi.feature.groupmgt.models.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MeetingTime {
    private int hour;
    private int minute;
    private int second;
    private int nano;
    
    public java.time.LocalTime toLocalTime() {
        return java.time.LocalTime.of(hour, minute, second, nano);
    }
}