package com.authentication.authentication.Services;

import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Service;

@Service
public class EmailGeneratingService {

    private final Map<String, Integer> verificationCodes = new ConcurrentHashMap<>();

    public int generateCode() {
        Random random = new Random();
        return random.nextInt(900000) + 100000;
    }

    public void saveCodeForEmail(String email, int code) {
        verificationCodes.put(email, code);
    }

    public Integer getCodeForEmail(String email) {
        return verificationCodes.get(email);
    }

    public String formatCode(int code) {
        String codeStr = String.valueOf(code);
        return codeStr.substring(0, 3) + " " + codeStr.substring(3);
    }

    public String generateEmailTextAndSaveCode(String email, int code) {
        saveCodeForEmail(email, code);
        return "Hello,\n\nYour verification code is: " + formatCode(code) +
                "\n\nPlease enter this code to verify your email address.\n\nThank you!";
    }
}
