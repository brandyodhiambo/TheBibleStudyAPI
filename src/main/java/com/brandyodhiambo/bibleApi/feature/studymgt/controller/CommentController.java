package com.brandyodhiambo.bibleApi.feature.studymgt.controller;

import com.brandyodhiambo.bibleApi.feature.studymgt.models.dto.CommentResponse;
import com.brandyodhiambo.bibleApi.feature.studymgt.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @PostMapping("/study-materials/{studyMaterialId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<CommentResponse> addComment(
            @PathVariable Long studyMaterialId,
            @RequestBody Map<String, String> payload) {
        String content = payload.get("content");
        if (content == null || content.trim().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        CommentResponse comment = commentService.addComment(studyMaterialId, content);
        return ResponseEntity.status(HttpStatus.CREATED).body(comment);
    }

    @GetMapping("/study-materials/{studyMaterialId}")
    public ResponseEntity<List<CommentResponse>> getStudyMaterialComments(@PathVariable Long studyMaterialId) {
        List<CommentResponse> comments = commentService.getStudyMaterialComments(studyMaterialId);
        return ResponseEntity.ok(comments);
    }

    @GetMapping("/{commentId}")
    public ResponseEntity<CommentResponse> getComment(@PathVariable Long commentId) {
        CommentResponse comment = commentService.getComment(commentId);
        return ResponseEntity.ok(comment);
    }

    @PutMapping("/{commentId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<CommentResponse> updateComment(
            @PathVariable Long commentId,
            @RequestBody Map<String, String> payload) {
        String content = payload.get("content");
        if (content == null || content.trim().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        CommentResponse comment = commentService.updateComment(commentId, content);
        return ResponseEntity.ok(comment);
    }

    @DeleteMapping("/{commentId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> deleteComment(@PathVariable Long commentId) {
        commentService.deleteComment(commentId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/user")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<CommentResponse>> getUserComments() {
        List<CommentResponse> comments = commentService.getUserComments();
        return ResponseEntity.ok(comments);
    }
}