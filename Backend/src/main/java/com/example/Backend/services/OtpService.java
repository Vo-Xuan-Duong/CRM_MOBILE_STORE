package com.example.Backend.services;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class OtpService {

    private String OTP = "OTP : ";

    private final RedisService redisService;

    public OtpService(RedisService redisService) {
        this.redisService = redisService;
    }

    public String generateOtp(String email) {
        // Generate a 6-digit OTP
        int otp = (int)(Math.random() * 900000) + 100000;

        String key = OTP + email;

        redisService.set(key, otp, 10, java.util.concurrent.TimeUnit.MINUTES);

        return String.valueOf(otp);
    }

    public boolean validateOtp(String email, String inputOtp) {
        String key = OTP + email;
        Object cachedOtp = redisService.get(key);

        if (cachedOtp != null && cachedOtp.toString().equals(inputOtp)) {
            redisService.delete(key); // Invalidate OTP after successful validation
            return true;
        }
        return false;
    }
}
