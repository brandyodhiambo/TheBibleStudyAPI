package com.brandyodhiambo.bibleApi.feature.studymgt.service;

import com.brandyodhiambo.bibleApi.feature.studymgt.models.Comment;
import com.brandyodhiambo.bibleApi.feature.studymgt.models.StudyMaterial;
import com.brandyodhiambo.bibleApi.feature.studymgt.models.dto.CommentResponse;
import com.brandyodhiambo.bibleApi.feature.studymgt.repository.CommentRepository;
import com.brandyodhiambo.bibleApi.feature.studymgt.repository.StudyMaterialRepository;
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
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final StudyMaterialRepository studyMaterialRepository;

    @Override
    public CommentResponse addComment(Long studyMaterialId, String content) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Users currentUser = (Users) authentication.getPrincipal();

        StudyMaterial studyMaterial = studyMaterialRepository.findById(studyMaterialId)
                .orElseThrow(() -> new EntityNotFoundException("Study material not found with id: " + studyMaterialId));

        Comment comment = Comment.builder()
                .content(content)
                .studyMaterial(studyMaterial)
                .user(currentUser)
                .build();

        Comment savedComment = commentRepository.save(comment);
        return mapToResponse(savedComment);
    }

    @Override
    public List<CommentResponse> getStudyMaterialComments(Long studyMaterialId) {
        // Check if study material exists
        if (!studyMaterialRepository.existsById(studyMaterialId)) {
            throw new EntityNotFoundException("Study material not found with id: " + studyMaterialId);
        }

        List<Comment> comments = commentRepository.findByStudyMaterialIdOrderByCreatedAtDesc(studyMaterialId);
        return comments.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public CommentResponse getComment(Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new EntityNotFoundException("Comment not found with id: " + commentId));

        return mapToResponse(comment);
    }

    @Override
    public CommentResponse updateComment(Long commentId, String content) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Users currentUser = (Users) authentication.getPrincipal();

        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new EntityNotFoundException("Comment not found with id: " + commentId));

        // Check if user is the author of the comment
        if (!comment.getUser().equals(currentUser)) {
            throw new IllegalStateException("You can only update your own comments");
        }

        comment.setContent(content);
        Comment updatedComment = commentRepository.save(comment);
        return mapToResponse(updatedComment);
    }

    @Override
    public void deleteComment(Long commentId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Users currentUser = (Users) authentication.getPrincipal();

        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new EntityNotFoundException("Comment not found with id: " + commentId));

        // Check if user is the author of the comment
        if (!comment.getUser().equals(currentUser)) {
            throw new IllegalStateException("You can only delete your own comments");
        }

        commentRepository.delete(comment);
    }

    @Override
    public List<CommentResponse> getUserComments() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Users currentUser = (Users) authentication.getPrincipal();

        List<Comment> comments = commentRepository.findByUserOrderByCreatedAtDesc(currentUser);
        return comments.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private CommentResponse mapToResponse(Comment comment) {
        return CommentResponse.builder()
                .id(comment.getId())
                .content(comment.getContent())
                .studyMaterialId(comment.getStudyMaterial().getId())
                .studyMaterialTitle(comment.getStudyMaterial().getTitle())
                .userId(comment.getUser().getId())
                .username(comment.getUser().getUsername())
                .createdAt(comment.getCreatedAt())
                .build();
    }
}