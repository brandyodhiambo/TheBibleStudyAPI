package com.brandyodhiambo.bibleApi.feature.usermgt.service.emailVerification;

import com.brandyodhiambo.bibleApi.feature.usermgt.models.Users;
import com.brandyodhiambo.bibleApi.feature.usermgt.repository.UserRepository;
import com.brandyodhiambo.bibleApi.feature.usermgt.service.otp.OtpService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import static org.springframework.http.HttpStatus.*;

@Service
@RequiredArgsConstructor
public class EmailVerificationService {

    private final OtpService otpService;

    private final UserRepository userRepository;

    private final JavaMailSender mailSender;

    public EmailVerificationService(OtpService otpService, UserRepository userRepository, JavaMailSender mailSender) {
        this.otpService = otpService;
        this.userRepository = userRepository;
        this.mailSender = mailSender;
    }

    @Async
    public void sendVerificationToken(Long userId, String email) {
        final var token = otpService.generateAndStoreOtp(userId);

        // Localhost URL with userId and OTP token
        final var emailVerificationUrl =
                "http://localhost:8005/api/auth/email/verify?uid=%s&t=%s"
                        .formatted(userId, token);
        final var emailText =
                "Click the link to verify your email: " + emailVerificationUrl;

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("Email Verification");
        message.setFrom("System");
        message.setText(emailText);

        mailSender.send(message);
    }

    public void resendVerificationToken(String email) {
        Users user = userRepository.findUserByEmail(email)
                .filter(u -> !u.isEmailVerified())
                .orElseThrow(() -> new ResponseStatusException(
                        NOT_FOUND, "Email not found or already verified"));

        sendVerificationToken(user.getId(), user.getEmail());
    }

    @Transactional
    public Users verifyEmail(Long userId, String token) {
        if (!otpService.isOtpValid(userId, token)) {
            throw new ResponseStatusException(BAD_REQUEST,
                    "Token invalid or expired");
        }
        otpService.deleteOtp(userId);

        final var user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new ResponseStatusException(GONE,
                                "User account has been deleted or deactivated"));

        if (user.isEmailVerified()) {
            throw new ResponseStatusException(BAD_REQUEST,
                    "Email is already verified");
        }

        user.setEmailVerified(true);

        return user;
    }

}
