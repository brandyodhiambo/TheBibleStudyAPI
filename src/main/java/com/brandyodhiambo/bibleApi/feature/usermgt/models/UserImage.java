package com.brandyodhiambo.bibleApi.feature.usermgt.models;

import jakarta.persistence.*;

@Entity
@Table(name = "user_images")
public class UserImage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String fileName;

    public UserImage() {}

    public UserImage(String username, String fileName) {
        this.username = username;
        this.fileName = fileName;
    }

    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getFileName() {
        return fileName;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
}

