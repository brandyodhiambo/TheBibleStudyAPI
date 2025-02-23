package com.brandyodhiambo.bibleApi.feature.usermgt.service.otp;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.Duration;
import java.util.Objects;
import java.util.UUID;

/*
* Class that handles otp generation, validation and storing
* */

@Service
@RequiredArgsConstructor
public class OtpService {

    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    private final RedisTemplate<String, String> redisTemplate;

    public OtpService(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public String generateAndStoreOtp(final Long id) {
        final var otp = generateOtp("ABCDEFG123456789", 10);
        final var cacheKey = getCacheKey(id);

        redisTemplate.opsForValue().set(
                cacheKey, otp, Duration.ofMinutes(5));

        return otp;
    }

    public boolean isOtpValid(final Long id, final String otp) {
        final var cacheKey = getCacheKey(id);
        return Objects.equals(
                redisTemplate.opsForValue().get(cacheKey), otp);
    }

    public void deleteOtp(final Long id) {
        final var cacheKey = getCacheKey(id);
        redisTemplate.delete(cacheKey);
    }

    private String getCacheKey(Long id) {
        return "otp:%s".formatted(id);
    }

    private String generateOtp(String characters, Integer length) {
        StringBuilder otp = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            int index = SECURE_RANDOM.nextInt(characters.length());
            otp.append(characters.charAt(index));
        }
        return otp.toString();
    }
}
