package com.ecommerce.common.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

public class SecurityUtils {
    
    private static final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    
    public static String encodePassword(String password) {
        return passwordEncoder.encode(password);
    }
    
    public static boolean matchesPassword(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }
    
    public static String generateToken() {
        return java.util.UUID.randomUUID().toString();
    }
}