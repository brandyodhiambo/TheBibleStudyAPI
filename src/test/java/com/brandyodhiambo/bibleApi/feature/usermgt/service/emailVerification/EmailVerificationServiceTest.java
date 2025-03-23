package com.brandyodhiambo.bibleApi.feature.usermgt.service.emailVerification;

import com.brandyodhiambo.bibleApi.feature.usermgt.models.Users;
import com.brandyodhiambo.bibleApi.feature.usermgt.repository.UserRepository;
import com.brandyodhiambo.bibleApi.feature.usermgt.service.otp.OtpService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmailVerificationServiceTest {

    @Mock
    private OtpService otpService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private JavaMailSender mailSender;

    @InjectMocks
    private EmailVerificationService emailVerificationService;

    private Users testUser;

    @BeforeEach
    void setUp() {
        testUser = new Users();
        testUser.setUsername("testuser");
        testUser.setEmail("test@example.com");
        testUser.setFirstName("Test");
        testUser.setLastName("User");
        testUser.setEmailVerified(false);
    }

    @Test
    void sendVerificationToken_ShouldGenerateTokenAndSendEmail() {
        // Arrange
        Long userId = 1L;
        String email = "test@example.com";
        when(otpService.generateAndStoreOtp(userId)).thenReturn("test-token");
        doNothing().when(mailSender).send(any(SimpleMailMessage.class));

        // Act
        emailVerificationService.sendVerificationToken(userId, email);

        // Assert
        verify(otpService).generateAndStoreOtp(userId);
        verify(mailSender).send(any(SimpleMailMessage.class));
    }

    @Test
    void resendVerificationToken_WhenUserExistsAndNotVerified_ShouldResendToken() {
        // Arrange
        String username = "testuser";
        when(userRepository.findUserByUsername(username)).thenReturn(Optional.of(testUser));
        doNothing().when(otpService).generateAndStoreOtp(anyLong());
        doNothing().when(mailSender).send(any(SimpleMailMessage.class));

        // Act
        emailVerificationService.resendVerificationToken(username);

        // Assert
        verify(userRepository).findUserByUsername(username);
        verify(otpService).generateAndStoreOtp(anyLong());
        verify(mailSender).send(any(SimpleMailMessage.class));
    }

    @Test
    void resendVerificationToken_WhenUserDoesNotExist_ShouldThrowException() {
        // Arrange
        String username = "nonexistent";
        when(userRepository.findUserByUsername(username)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResponseStatusException.class, () -> {
            emailVerificationService.resendVerificationToken(username);
        });
        verify(userRepository).findUserByUsername(username);
        verifyNoInteractions(otpService);
        verifyNoInteractions(mailSender);
    }

    @Test
    void resendVerificationToken_WhenUserAlreadyVerified_ShouldThrowException() {
        // Arrange
        String username = "verified";
        Users verifiedUser = new Users();
        verifiedUser.setEmailVerified(true);
        when(userRepository.findUserByUsername(username)).thenReturn(Optional.of(verifiedUser));

        // Act & Assert
        assertThrows(ResponseStatusException.class, () -> {
            emailVerificationService.resendVerificationToken(username);
        });
        verify(userRepository).findUserByUsername(username);
        verifyNoInteractions(otpService);
        verifyNoInteractions(mailSender);
    }

    @Test
    void verifyEmail_WhenTokenIsValid_ShouldVerifyEmail() {
        // Arrange
        Long userId = 1L;
        String token = "valid-token";
        when(otpService.isOtpValid(userId, token)).thenReturn(true);
        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));

        // Act
        Users result = emailVerificationService.verifyEmail(userId, token);

        // Assert
        assertTrue(result.isEmailVerified());
        verify(otpService).isOtpValid(userId, token);
        verify(otpService).deleteOtp(userId);
        verify(userRepository).findById(userId);
    }

    @Test
    void verifyEmail_WhenTokenIsInvalid_ShouldThrowException() {
        // Arrange
        Long userId = 1L;
        String token = "invalid-token";
        when(otpService.isOtpValid(userId, token)).thenReturn(false);

        // Act & Assert
        assertThrows(ResponseStatusException.class, () -> {
            emailVerificationService.verifyEmail(userId, token);
        });
        verify(otpService).isOtpValid(userId, token);
        verifyNoMoreInteractions(otpService);
        verifyNoInteractions(userRepository);
    }

    @Test
    void verifyEmail_WhenUserDoesNotExist_ShouldThrowException() {
        // Arrange
        Long userId = 1L;
        String token = "valid-token";
        when(otpService.isOtpValid(userId, token)).thenReturn(true);
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResponseStatusException.class, () -> {
            emailVerificationService.verifyEmail(userId, token);
        });
        verify(otpService).isOtpValid(userId, token);
        verify(otpService).deleteOtp(userId);
        verify(userRepository).findById(userId);
    }

    @Test
    void verifyEmail_WhenUserAlreadyVerified_ShouldThrowException() {
        // Arrange
        Long userId = 1L;
        String token = "valid-token";
        Users verifiedUser = new Users();
        verifiedUser.setEmailVerified(true);
        when(otpService.isOtpValid(userId, token)).thenReturn(true);
        when(userRepository.findById(userId)).thenReturn(Optional.of(verifiedUser));

        // Act & Assert
        assertThrows(ResponseStatusException.class, () -> {
            emailVerificationService.verifyEmail(userId, token);
        });
        verify(otpService).isOtpValid(userId, token);
        verify(otpService).deleteOtp(userId);
        verify(userRepository).findById(userId);
    }
}