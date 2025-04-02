package com.brandyodhiambo.bibleApi.feature.studymgt.service;

import com.brandyodhiambo.bibleApi.feature.studymgt.models.Comment;
import com.brandyodhiambo.bibleApi.feature.studymgt.models.StudyMaterial;
import com.brandyodhiambo.bibleApi.feature.studymgt.models.dto.CommentResponse;
import com.brandyodhiambo.bibleApi.feature.studymgt.repository.CommentRepository;
import com.brandyodhiambo.bibleApi.feature.studymgt.repository.StudyMaterialRepository;
import com.brandyodhiambo.bibleApi.feature.usermgt.models.Users;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class CommentServiceImplTest {

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private StudyMaterialRepository studyMaterialRepository;

    @Mock
    private Authentication authentication;

    @Mock
    private SecurityContext securityContext;

    @InjectMocks
    private CommentServiceImpl commentService;

    private Users user;
    private StudyMaterial studyMaterial;
    private Comment comment;

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

        studyMaterial = new StudyMaterial();
        studyMaterial.setId(1L);
        studyMaterial.setTitle("Test Study Material");

        comment = Comment.builder()
                .id(1L)
                .content("Test comment")
                .studyMaterial(studyMaterial)
                .user(user)
                .createdAt(LocalDateTime.now())
                .build();

        // Setup security context
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(authentication.getPrincipal()).thenReturn(user);
    }

    @Test
    void addComment_Success() {
        // Arrange
        when(studyMaterialRepository.findById(1L)).thenReturn(Optional.of(studyMaterial));
        when(commentRepository.save(any(Comment.class))).thenReturn(comment);

        // Act
        CommentResponse response = commentService.addComment(1L, "Test comment");

        // Assert
        assertNotNull(response);
        assertEquals("Test comment", response.getContent());
        assertEquals(1L, response.getStudyMaterialId());
        assertEquals("Test Study Material", response.getStudyMaterialTitle());
        assertEquals(1L, response.getUserId());
        assertEquals("testuser", response.getUsername());
        verify(commentRepository, times(1)).save(any(Comment.class));
    }

    @Test
    void addComment_StudyMaterialNotFound() {
        // Arrange
        when(studyMaterialRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> {
            commentService.addComment(1L, "Test comment");
        });
        verify(commentRepository, never()).save(any(Comment.class));
    }

    @Test
    void getStudyMaterialComments_Success() {
        // Arrange
        when(studyMaterialRepository.existsById(1L)).thenReturn(true);
        when(commentRepository.findByStudyMaterialIdOrderByCreatedAtDesc(1L))
                .thenReturn(Arrays.asList(comment));

        // Act
        List<CommentResponse> responses = commentService.getStudyMaterialComments(1L);

        // Assert
        assertNotNull(responses);
        assertEquals(1, responses.size());
        assertEquals("Test comment", responses.get(0).getContent());
        verify(commentRepository, times(1)).findByStudyMaterialIdOrderByCreatedAtDesc(1L);
    }

    @Test
    void getStudyMaterialComments_StudyMaterialNotFound() {
        // Arrange
        when(studyMaterialRepository.existsById(1L)).thenReturn(false);

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> {
            commentService.getStudyMaterialComments(1L);
        });
        verify(commentRepository, never()).findByStudyMaterialIdOrderByCreatedAtDesc(anyLong());
    }

    @Test
    void getComment_Success() {
        // Arrange
        when(commentRepository.findById(1L)).thenReturn(Optional.of(comment));

        // Act
        CommentResponse response = commentService.getComment(1L);

        // Assert
        assertNotNull(response);
        assertEquals("Test comment", response.getContent());
        assertEquals(1L, response.getStudyMaterialId());
        verify(commentRepository, times(1)).findById(1L);
    }

    @Test
    void getComment_NotFound() {
        // Arrange
        when(commentRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> {
            commentService.getComment(1L);
        });
    }

    @Test
    void updateComment_Success() {
        // Arrange
        when(commentRepository.findById(1L)).thenReturn(Optional.of(comment));
        when(commentRepository.save(any(Comment.class))).thenReturn(comment);

        // Act
        CommentResponse response = commentService.updateComment(1L, "Updated comment");

        // Assert
        assertNotNull(response);
        assertEquals("Updated comment", response.getContent());
        verify(commentRepository, times(1)).save(any(Comment.class));
    }

    @Test
    void updateComment_NotFound() {
        // Arrange
        when(commentRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> {
            commentService.updateComment(1L, "Updated comment");
        });
        verify(commentRepository, never()).save(any(Comment.class));
    }

    @Test
    void updateComment_NotAuthorized() {
        // Arrange
        Users otherUser = new Users();
        setUserId(otherUser, 2L);
        otherUser.setUsername("otheruser");

        Comment otherComment = Comment.builder()
                .id(1L)
                .content("Test comment")
                .studyMaterial(studyMaterial)
                .user(otherUser)
                .createdAt(LocalDateTime.now())
                .build();

        when(commentRepository.findById(1L)).thenReturn(Optional.of(otherComment));

        // Act & Assert
        assertThrows(IllegalStateException.class, () -> {
            commentService.updateComment(1L, "Updated comment");
        });
        verify(commentRepository, never()).save(any(Comment.class));
    }

    @Test
    void deleteComment_Success() {
        // Arrange
        when(commentRepository.findById(1L)).thenReturn(Optional.of(comment));

        // Act
        commentService.deleteComment(1L);

        // Assert
        verify(commentRepository, times(1)).delete(comment);
    }

    @Test
    void deleteComment_NotFound() {
        // Arrange
        when(commentRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> {
            commentService.deleteComment(1L);
        });
        verify(commentRepository, never()).delete(any(Comment.class));
    }

    @Test
    void deleteComment_NotAuthorized() {
        // Arrange
        Users otherUser = new Users();
        setUserId(otherUser, 2L);
        otherUser.setUsername("otheruser");

        Comment otherComment = Comment.builder()
                .id(1L)
                .content("Test comment")
                .studyMaterial(studyMaterial)
                .user(otherUser)
                .createdAt(LocalDateTime.now())
                .build();

        when(commentRepository.findById(1L)).thenReturn(Optional.of(otherComment));

        // Act & Assert
        assertThrows(IllegalStateException.class, () -> {
            commentService.deleteComment(1L);
        });
        verify(commentRepository, never()).delete(any(Comment.class));
    }

    @Test
    void getUserComments_Success() {
        // Arrange
        when(commentRepository.findByUserOrderByCreatedAtDesc(user))
                .thenReturn(Arrays.asList(comment));

        // Act
        List<CommentResponse> responses = commentService.getUserComments();

        // Assert
        assertNotNull(responses);
        assertEquals(1, responses.size());
        assertEquals("Test comment", responses.get(0).getContent());
        verify(commentRepository, times(1)).findByUserOrderByCreatedAtDesc(user);
    }
}
