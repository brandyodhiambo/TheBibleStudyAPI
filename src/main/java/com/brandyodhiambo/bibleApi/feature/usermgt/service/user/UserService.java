package com.brandyodhiambo.bibleApi.feature.usermgt.service.user;

import com.brandyodhiambo.bibleApi.feature.usermgt.models.UserPrincipal;
import com.brandyodhiambo.bibleApi.feature.usermgt.models.dto.LoginRequestDto;
import com.brandyodhiambo.bibleApi.feature.usermgt.models.dto.LoginResponseDto;
import com.brandyodhiambo.bibleApi.feature.usermgt.models.dto.SignUpRequestDto;
import com.brandyodhiambo.bibleApi.feature.usermgt.models.UserDetailsImpl;
import com.brandyodhiambo.bibleApi.util.ApiResponse;
import org.springframework.security.core.userdetails.UserDetails;

public interface UserService {

    Boolean checkUsernameAvailability(String username);

    Boolean checkEmailAvailability(String email);

    UserDetailsImpl getUser(String username);

    UserDetailsImpl signUp(SignUpRequestDto signUpRequestDto);
    LoginResponseDto signIn(LoginRequestDto loginRequestDto);

    UserDetailsImpl updateUser(UserDetailsImpl newUser, String username, UserPrincipal currentUser);

    ApiResponse deleteUser(String username, UserPrincipal currentUser);

    ApiResponse giveAdmin(String username);

    ApiResponse removeAdmin(String username);

    ApiResponse giveGroupLeader(String username);

    ApiResponse removeGroupLeader(String username);

    public UserDetails loadUserByUsername(String email);

}
