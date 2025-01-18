package com.brandyodhiambo.bibleApi.feature.usermgt.service;

import com.brandyodhiambo.bibleApi.feature.usermgt.models.User;
import com.brandyodhiambo.bibleApi.feature.usermgt.models.dto.SignUpRequestDto;
import com.brandyodhiambo.bibleApi.util.ApiResponse;
import com.sun.security.auth.UserPrincipal;

public interface UserService {

    Boolean checkUsernameAvailability(String username);

    Boolean checkEmailAvailability(String email);

    User getUser(String username);

    User signUp(SignUpRequestDto signUpRequestDto);

    User updateUser(User newUser, String username, UserPrincipal currentUser);

    ApiResponse deleteUser(String username, UserPrincipal currentUser);

    ApiResponse giveAdmin(String username);

    ApiResponse removeAdmin(String username);

    ApiResponse giveGroupLeader(String username);

    ApiResponse removeGroupLeader(String username);

}
