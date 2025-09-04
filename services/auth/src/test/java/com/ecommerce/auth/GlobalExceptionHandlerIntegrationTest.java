package com.ecommerce.auth;

import com.ecommerce.common.exception.GlobalExceptionHandler;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class GlobalExceptionHandlerIntegrationTest {

    @Autowired
    private ApplicationContext applicationContext;

    @Test
    void should_LoadGlobalExceptionHandler_When_SpringContextStarts() {
        assertTrue(applicationContext.containsBean("globalExceptionHandler"));
        
        GlobalExceptionHandler handler = applicationContext.getBean(GlobalExceptionHandler.class);
        assertNotNull(handler);
    }
}