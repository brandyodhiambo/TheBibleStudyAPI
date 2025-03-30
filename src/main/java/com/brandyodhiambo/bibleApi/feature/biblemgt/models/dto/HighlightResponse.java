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
public class HighlightResponse {
    private Long id;
    private Long verseId;
    private Integer verseNumber;
    private String verseText;
    private Long chapterId;
    private Integer chapterNumber;
    private Long bookId;
    private String bookName;
    private Long userId;
    private String username;
    private String color;
    private LocalDateTime createdAt;
}