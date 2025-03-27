package com.brandyodhiambo.bibleApi.feature.chatmgt.service;

import com.brandyodhiambo.bibleApi.feature.chatmgt.models.ChatMessage;
import com.brandyodhiambo.bibleApi.feature.chatmgt.models.dto.ChatMessageResponse;
import com.brandyodhiambo.bibleApi.feature.chatmgt.repository.ChatMessageRepository;
import com.brandyodhiambo.bibleApi.feature.groupmgt.models.Group;
import com.brandyodhiambo.bibleApi.feature.groupmgt.repository.GroupRepository;
import com.brandyodhiambo.bibleApi.feature.usermgt.models.Users;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChatMessageServiceImpl implements ChatMessageService {

    private final ChatMessageRepository chatMessageRepository;
    private final GroupRepository groupRepository;

    @Override
    public ChatMessageResponse sendMessage(Long groupId, String content) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Users currentUser = (Users) authentication.getPrincipal();

        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new EntityNotFoundException("Group not found with id: " + groupId));

        // Check if user is a member of the group
        if (!group.isMember(currentUser) && !group.isLeader(currentUser)) {
            throw new IllegalStateException("You must be a member of the group to send messages");
        }

        ChatMessage chatMessage = ChatMessage.builder()
                .content(content)
                .group(group)
                .sender(currentUser)
                .build();

        ChatMessage savedMessage = chatMessageRepository.save(chatMessage);
        return mapToResponse(savedMessage);
    }

    @Override
    public List<ChatMessageResponse> getGroupMessages(Long groupId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Users currentUser = (Users) authentication.getPrincipal();

        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new EntityNotFoundException("Group not found with id: " + groupId));

        // Check if user is a member of the group
        if (!group.isMember(currentUser) && !group.isLeader(currentUser)) {
            throw new IllegalStateException("You must be a member of the group to view messages");
        }

        List<ChatMessage> messages = chatMessageRepository.findByGroupIdOrderByCreatedAtDesc(groupId);
        return messages.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public ChatMessageResponse getMessage(Long messageId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Users currentUser = (Users) authentication.getPrincipal();

        ChatMessage message = chatMessageRepository.findById(messageId)
                .orElseThrow(() -> new EntityNotFoundException("Message not found with id: " + messageId));

        Group group = message.getGroup();

        // Check if user is a member of the group
        if (!group.isMember(currentUser) && !group.isLeader(currentUser)) {
            throw new IllegalStateException("You must be a member of the group to view this message");
        }

        return mapToResponse(message);
    }

    @Override
    public void deleteMessage(Long messageId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Users currentUser = (Users) authentication.getPrincipal();

        ChatMessage message = chatMessageRepository.findById(messageId)
                .orElseThrow(() -> new EntityNotFoundException("Message not found with id: " + messageId));

        // Check if user is the sender of the message or the group leader
        if (!message.getSender().equals(currentUser) && !message.getGroup().isLeader(currentUser)) {
            throw new IllegalStateException("You can only delete your own messages or as a group leader");
        }

        chatMessageRepository.delete(message);
    }

    private ChatMessageResponse mapToResponse(ChatMessage chatMessage) {
        return ChatMessageResponse.builder()
                .id(chatMessage.getId())
                .content(chatMessage.getContent())
                .groupId(chatMessage.getGroup().getId())
                .groupName(chatMessage.getGroup().getName())
                .senderId(chatMessage.getSender().getId())
                .senderUsername(chatMessage.getSender().getUsername())
                .createdAt(chatMessage.getCreatedAt())
                .build();
    }
}