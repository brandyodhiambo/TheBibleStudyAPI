package com.brandyodhiambo.bibleApi.feature.prayermgt.service;

import com.brandyodhiambo.bibleApi.feature.prayermgt.models.dto.PrayerRequestResponse;

import java.util.List;

public interface PrayerRequestService {
    PrayerRequestResponse createPrayerRequest(Long groupId, String title, String description);
    List<PrayerRequestResponse> getGroupPrayerRequests(Long groupId);
    List<PrayerRequestResponse> getGroupPrayerRequestsByAnswered(Long groupId, boolean answered);
    PrayerRequestResponse getPrayerRequest(Long prayerRequestId);
    PrayerRequestResponse updatePrayerRequest(Long prayerRequestId, String title, String description);
    PrayerRequestResponse markAsAnswered(Long prayerRequestId, String testimony);
    void deletePrayerRequest(Long prayerRequestId);
    List<PrayerRequestResponse> getUserPrayerRequests();
    List<PrayerRequestResponse> getUserPrayerRequestsByAnswered(boolean answered);
}