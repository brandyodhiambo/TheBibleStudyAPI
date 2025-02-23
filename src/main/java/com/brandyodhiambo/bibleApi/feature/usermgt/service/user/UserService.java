package com.brandyodhiambo.bibleApi.feature.usermgt.service.user;

import com.brandyodhiambo.bibleApi.feature.usermgt.models.UserPrincipal;
import com.brandyodhiambo.bibleApi.feature.usermgt.models.dto.LoginRequestDto;
import com.brandyodhiambo.bibleApi.feature.usermgt.models.dto.LoginResponseDto;
import com.brandyodhiambo.bibleApi.feature.usermgt.models.dto.SignUpRequestDto;
import com.brandyodhiambo.bibleApi.feature.usermgt.models.Users;
import com.brandyodhiambo.bibleApi.util.ApiResponse;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public interface UserService {

    Boolean checkUsernameAvailability(String username);

    Boolean checkEmailAvailability(String email);

    Users getUser(String email);

    Users signUp(SignUpRequestDto signUpRequestDto);
    LoginResponseDto signIn(LoginRequestDto loginRequestDto);

    Users updateUser(Users newUser, String username, UserPrincipal currentUser);

    ApiResponse deleteUser(String username, UserPrincipal currentUser);

    ApiResponse giveAdmin(String username);

    ApiResponse removeAdmin(String username);

    ApiResponse giveGroupLeader(String username);

    ApiResponse removeGroupLeader(String username);
    void save(Users user);

}
