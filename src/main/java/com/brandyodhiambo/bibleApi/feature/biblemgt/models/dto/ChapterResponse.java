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
public class ChapterResponse {
    private Long id;
    private Integer number;
    private Long bookId;
    private String bookName;
    private List<VerseResponse> verses;
    private LocalDateTime createdAt;
}