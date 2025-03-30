package com.brandyodhiambo.bibleApi.feature.studymgt.repository;

import com.brandyodhiambo.bibleApi.feature.studymgt.models.Comment;
import com.brandyodhiambo.bibleApi.feature.studymgt.models.StudyMaterial;
import com.brandyodhiambo.bibleApi.feature.usermgt.models.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByStudyMaterialOrderByCreatedAtDesc(StudyMaterial studyMaterial);
    List<Comment> findByStudyMaterialIdOrderByCreatedAtDesc(Long studyMaterialId);
    List<Comment> findByUserOrderByCreatedAtDesc(Users user);
}