package com.brandyodhiambo.bibleApi.feature.usermgt.repository;

import com.brandyodhiambo.bibleApi.feature.usermgt.models.UserImage;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserImageRepository extends JpaRepository<UserImage, Long> {
    Optional<UserImage> findByUsername(String username);
}

