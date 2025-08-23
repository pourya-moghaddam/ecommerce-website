package com.ecommerce.common.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SecurityUtilsTest {

    @Test
    void testEncodePassword() {
        String rawPassword = "myPassword123";
        
        String encodedPassword = SecurityUtils.encodePassword(rawPassword);
        
        assertNotNull(encodedPassword);
        assertNotEquals(rawPassword, encodedPassword);
        assertTrue(encodedPassword.startsWith("$2a$"));
        assertTrue(encodedPassword.length() > 50);
    }

    @Test
    void testEncodePasswordProducesDifferentHashes() {
        String rawPassword = "samePassword";
        
        String encoded1 = SecurityUtils.encodePassword(rawPassword);
        String encoded2 = SecurityUtils.encodePassword(rawPassword);
        
        assertNotEquals(encoded1, encoded2);
    }

    @Test
    void testMatchesPasswordWithValidPassword() {
        String rawPassword = "testPassword456";
        String encodedPassword = SecurityUtils.encodePassword(rawPassword);
        
        boolean matches = SecurityUtils.matchesPassword(rawPassword, encodedPassword);
        
        assertTrue(matches);
    }

    @Test
    void testMatchesPasswordWithInvalidPassword() {
        String rawPassword = "correctPassword";
        String wrongPassword = "wrongPassword";
        String encodedPassword = SecurityUtils.encodePassword(rawPassword);
        
        boolean matches = SecurityUtils.matchesPassword(wrongPassword, encodedPassword);
        
        assertFalse(matches);
    }

    @Test
    void testMatchesPasswordWithNullRawPassword() {
        String encodedPassword = SecurityUtils.encodePassword("test");
        
        boolean matches = SecurityUtils.matchesPassword(null, encodedPassword);
        
        assertFalse(matches);
    }

    @Test
    void testMatchesPasswordWithNullEncodedPassword() {
        String rawPassword = "test";
        
        boolean matches = SecurityUtils.matchesPassword(rawPassword, null);
        
        assertFalse(matches);
    }

    @Test
    void testGenerateToken() {
        String token = SecurityUtils.generateToken();
        
        assertNotNull(token);
        assertEquals(36, token.length());
        assertTrue(token.matches("^[a-f0-9]{8}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{12}$"));
    }

    @Test
    void testGenerateTokenProducesUniqueTokens() {
        String token1 = SecurityUtils.generateToken();
        String token2 = SecurityUtils.generateToken();
        
        assertNotEquals(token1, token2);
    }
}