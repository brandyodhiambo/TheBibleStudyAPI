package com.brandyodhiambo.bibleApi.feature.usermgt.service.confirmationToken;

import com.brandyodhiambo.bibleApi.exception.AppException;
import com.brandyodhiambo.bibleApi.feature.usermgt.models.ConfirmationToken;
import com.brandyodhiambo.bibleApi.feature.usermgt.repository.ConfirmationTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class ConfirmationTokenServiceImpl implements ConfirmationTokenService{
    private final JavaMailSender javaMailSender;
    private final ConfirmationTokenRepository confirmationTokenRepository;

    @Autowired
    public ConfirmationTokenServiceImpl(JavaMailSender javaMailSender, ConfirmationTokenRepository confirmationTokenRepository) {
        this.javaMailSender = javaMailSender;
        this.confirmationTokenRepository = confirmationTokenRepository;
    }

    @Override
    public void sendEmailConfirmation(String recipientEmail, String token) {
        String subject = "Email Confirmation";
        String confirmationUrl = "http://localhost:8005/api/auth/confirm?token=" + token;
        String message = "Please confirm your email by clicking the following link: " + confirmationUrl;

        SimpleMailMessage email = new SimpleMailMessage();
        email.setTo(recipientEmail);
        email.setSubject(subject);
        email.setText(message);
        email.setFrom("brandyodhiambo643@gmail.com");

        javaMailSender.send(email);
    }

    @Override
    public void saveConfirmationToken(ConfirmationToken confirmationToken) {
        confirmationTokenRepository.save(confirmationToken);
    }

    @Override
    public ConfirmationToken findByToken(String token) {
        return confirmationTokenRepository.findByToken(token)
                .orElseThrow(() -> new AppException("Invalid or expired token"));
    }
}
