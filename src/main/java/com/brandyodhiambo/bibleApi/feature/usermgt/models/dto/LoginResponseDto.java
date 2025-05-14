package com.brandyodhiambo.bibleApi.feature.usermgt.models.dto;


import java.util.List;

public class LoginResponseDto {
    private String token;
    private String type;
    private Long id;
    private String username;
    private String email;
    private List<String> role;
    private long expiresIn;
    private String groupId;
    private String firstName;
    private String lastName;

    public LoginResponseDto(String accesstoken, String type, Long id, String username, String email, List<String> role, Long expiresIn, String groupId, String firstName, String lastName) {
        this.token = accesstoken;
        this.type = type;
        this.id = id;
        this.username = username;
        this.email = email;
        this.role = role;
        this.expiresIn = expiresIn;
        this.groupId = groupId;
        this.firstName = firstName;
        this.lastName = lastName;
    }


    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public long getExpiresIn() {
        return expiresIn;
    }

    public void setExpiresIn(long expiresIn) {
        this.expiresIn = expiresIn;
    }

    public String getAccessToken() {
        return token;
    }

    public void setAccessToken(String token) {
        this.token = token;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<String> getRole() {
        return role;
    }

    public void setRole(List<String> role) {
        this.role = role;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
}
