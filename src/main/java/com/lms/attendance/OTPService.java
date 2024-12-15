package com.lms.attendance;

import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Service
public class OTPService {

    private Map<String, String> otpStorage = new HashMap<>();  

    public String generateOtp(Long courseId, Long lessonId) {
        String otp = String.valueOf(new Random().nextInt(999999));  
        otpStorage.put(courseId + "_" + lessonId, otp);  
        return otp;
    }

    public String getOtp(Long courseId, Long lessonId) {
        return otpStorage.get(courseId + "_" + lessonId);  
    }
}
