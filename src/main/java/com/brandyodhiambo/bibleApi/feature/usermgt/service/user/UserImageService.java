package com.brandyodhiambo.bibleApi.feature.usermgt.service.user;

import com.brandyodhiambo.bibleApi.feature.usermgt.models.UserImage;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public interface UserImageService {

    void saveUserImage(String username, MultipartFile file);
    UserImage getUserImage(String username);
    void deleteUserImage(String username);
    byte[] getImage(String name);
}
