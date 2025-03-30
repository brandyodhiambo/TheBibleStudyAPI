package com.brandyodhiambo.bibleApi.feature.studymgt.service;

import com.brandyodhiambo.bibleApi.feature.studymgt.models.dto.CommentResponse;

import java.util.List;

public interface CommentService {
    CommentResponse addComment(Long studyMaterialId, String content);
    List<CommentResponse> getStudyMaterialComments(Long studyMaterialId);
    CommentResponse getComment(Long commentId);
    CommentResponse updateComment(Long commentId, String content);
    void deleteComment(Long commentId);
    List<CommentResponse> getUserComments();
}