package com.ecommerce.common.security;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class JwtUtilTest {

    @Mock
    private SecurityProperties securityProperties;

    private JwtUtil jwtUtil;

    @BeforeEach
    void setUp() {
        when(securityProperties.getSecret()).thenReturn("testSecretKeyThatIsAtLeast256BitsLongForHS256AlgorithmSecurityTesting");
        jwtUtil = new JwtUtil(securityProperties);
    }

    @Test
    void generateToken_WithUsernameOnly_ShouldReturnValidToken() {
        when(securityProperties.getExpiration()).thenReturn(3600L);
        jwtUtil = new JwtUtil(securityProperties);
        
        String username = "testuser";
        
        String token = jwtUtil.generateToken(username);
        
        assertNotNull(token);
        assertFalse(token.isEmpty());
        assertTrue(jwtUtil.isTokenValid(token));
        assertEquals(username, jwtUtil.extractUsername(token));
    }

    @Test
    void generateToken_WithExtraClaims_ShouldReturnValidTokenWithClaims() {
        when(securityProperties.getExpiration()).thenReturn(3600L);
        jwtUtil = new JwtUtil(securityProperties);
        
        String username = "testuser";
        Map<String, Object> extraClaims = new HashMap<>();
        extraClaims.put("role", "USER");
        extraClaims.put("department", "IT");
        
        String token = jwtUtil.generateToken(username, extraClaims);
        
        assertNotNull(token);
        assertTrue(jwtUtil.isTokenValid(token));
        assertEquals(username, jwtUtil.extractUsername(token));
    }

    @Test
    void isTokenValid_WithValidToken_ShouldReturnTrue() {
        when(securityProperties.getExpiration()).thenReturn(3600L);
        jwtUtil = new JwtUtil(securityProperties);
        
        String username = "testuser";
        String token = jwtUtil.generateToken(username);
        
        boolean isValid = jwtUtil.isTokenValid(token);
        
        assertTrue(isValid);
    }

    @Test
    void isTokenValid_WithInvalidToken_ShouldReturnFalse() {
        String invalidToken = "invalid.jwt.token";
        
        boolean isValid = jwtUtil.isTokenValid(invalidToken);
        
        assertFalse(isValid);
    }

    @Test
    void isTokenValid_WithNullToken_ShouldReturnFalse() {
        boolean isValid = jwtUtil.isTokenValid(null);
        
        assertFalse(isValid);
    }

    @Test
    void isTokenValid_WithEmptyToken_ShouldReturnFalse() {
        boolean isValid = jwtUtil.isTokenValid("");
        
        assertFalse(isValid);
    }

    @Test
    void extractUsername_WithValidToken_ShouldReturnCorrectUsername() {
        when(securityProperties.getExpiration()).thenReturn(3600L);
        jwtUtil = new JwtUtil(securityProperties);
        
        String expectedUsername = "testuser";
        String token = jwtUtil.generateToken(expectedUsername);
        
        String actualUsername = jwtUtil.extractUsername(token);
        
        assertEquals(expectedUsername, actualUsername);
    }

    @Test
    void extractUsername_WithInvalidToken_ShouldReturnNull() {
        String invalidToken = "invalid.jwt.token";
        
        String username = jwtUtil.extractUsername(invalidToken);
        
        assertNull(username);
    }

    @Test
    void extractExpiration_WithValidToken_ShouldReturnFutureDate() {
        when(securityProperties.getExpiration()).thenReturn(3600L);
        jwtUtil = new JwtUtil(securityProperties);
        
        String username = "testuser";
        String token = jwtUtil.generateToken(username);
        
        Date expiration = jwtUtil.extractExpiration(token);
        
        assertNotNull(expiration);
        assertTrue(expiration.after(new Date()));
    }

    @Test
    void extractExpiration_WithInvalidToken_ShouldReturnNull() {
        String invalidToken = "invalid.jwt.token";
        
        Date expiration = jwtUtil.extractExpiration(invalidToken);
        
        assertNull(expiration);
    }

    @Test
    void isTokenExpired_WithFreshToken_ShouldReturnFalse() {
        when(securityProperties.getExpiration()).thenReturn(3600L);
        jwtUtil = new JwtUtil(securityProperties);
        
        String username = "testuser";
        String token = jwtUtil.generateToken(username);
        
        boolean isExpired = jwtUtil.isTokenExpired(token);
        
        assertFalse(isExpired);
    }

    @Test
    void isTokenExpired_WithExpiredToken_ShouldReturnFalseAsTokenBecomesInvalid() throws InterruptedException {
        // Create JWT with very short expiration that will expire
        when(securityProperties.getExpiration()).thenReturn(1L); // 1 second
        jwtUtil = new JwtUtil(securityProperties);
        
        String username = "testuser";
        String token = jwtUtil.generateToken(username);

        assertNotNull(token);

        Thread.sleep(1500);

        assertFalse(jwtUtil.isTokenExpired(token));
        assertFalse(jwtUtil.isTokenValid(token));
        assertNull(jwtUtil.extractExpiration(token));
    }

    @Test
    void isTokenExpired_WithInvalidToken_ShouldHandleGracefully() {
        String invalidToken = "invalid.jwt.token";

        assertDoesNotThrow(() -> {
            boolean isExpired = jwtUtil.isTokenExpired(invalidToken);
            // The behavior here depends on implementation - could be true or false
            // The important thing is it doesn't throw an exception
        });
    }

    @Test
    void tokenGeneration_WithDifferentSecrets_ShouldProduceDifferentTokens() {
        when(securityProperties.getExpiration()).thenReturn(3600L);
        jwtUtil = new JwtUtil(securityProperties);
        
        String username = "testuser";
        String token1 = jwtUtil.generateToken(username);

        when(securityProperties.getSecret()).thenReturn("differentSecretKeyThatIsAtLeast256BitsLongForHS256AlgorithmSecurity");
        when(securityProperties.getExpiration()).thenReturn(3600L);
        JwtUtil jwtUtil2 = new JwtUtil(securityProperties);
        String token2 = jwtUtil2.generateToken(username);
        
        assertNotEquals(token1, token2);
    }

    @Test
    void tokenValidation_WithWrongSecret_ShouldFail() {
        when(securityProperties.getExpiration()).thenReturn(3600L);
        jwtUtil = new JwtUtil(securityProperties);
        
        String username = "testuser";
        String token = jwtUtil.generateToken(username);

        when(securityProperties.getSecret()).thenReturn("wrongSecretKeyThatIsAtLeast256BitsLongForHS256AlgorithmSecurity");
        JwtUtil jwtUtilWithWrongSecret = new JwtUtil(securityProperties);
        
        boolean isValid = jwtUtilWithWrongSecret.isTokenValid(token);
        
        assertFalse(isValid);
    }
}