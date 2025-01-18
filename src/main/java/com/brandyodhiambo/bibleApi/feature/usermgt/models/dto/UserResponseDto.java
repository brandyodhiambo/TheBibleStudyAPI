package com.brandyodhiambo.bibleApi.feature.usermgt.models.dto;

import com.brandyodhiambo.bibleApi.feature.usermgt.models.Role;

import java.util.List;

public class UserResponseDto {

    private String id;

    private String firstName;

    private String lastName;

    private String username;

    private String email;

    private String password;

    private List<Role> role;

    private String profilePicture;
}
