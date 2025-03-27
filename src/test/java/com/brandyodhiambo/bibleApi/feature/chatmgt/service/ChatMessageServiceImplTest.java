package com.brandyodhiambo.bibleApi.feature.chatmgt.service;

import com.brandyodhiambo.bibleApi.feature.chatmgt.models.ChatMessage;
import com.brandyodhiambo.bibleApi.feature.chatmgt.models.dto.ChatMessageResponse;
import com.brandyodhiambo.bibleApi.feature.chatmgt.repository.ChatMessageRepository;
import com.brandyodhiambo.bibleApi.feature.groupmgt.models.Group;
import com.brandyodhiambo.bibleApi.feature.groupmgt.repository.GroupRepository;
import com.brandyodhiambo.bibleApi.feature.usermgt.models.Users;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ChatMessageServiceImplTest {

    @Mock
    private ChatMessageRepository chatMessageRepository;

    @Mock
    private GroupRepository groupRepository;

    @Mock
    private Authentication authentication;

    @Mock
    private SecurityContext securityContext;

    @InjectMocks
    private ChatMessageServiceImpl chatMessageService;

    private Users user;
    private Group group;
    private ChatMessage chatMessage;

    // Helper method to set the id field of Users using reflection
    private void setUserId(Users user, Long id) {
        try {
            Field idField = Users.class.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(user, id);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @BeforeEach
    void setUp() {
        user = new Users();
        setUserId(user, 1L);
        user.setUsername("testuser");

        group = new Group();
        group.setId(1L);
        group.setName("Test Group");
        group.setLeader(user);
        group.setMembers(new HashSet<>(Arrays.asList(user)));

        chatMessage = ChatMessage.builder()
                .id(1L)
                .content("Test message")
                .group(group)
                .sender(user)
                .createdAt(LocalDateTime.now())
                .build();

        // Setup security context
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(authentication.getPrincipal()).thenReturn(user);
    }

    @Test
    void sendMessage_Success() {
        // Arrange
        when(groupRepository.findById(1L)).thenReturn(Optional.of(group));
        when(chatMessageRepository.save(any(ChatMessage.class))).thenReturn(chatMessage);

        // Act
        ChatMessageResponse response = chatMessageService.sendMessage(1L, "Test message");

        // Assert
        assertNotNull(response);
        assertEquals("Test message", response.getContent());
        assertEquals(1L, response.getGroupId());
        assertEquals("Test Group", response.getGroupName());
        assertEquals(1L, response.getSenderId());
        assertEquals("testuser", response.getSenderUsername());
        verify(chatMessageRepository, times(1)).save(any(ChatMessage.class));
    }

    @Test
    void sendMessage_GroupNotFound() {
        // Arrange
        when(groupRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> {
            chatMessageService.sendMessage(1L, "Test message");
        });
        verify(chatMessageRepository, never()).save(any(ChatMessage.class));
    }

    @Test
    void sendMessage_UserNotMember() {
        // Arrange
        Users nonMember = new Users();
        setUserId(nonMember, 2L);
        nonMember.setUsername("nonmember");

        when(authentication.getPrincipal()).thenReturn(nonMember);
        when(groupRepository.findById(1L)).thenReturn(Optional.of(group));

        // Act & Assert
        assertThrows(IllegalStateException.class, () -> {
            chatMessageService.sendMessage(1L, "Test message");
        });
        verify(chatMessageRepository, never()).save(any(ChatMessage.class));
    }

    @Test
    void getGroupMessages_Success() {
        // Arrange
        when(groupRepository.findById(1L)).thenReturn(Optional.of(group));
        when(chatMessageRepository.findByGroupIdOrderByCreatedAtDesc(1L))
                .thenReturn(Arrays.asList(chatMessage));

        // Act
        List<ChatMessageResponse> responses = chatMessageService.getGroupMessages(1L);

        // Assert
        assertNotNull(responses);
        assertEquals(1, responses.size());
        assertEquals("Test message", responses.get(0).getContent());
        verify(chatMessageRepository, times(1)).findByGroupIdOrderByCreatedAtDesc(1L);
    }

    @Test
    void getMessage_Success() {
        // Arrange
        when(chatMessageRepository.findById(1L)).thenReturn(Optional.of(chatMessage));

        // Act
        ChatMessageResponse response = chatMessageService.getMessage(1L);

        // Assert
        assertNotNull(response);
        assertEquals("Test message", response.getContent());
        assertEquals(1L, response.getGroupId());
        verify(chatMessageRepository, times(1)).findById(1L);
    }

    @Test
    void getMessage_NotFound() {
        // Arrange
        when(chatMessageRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> {
            chatMessageService.getMessage(1L);
        });
    }

    @Test
    void deleteMessage_Success() {
        // Arrange
        when(chatMessageRepository.findById(1L)).thenReturn(Optional.of(chatMessage));

        // Act
        chatMessageService.deleteMessage(1L);

        // Assert
        verify(chatMessageRepository, times(1)).delete(chatMessage);
    }

    @Test
    void deleteMessage_NotFound() {
        // Arrange
        when(chatMessageRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> {
            chatMessageService.deleteMessage(1L);
        });
        verify(chatMessageRepository, never()).delete(any(ChatMessage.class));
    }

    @Test
    void deleteMessage_NotAuthorized() {
        // Arrange
        Users otherUser = new Users();
        setUserId(otherUser, 2L);
        otherUser.setUsername("otheruser");

        ChatMessage otherMessage = ChatMessage.builder()
                .id(1L)
                .content("Test message")
                .group(group)
                .sender(otherUser)
                .createdAt(LocalDateTime.now())
                .build();

        when(chatMessageRepository.findById(1L)).thenReturn(Optional.of(otherMessage));

        // Act & Assert
        assertThrows(IllegalStateException.class, () -> {
            chatMessageService.deleteMessage(1L);
        });
        verify(chatMessageRepository, never()).delete(any(ChatMessage.class));
    }
}
