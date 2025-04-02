package com.brandyodhiambo.bibleApi.feature.usermgt.controller;

import com.brandyodhiambo.bibleApi.feature.usermgt.models.Users;
import com.brandyodhiambo.bibleApi.feature.usermgt.models.dto.ChangePasswordRequestDto;
import com.brandyodhiambo.bibleApi.feature.usermgt.models.dto.ForgotPasswordRequestDto;
import com.brandyodhiambo.bibleApi.feature.usermgt.models.dto.LoginRequestDto;
import com.brandyodhiambo.bibleApi.feature.usermgt.models.dto.LoginResponseDto;
import com.brandyodhiambo.bibleApi.feature.usermgt.models.dto.ResetPasswordRequestDto;
import com.brandyodhiambo.bibleApi.feature.usermgt.models.dto.SignUpRequestDto;
import com.brandyodhiambo.bibleApi.util.ApiResponse;
import com.brandyodhiambo.bibleApi.feature.usermgt.service.emailVerification.EmailVerificationService;
import com.brandyodhiambo.bibleApi.feature.usermgt.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {
    @Autowired
    private UserService userService;

    @Autowired
    private EmailVerificationService emailVerificationService;

    @Autowired
    JavaMailSender mailSender;

    @PostMapping("/signup")
    public ResponseEntity<Users> signUp(@Valid @RequestBody SignUpRequestDto signUpRequestDto) {
        Users user = userService.signUp(signUpRequestDto);
        if(!user.isEmailVerified()){
            emailVerificationService.sendVerificationToken(
                    user.getId(),
                    user.getEmail()
            );
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(user);
    }

    @PostMapping("/signin")
    public ResponseEntity<LoginResponseDto> signIn(@Valid @RequestBody LoginRequestDto loginRequestDto) {
        LoginResponseDto response = userService.signIn(loginRequestDto);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/email/resend-verification")
    public ResponseEntity<Void> resendVerificationLink(
            @RequestParam String username) {

        emailVerificationService.resendVerificationToken(username);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/email/verify")
    public ResponseEntity<Users> verifyEmail(
            @RequestParam("uid") Long userId,
            @RequestParam("t") String token) {

        final var verifiedUser =
                emailVerificationService.verifyEmail(userId, token);

        return ResponseEntity.ok(verifiedUser);
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<ApiResponse> forgotPassword(@Valid @RequestBody ForgotPasswordRequestDto requestDto) {
        try {
            userService.forgotPassword(requestDto);
            return ResponseEntity.ok(new ApiResponse(Boolean.TRUE, "Password reset email sent successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse(Boolean.FALSE, e.getMessage()));
        }
    }

    @PostMapping("/reset-password")
    public ResponseEntity<ApiResponse> resetPassword(@Valid @RequestBody ResetPasswordRequestDto requestDto) {
        ApiResponse response = userService.resetPassword(requestDto);
        if (response.getSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    @PostMapping("/change-password")
    public ResponseEntity<ApiResponse> changePassword(
            @Valid @RequestBody ChangePasswordRequestDto requestDto,
            @AuthenticationPrincipal UserDetails userDetails) {
        ApiResponse response = userService.changePassword(userDetails.getUsername(), requestDto);
        if (response.getSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }
}
