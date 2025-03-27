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
public class BookResponse {
    private Long id;
    private String name;
    private String abbreviation;
    private Integer position;
    private String description;
    private Long bibleId;
    private String bibleName;
    private List<ChapterResponse> chapters;
    private LocalDateTime createdAt;
}