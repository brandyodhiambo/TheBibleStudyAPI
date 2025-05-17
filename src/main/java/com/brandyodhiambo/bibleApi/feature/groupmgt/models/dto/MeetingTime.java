package com.brandyodhiambo.bibleApi.feature.groupmgt.models.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class MeetingTime implements Serializable {
    private int hour;
    private int minute;
    private int second;
    private int nano;

    public java.time.LocalTime toLocalTime() {
        return java.time.LocalTime.of(hour, minute, second, nano);
    }
}
