package com.brandyodhiambo.bibleApi.feature.eventmgt.models;

public enum RecurrencePattern {
    NONE,       // One-time event
    DAILY,      // Repeats every day
    WEEKLY,     // Repeats every week on the same day
    BI_WEEKLY,  // Repeats every two weeks
    MONTHLY,    // Repeats every month on the same date
    CUSTOM      // Custom recurrence pattern
}