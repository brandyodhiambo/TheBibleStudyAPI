package com.brandyodhiambo.bibleApi.feature.usermgt.models;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class ConfirmationToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String token;

    @OneToOne
    @JoinColumn(nullable = false, name = "user_id")
    private UserDetailsImpl user;

    private LocalDateTime createdAt;
    private LocalDateTime expiresAt;

    public ConfirmationToken(String token, UserDetailsImpl user) {
        this.token = token;
        this.user = user;
        this.createdAt = LocalDateTime.now();
        this.expiresAt = this.createdAt.plusHours(24); // Token valid for 24 hours
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public UserDetailsImpl getUser() {
        return user;
    }

    public void setUser(UserDetailsImpl user) {
        this.user = user;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(LocalDateTime expiresAt) {
        this.expiresAt = expiresAt;
    }
}

