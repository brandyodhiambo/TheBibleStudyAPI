package com.brandyodhiambo.bibleApi.feature.chatmgt.models.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatMessageResponse {
    private Long id;
    private String content;
    private Long groupId;
    private String groupName;
    private Long senderId;
    private String senderUsername;
    private LocalDateTime createdAt;
}