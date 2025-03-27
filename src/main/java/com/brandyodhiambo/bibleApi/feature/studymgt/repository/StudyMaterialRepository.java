package com.brandyodhiambo.bibleApi.feature.studymgt.repository;

import com.brandyodhiambo.bibleApi.feature.groupmgt.models.Group;
import com.brandyodhiambo.bibleApi.feature.studymgt.models.StudyMaterial;
import com.brandyodhiambo.bibleApi.feature.usermgt.models.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StudyMaterialRepository extends JpaRepository<StudyMaterial, Long> {
    
    List<StudyMaterial> findByGroup(Group group);
    
    List<StudyMaterial> findByUploadedBy(Users user);
    
    @Query("SELECT sm FROM StudyMaterial sm WHERE sm.group = :group AND sm.uploadedBy = :user")
    List<StudyMaterial> findByGroupAndUploadedBy(@Param("group") Group group, @Param("user") Users user);
    
    @Query("SELECT sm FROM StudyMaterial sm WHERE " +
           "LOWER(sm.title) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(sm.description) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(sm.keywords) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    List<StudyMaterial> searchByTerm(@Param("searchTerm") String searchTerm);
    
    @Query("SELECT sm FROM StudyMaterial sm WHERE sm.group = :group AND (" +
           "LOWER(sm.title) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(sm.description) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(sm.keywords) LIKE LOWER(CONCAT('%', :searchTerm, '%')))")
    List<StudyMaterial> searchByTermInGroup(@Param("searchTerm") String searchTerm, @Param("group") Group group);
}