package com.brandyodhiambo.bibleApi.feature.usermgt.controller;

import com.brandyodhiambo.bibleApi.feature.usermgt.models.UserImage;
import com.brandyodhiambo.bibleApi.feature.usermgt.models.Users;
import com.brandyodhiambo.bibleApi.feature.usermgt.repository.UserRepository;
import com.brandyodhiambo.bibleApi.feature.usermgt.service.user.UserImageService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

@RestController
@RequestMapping("/api/profile")
public class ImageUploadController {

    @Autowired
    private UserImageService userImageService;

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/upload/{username}")
    public ResponseEntity<String> uploadImage(@PathVariable String username, @RequestParam("image") MultipartFile file) {
            Optional<Users> userOpt = userRepository.findUserByUsername(username);
            if (userOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
            }
            Users user = userOpt.get();
            userImageService.saveUserImage(user.getUsername(), file);
            return ResponseEntity.ok("Image uploaded successfully for user: " + user.getUsername());
    }

    @GetMapping("/getProfile/{username}")
    public ResponseEntity<?> getImageUrlByUsername(@PathVariable String username) {
        Optional<Users> userOpt = userRepository.findUserByUsername(username);
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }
        UserImage userImage = userImageService.getUserImage(userOpt.get().getUsername());
        return ResponseEntity.ok().body(userImage);
    }

    @GetMapping("image/{name}")
    public ResponseEntity<?>  getImageByName(@PathVariable("name") String name){
        byte[] image = userImageService.getImage(name);
        return ResponseEntity.status(HttpStatus.OK)
                .contentType(MediaType.valueOf("image/png"))
                .body(image);
    }
}
