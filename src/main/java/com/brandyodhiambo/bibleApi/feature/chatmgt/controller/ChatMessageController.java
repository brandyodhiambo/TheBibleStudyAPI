package com.brandyodhiambo.bibleApi.feature.chatmgt.controller;

import com.brandyodhiambo.bibleApi.feature.chatmgt.models.dto.ChatMessageResponse;
import com.brandyodhiambo.bibleApi.feature.chatmgt.service.ChatMessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/chat")
@RequiredArgsConstructor
public class ChatMessageController {

    private final ChatMessageService chatMessageService;

    @PostMapping("/groups/{groupId}/messages")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ChatMessageResponse> sendMessage(
            @PathVariable Long groupId,
            @RequestBody Map<String, String> payload) {
        String content = payload.get("content");
        if (content == null || content.trim().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        ChatMessageResponse message = chatMessageService.sendMessage(groupId, content);
        return ResponseEntity.status(HttpStatus.CREATED).body(message);
    }

    @GetMapping("/groups/{groupId}/messages")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<ChatMessageResponse>> getGroupMessages(@PathVariable Long groupId) {
        List<ChatMessageResponse> messages = chatMessageService.getGroupMessages(groupId);
        return ResponseEntity.ok(messages);
    }

    @GetMapping("/messages/{messageId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ChatMessageResponse> getMessage(@PathVariable Long messageId) {
        ChatMessageResponse message = chatMessageService.getMessage(messageId);
        return ResponseEntity.ok(message);
    }

    @DeleteMapping("/messages/{messageId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> deleteMessage(@PathVariable Long messageId) {
        chatMessageService.deleteMessage(messageId);
        return ResponseEntity.noContent().build();
    }
}