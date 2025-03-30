package com.brandyodhiambo.bibleApi.feature.chatmgt.repository;

import com.brandyodhiambo.bibleApi.feature.chatmgt.models.ChatMessage;
import com.brandyodhiambo.bibleApi.feature.groupmgt.models.Group;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
    List<ChatMessage> findByGroupOrderByCreatedAtDesc(Group group);
    List<ChatMessage> findByGroupIdOrderByCreatedAtDesc(Long groupId);
}