package com.brandyodhiambo.bibleApi.feature.biblemgt.models.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BibleResponse {
    private Long id;
    private String name;
    private String abbreviation;
    private String description;
    private String language;
    private List<BookResponse> books;
    private LocalDateTime createdAt;
}