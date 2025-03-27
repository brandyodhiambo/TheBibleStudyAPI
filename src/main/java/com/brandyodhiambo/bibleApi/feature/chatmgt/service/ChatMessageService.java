package com.brandyodhiambo.bibleApi.feature.chatmgt.service;

import com.brandyodhiambo.bibleApi.feature.chatmgt.models.ChatMessage;
import com.brandyodhiambo.bibleApi.feature.chatmgt.models.dto.ChatMessageResponse;

import java.util.List;

public interface ChatMessageService {
    ChatMessageResponse sendMessage(Long groupId, String content);
    List<ChatMessageResponse> getGroupMessages(Long groupId);
    ChatMessageResponse getMessage(Long messageId);
    void deleteMessage(Long messageId);
}