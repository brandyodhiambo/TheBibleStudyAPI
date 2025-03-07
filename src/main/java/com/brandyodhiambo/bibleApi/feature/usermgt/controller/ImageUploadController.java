package com.brandyodhiambo.bibleApi.feature.usermgt.controller;

import com.brandyodhiambo.bibleApi.feature.usermgt.models.UserImage;
import com.brandyodhiambo.bibleApi.feature.usermgt.models.Users;
import com.brandyodhiambo.bibleApi.feature.usermgt.repository.UserImageRepository;
import com.brandyodhiambo.bibleApi.feature.usermgt.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.*;
import java.util.Optional;

@RestController
@RequestMapping("/api/profile")
public class ImageUploadController {

    @Value("${file.upload-dir}")
    private String uploadDir;

    @Value("${server.base-url}")
    private String baseUrl;

    @Autowired
    private UserImageRepository userImageRepository;

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/upload/{username}")
    public ResponseEntity<String> uploadImage(@PathVariable String username, @RequestParam("file") MultipartFile file) {
        try {
            Optional<Users> userOpt = userRepository.findUserByUsername(username);
            if (userOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
            }
            Users user = userOpt.get();
            String fileName = saveImage(file);
            String imageUrl = baseUrl + "/api/uploads/images/" + fileName;
            user.setProfilePicture(imageUrl);
            userRepository.save(user);

            return ResponseEntity.ok("Image uploaded successfully for user: " + user.getUsername());
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error uploading image");
        }
    }

    @GetMapping("/getProfile/{username}")
    public ResponseEntity<?> getImageUrlByUsername(@PathVariable String username) {
        Optional<UserImage> userImageOpt = userImageRepository.findByUsername(username);
        if (userImageOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User image not found");
        }

        String imageUrl = baseUrl + "/api/profile/image/" + userImageOpt.get().getFileName();
        return ResponseEntity.ok().body(imageUrl);
    }

    @GetMapping("/images/{filename}")
    public ResponseEntity<Resource> getImage(@PathVariable String filename) {
        try {
            Path filePath = Paths.get(uploadDir).resolve(filename);
            Resource resource = new UrlResource(filePath.toUri());

            if (resource.exists()) {
                return ResponseEntity.ok()
                        .contentType(MediaType.IMAGE_JPEG)
                        .body(resource);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (MalformedURLException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    //Todo:Change to firebase storage currently saves in file path
    private String saveImage(MultipartFile file) throws IOException {
        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        String fileName = file.getOriginalFilename();
        Path filePath = uploadPath.resolve(fileName);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
        return fileName;
    }
}
