package com.brandyodhiambo.bibleApi.feature.usermgt.controller;

import com.brandyodhiambo.bibleApi.feature.usermgt.models.Users;
import com.brandyodhiambo.bibleApi.feature.usermgt.models.dto.LoginRequestDto;
import com.brandyodhiambo.bibleApi.feature.usermgt.models.dto.LoginResponseDto;
import com.brandyodhiambo.bibleApi.feature.usermgt.models.dto.SignUpRequestDto;
import com.brandyodhiambo.bibleApi.feature.usermgt.service.emailVerification.EmailVerificationService;
import com.brandyodhiambo.bibleApi.feature.usermgt.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

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
            @RequestParam String email) {

        emailVerificationService.resendVerificationToken(email);
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

    @GetMapping("/test-email")
    public String testEmail() {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo("brandyariana99@gmail.com");
        message.setSubject("Test Email");
        message.setText("This is a test email from Spring Boot.");
        mailSender.send(message);
        return "Email sent!";
    }
}
