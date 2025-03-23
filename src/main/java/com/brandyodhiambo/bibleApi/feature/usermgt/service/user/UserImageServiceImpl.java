package com.brandyodhiambo.bibleApi.feature.usermgt.service.user;

import com.brandyodhiambo.bibleApi.feature.usermgt.models.UserImage;
import com.brandyodhiambo.bibleApi.feature.usermgt.models.Users;
import com.brandyodhiambo.bibleApi.feature.usermgt.repository.UserImageRepository;
import com.brandyodhiambo.bibleApi.util.ImageUtil;
import io.jsonwebtoken.io.IOException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

@Component
public class UserImageServiceImpl implements UserImageService {

    @Autowired
    private UserImageRepository userImageRepository;

    @Autowired
    private UserService userService;

    @Override
    @Transactional
    public void saveUserImage(String username, MultipartFile file) throws IOException {
        try {
            Users user = userService.getUser(username);
            Optional<UserImage> existingImage = userImageRepository.findByUser_Username(username);
            if(existingImage.isPresent()){
                UserImage userImage = existingImage.get();
                userImage.setImageData(ImageUtil.compressImage(file.getBytes()));
                userImage.setUser(user);
                userImageRepository.save(userImage);
            } else{
                userImageRepository.save(UserImage.builder()
                        .user(user)
                        .imageData(ImageUtil.compressImage(file.getBytes()))
                        .build());
            }
        } catch (IOException | java.io.IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    @Transactional
    public UserImage getUserImage(String username) {
        Optional<UserImage> dbImage = userImageRepository.findByUser_Username(username);
        if (dbImage.isPresent()) {
            UserImage userImage = dbImage.get();
            return UserImage.builder()
                    .user(userImage.getUser())
                    .imageData(ImageUtil.decompressImage(userImage.getImageData()))
                    .build();
        } else {
            throw new RuntimeException("User image not found");
        }
    }


    @Override
    public void deleteUserImage(String username) {
        Optional<UserImage> userImage = userImageRepository.findByUser_Username(username);
        userImage.ifPresent(userImageRepository::delete);
    }

    @Transactional
    @Override
    public byte[] getImage(String name) {
        Optional<UserImage> dbImage = userImageRepository.findByUser_Username(name);
        return ImageUtil.decompressImage(dbImage.get().getImageData());
    }
}
