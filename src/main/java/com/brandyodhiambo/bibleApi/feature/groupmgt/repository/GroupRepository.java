package com.brandyodhiambo.bibleApi.feature.groupmgt.repository;

import com.brandyodhiambo.bibleApi.feature.groupmgt.models.Group;
import com.brandyodhiambo.bibleApi.feature.usermgt.models.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GroupRepository extends JpaRepository<Group, Long> {
    
    Optional<Group> findByName(String name);
    
    List<Group> findByLeader(Users leader);
    
    @Query("SELECT g FROM Group g JOIN g.members m WHERE m = ?1")
    List<Group> findByMember(Users member);
    
    boolean existsByName(String name);
}