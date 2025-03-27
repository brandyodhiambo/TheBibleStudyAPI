package com.brandyodhiambo.bibleApi.feature.prayermgt.repository;

import com.brandyodhiambo.bibleApi.feature.groupmgt.models.Group;
import com.brandyodhiambo.bibleApi.feature.prayermgt.models.PrayerRequest;
import com.brandyodhiambo.bibleApi.feature.usermgt.models.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PrayerRequestRepository extends JpaRepository<PrayerRequest, Long> {
    List<PrayerRequest> findByGroupOrderByCreatedAtDesc(Group group);
    List<PrayerRequest> findByGroupIdOrderByCreatedAtDesc(Long groupId);
    List<PrayerRequest> findByUserOrderByCreatedAtDesc(Users user);
    List<PrayerRequest> findByGroupAndAnsweredOrderByCreatedAtDesc(Group group, boolean answered);
    List<PrayerRequest> findByGroupIdAndAnsweredOrderByCreatedAtDesc(Long groupId, boolean answered);
    List<PrayerRequest> findByUserAndAnsweredOrderByCreatedAtDesc(Users user, boolean answered);
}