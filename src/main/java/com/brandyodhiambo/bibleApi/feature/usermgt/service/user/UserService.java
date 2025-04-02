package com.brandyodhiambo.bibleApi.feature.usermgt.service.user;

import com.brandyodhiambo.bibleApi.feature.usermgt.models.dto.ChangePasswordRequestDto;
import com.brandyodhiambo.bibleApi.feature.usermgt.models.dto.ForgotPasswordRequestDto;
import com.brandyodhiambo.bibleApi.feature.usermgt.models.dto.LoginRequestDto;
import com.brandyodhiambo.bibleApi.feature.usermgt.models.dto.LoginResponseDto;
import com.brandyodhiambo.bibleApi.feature.usermgt.models.dto.ResetPasswordRequestDto;
import com.brandyodhiambo.bibleApi.feature.usermgt.models.dto.SignUpRequestDto;
import com.brandyodhiambo.bibleApi.feature.usermgt.models.Users;
import com.brandyodhiambo.bibleApi.util.ApiResponse;
import org.springframework.stereotype.Service;

@Service
public interface UserService {

    Boolean checkUsernameAvailability(String username);

    Boolean checkEmailAvailability(String email);

    Users getUser(String username);

    Users signUp(SignUpRequestDto signUpRequestDto);
    LoginResponseDto signIn(LoginRequestDto loginRequestDto);

    Users updateUser(SignUpRequestDto newUser, String username, Users currentUser);

    void deleteUser(String username, Users currentUser);

    ApiResponse giveAdmin(String username);

    ApiResponse removeAdmin(String username);

    ApiResponse giveGroupLeader(String username);

    ApiResponse removeGroupLeader(String username);
    void save(Users user);

    void forgotPassword(ForgotPasswordRequestDto requestDto);

    ApiResponse resetPassword(ResetPasswordRequestDto requestDto);

    ApiResponse changePassword(String username, ChangePasswordRequestDto requestDto);
}
