package com.ecommerce.common.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;

@ExtendWith(MockitoExtension.class)
class JwtAuthenticationFilterTest {

    @Mock
    private JwtUtil jwtUtil;
    
    @Mock
    private FilterChain filterChain;
    
    private JwtAuthenticationFilter jwtAuthenticationFilter;
    private MockHttpServletRequest request;
    private MockHttpServletResponse response;
    
    @BeforeEach
    void setUp() {
        jwtAuthenticationFilter = new JwtAuthenticationFilter(jwtUtil);
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
        SecurityContextHolder.clearContext();
    }
    
    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void doFilterInternal_WithValidToken_ShouldSetAuthentication() throws ServletException, IOException {
        String token = "valid-jwt-token";
        String username = "testuser";
        request.addHeader("Authorization", "Bearer " + token);
        
        when(jwtUtil.isTokenValid(token)).thenReturn(true);
        when(jwtUtil.isTokenExpired(token)).thenReturn(false);
        when(jwtUtil.extractUsername(token)).thenReturn(username);
        
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);
        
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        assertNotNull(authentication);
        assertEquals(username, authentication.getName());
        assertEquals(1, authentication.getAuthorities().size());
        assertEquals("ROLE_USER", authentication.getAuthorities().iterator().next().getAuthority());
        
        verify(filterChain, times(1)).doFilter(request, response);
    }

    @Test
    void doFilterInternal_WithNoAuthorizationHeader_ShouldNotSetAuthentication() throws ServletException, IOException {
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);
        
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        assertNull(authentication);
        
        verify(jwtUtil, never()).isTokenValid(anyString());
        verify(filterChain, times(1)).doFilter(request, response);
    }

    @Test
    void doFilterInternal_WithNonBearerToken_ShouldNotSetAuthentication() throws ServletException, IOException {
        request.addHeader("Authorization", "Basic sometoken");
        
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);
        
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        assertNull(authentication);
        
        verify(jwtUtil, never()).isTokenValid(anyString());
        verify(filterChain, times(1)).doFilter(request, response);
    }

    @Test
    void doFilterInternal_WithInvalidToken_ShouldNotSetAuthentication() throws ServletException, IOException {
        String token = "invalid-jwt-token";
        request.addHeader("Authorization", "Bearer " + token);
        
        when(jwtUtil.isTokenValid(token)).thenReturn(false);
        
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);
        
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        assertNull(authentication);
        
        verify(jwtUtil, times(1)).isTokenValid(token);
        verify(jwtUtil, never()).extractUsername(anyString());
        verify(filterChain, times(1)).doFilter(request, response);
    }

    @Test
    void doFilterInternal_WithExpiredToken_ShouldNotSetAuthentication() throws ServletException, IOException {
        String token = "expired-jwt-token";
        request.addHeader("Authorization", "Bearer " + token);
        
        when(jwtUtil.isTokenValid(token)).thenReturn(true);
        when(jwtUtil.isTokenExpired(token)).thenReturn(true);
        
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);
        
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        assertNull(authentication);
        
        verify(jwtUtil, times(1)).isTokenValid(token);
        verify(jwtUtil, times(1)).isTokenExpired(token);
        verify(jwtUtil, never()).extractUsername(anyString());
        verify(filterChain, times(1)).doFilter(request, response);
    }

    @Test
    void doFilterInternal_WithValidTokenButNoUsername_ShouldNotSetAuthentication() throws ServletException, IOException {
        String token = "valid-jwt-token-no-username";
        request.addHeader("Authorization", "Bearer " + token);
        
        when(jwtUtil.isTokenValid(token)).thenReturn(true);
        when(jwtUtil.isTokenExpired(token)).thenReturn(false);
        when(jwtUtil.extractUsername(token)).thenReturn(null);
        
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);
        
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        assertNull(authentication);
        
        verify(jwtUtil, times(1)).isTokenValid(token);
        verify(jwtUtil, times(1)).isTokenExpired(token);
        verify(jwtUtil, times(1)).extractUsername(token);
        verify(filterChain, times(1)).doFilter(request, response);
    }

    @Test
    void doFilterInternal_WithExistingAuthentication_ShouldNotOverrideAuthentication() throws ServletException, IOException {
        String token = "valid-jwt-token";
        String username = "testuser";
        request.addHeader("Authorization", "Bearer " + token);
        
        Authentication existingAuth = new org.springframework.security.authentication.UsernamePasswordAuthenticationToken(
                "existinguser", null, java.util.Collections.emptyList());
        SecurityContextHolder.getContext().setAuthentication(existingAuth);
        
        when(jwtUtil.isTokenValid(token)).thenReturn(true);
        when(jwtUtil.isTokenExpired(token)).thenReturn(false);
        when(jwtUtil.extractUsername(token)).thenReturn(username);
        
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);
        
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        assertNotNull(authentication);
        assertEquals("existinguser", authentication.getName());
        
        verify(jwtUtil, times(1)).isTokenValid(token);
        verify(jwtUtil, times(1)).isTokenExpired(token);
        verify(jwtUtil, times(1)).extractUsername(token);
        verify(filterChain, times(1)).doFilter(request, response);
    }

    @Test
    void doFilterInternal_WithJwtUtilException_ShouldClearContextAndContinue() throws ServletException, IOException {
        String token = "problematic-jwt-token";
        request.addHeader("Authorization", "Bearer " + token);
        
        when(jwtUtil.isTokenValid(token)).thenThrow(new RuntimeException("JWT processing error"));
        
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);
        
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        assertNull(authentication);
        
        verify(filterChain, times(1)).doFilter(request, response);
    }

    @Test
    void doFilterInternal_WithEmptyBearerToken_ShouldNotSetAuthentication() throws ServletException, IOException {
        request.addHeader("Authorization", "Bearer ");
        
        when(jwtUtil.isTokenValid("")).thenReturn(false);
        
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);
        
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        assertNull(authentication);
        
        verify(jwtUtil, times(1)).isTokenValid("");
        verify(filterChain, times(1)).doFilter(request, response);
    }

    @Test
    void doFilterInternal_WithOnlyBearerKeyword_ShouldNotSetAuthentication() throws ServletException, IOException {
        request.addHeader("Authorization", "Bearer");
        
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);
        
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        assertNull(authentication);
        
        verify(jwtUtil, never()).isTokenValid(anyString());
        verify(filterChain, times(1)).doFilter(request, response);
    }

    @Test
    void doFilterInternal_WithWhitespaceToken_ShouldHandleGracefully() throws ServletException, IOException {
        String token = "   ";
        request.addHeader("Authorization", "Bearer " + token);
        
        when(jwtUtil.isTokenValid(token)).thenReturn(false);
        
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);
        
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        assertNull(authentication);
        
        verify(jwtUtil, times(1)).isTokenValid(token);
        verify(filterChain, times(1)).doFilter(request, response);
    }

    @Test
    void extractTokenFromRequest_WithValidBearerToken_ShouldReturnToken() throws Exception {
        String expectedToken = "valid-jwt-token";
        request.addHeader("Authorization", "Bearer " + expectedToken);
        
        when(jwtUtil.isTokenValid(expectedToken)).thenReturn(true);
        when(jwtUtil.isTokenExpired(expectedToken)).thenReturn(false);
        when(jwtUtil.extractUsername(expectedToken)).thenReturn("testuser");
        
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);
        
        verify(jwtUtil, times(1)).isTokenValid(expectedToken);
    }

    @Test
    void doFilterInternal_CaseInsensitiveBearerCheck_ShouldWork() throws ServletException, IOException {
        request.addHeader("Authorization", "bearer valid-token");
        
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);
        
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        assertNull(authentication);
        
        verify(jwtUtil, never()).isTokenValid(anyString());
        verify(filterChain, times(1)).doFilter(request, response);
    }
}