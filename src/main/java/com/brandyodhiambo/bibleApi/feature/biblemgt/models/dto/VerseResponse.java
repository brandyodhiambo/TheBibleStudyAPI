package com.brandyodhiambo.bibleApi.feature.biblemgt.models.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VerseResponse {
    private Long id;
    private Integer number;
    private String text;
    private Long chapterId;
    private Integer chapterNumber;
    private Long bookId;
    private String bookName;
    private LocalDateTime createdAt;
}