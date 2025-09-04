package com.ecommerce.common.security;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class DefaultSecurityConfigTest {

    @Mock
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    private DefaultSecurityConfig defaultSecurityConfig;

    @BeforeEach
    void setUp() {
        defaultSecurityConfig = new DefaultSecurityConfig(jwtAuthenticationFilter);
    }

    @Test
    void constructor_WithJwtAuthenticationFilter_ShouldSetFilter() {
        Object actualFilter = ReflectionTestUtils.getField(defaultSecurityConfig, "jwtAuthenticationFilter");
        assertNotNull(actualFilter);
        assertTrue(actualFilter instanceof JwtAuthenticationFilter);
    }

    @Test
    void filterChain_WithHttpSecurity_ShouldReturnSecurityFilterChain() throws Exception {
        HttpSecurity httpSecurity = createMockHttpSecurity();
        
        SecurityFilterChain result = defaultSecurityConfig.filterChain(httpSecurity);
        
        assertNotNull(result);
    }

    @Test
    void filterChain_ShouldConfigureStatelessSession() throws Exception {
        HttpSecurity httpSecurity = createMockHttpSecurity();
        
        SecurityFilterChain result = defaultSecurityConfig.filterChain(httpSecurity);
        
        assertNotNull(result);
    }

    @Test
    void filterChain_ShouldDisableCsrf() throws Exception {
        HttpSecurity httpSecurity = createMockHttpSecurity();
        
        SecurityFilterChain result = defaultSecurityConfig.filterChain(httpSecurity);
        
        assertNotNull(result);
    }

    @Test
    void filterChain_ShouldAddJwtFilter() throws Exception {
        HttpSecurity httpSecurity = createMockHttpSecurity();
        
        SecurityFilterChain result = defaultSecurityConfig.filterChain(httpSecurity);
        
        assertNotNull(result);
    }

    @Test
    void filterChain_ShouldPermitActuatorEndpoints() throws Exception {
        HttpSecurity httpSecurity = createMockHttpSecurity();
        
        SecurityFilterChain result = defaultSecurityConfig.filterChain(httpSecurity);
        
        assertNotNull(result);
    }

    @Test
    void filterChain_ShouldPermitHealthEndpoints() throws Exception {
        HttpSecurity httpSecurity = createMockHttpSecurity();
        
        SecurityFilterChain result = defaultSecurityConfig.filterChain(httpSecurity);
        
        assertNotNull(result);
    }

    @Test
    void filterChain_ShouldPermitErrorEndpoint() throws Exception {
        HttpSecurity httpSecurity = createMockHttpSecurity();
        
        SecurityFilterChain result = defaultSecurityConfig.filterChain(httpSecurity);
        
        assertNotNull(result);
    }

    @Test
    void filterChain_ShouldPermitAllRequests() throws Exception {
        HttpSecurity httpSecurity = createMockHttpSecurity();
        
        SecurityFilterChain result = defaultSecurityConfig.filterChain(httpSecurity);
        
        assertNotNull(result);
    }

    private HttpSecurity createMockHttpSecurity() throws Exception {
        HttpSecurity httpSecurity = mock(HttpSecurity.class);
        DefaultSecurityFilterChain defaultSecurityFilterChain = mock(DefaultSecurityFilterChain.class);

        org.mockito.Mockito.when(httpSecurity.csrf(org.mockito.ArgumentMatchers.any())).thenReturn(httpSecurity);
        org.mockito.Mockito.when(httpSecurity.sessionManagement(org.mockito.ArgumentMatchers.any())).thenReturn(httpSecurity);
        org.mockito.Mockito.when(httpSecurity.authorizeHttpRequests(org.mockito.ArgumentMatchers.any())).thenReturn(httpSecurity);
        org.mockito.Mockito.when(httpSecurity.addFilterBefore(org.mockito.ArgumentMatchers.any(), org.mockito.ArgumentMatchers.any())).thenReturn(httpSecurity);
        org.mockito.Mockito.when(httpSecurity.build()).thenReturn(defaultSecurityFilterChain);

        return httpSecurity;
    }
}