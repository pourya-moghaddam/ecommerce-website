package com.ecommerce.common.dto;

import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class ErrorResponseTest {

    @Test
    void testNoArgsConstructor() {
        ErrorResponse errorResponse = new ErrorResponse();
        
        assertNull(errorResponse.getType());
        assertNull(errorResponse.getTitle());
        assertEquals(0, errorResponse.getStatus());
        assertNull(errorResponse.getDetail());
        assertNull(errorResponse.getInstance());
        assertNull(errorResponse.getTimestamp());
    }

    @Test
    void testParameterizedConstructor() {
        String type = "about:blank";
        String title = "Bad Request";
        int status = 400;
        String detail = "Test error message";
        String instance = "/test-path";
        LocalDateTime before = LocalDateTime.now();

        ErrorResponse errorResponse = new ErrorResponse(type, title, status, detail, instance);
        LocalDateTime after = LocalDateTime.now();

        assertEquals(type, errorResponse.getType());
        assertEquals(title, errorResponse.getTitle());
        assertEquals(status, errorResponse.getStatus());
        assertEquals(detail, errorResponse.getDetail());
        assertEquals(instance, errorResponse.getInstance());
        assertNotNull(errorResponse.getTimestamp());
        assertTrue(errorResponse.getTimestamp().isAfter(before.minusSeconds(1)));
        assertTrue(errorResponse.getTimestamp().isBefore(after.plusSeconds(1)));
    }

    @Test
    void should_ReturnRFC9457CompliantFormat_When_ErrorResponseCreated() {
        String type = "https://api.example.com/errors/user-not-found";
        String title = "User Not Found";
        int status = 404;
        String detail = "The requested user could not be found";
        String instance = "/api/users/123";
        LocalDateTime before = LocalDateTime.now();

        ErrorResponse errorResponse = new ErrorResponse(type, title, status, detail, instance);

        assertNotNull(errorResponse.getTimestamp());
        assertEquals(type, errorResponse.getType());
        assertEquals(title, errorResponse.getTitle());
        assertEquals(status, errorResponse.getStatus());
        assertEquals(detail, errorResponse.getDetail());
        assertEquals(instance, errorResponse.getInstance());
        assertTrue(errorResponse.getTimestamp().isAfter(before.minusSeconds(1)));
        assertTrue(errorResponse.getTimestamp().isBefore(LocalDateTime.now().plusSeconds(1)));
    }

    @Test
    void testSettersAndGetters() {
        ErrorResponse errorResponse = new ErrorResponse();
        String type = "about:blank";
        String title = "Internal Server Error";
        int status = 500;
        String detail = "Test error detail";
        String instance = "/test";
        LocalDateTime timestamp = LocalDateTime.now();
        
        errorResponse.setType(type);
        errorResponse.setTitle(title);
        errorResponse.setStatus(status);
        errorResponse.setDetail(detail);
        errorResponse.setInstance(instance);
        errorResponse.setTimestamp(timestamp);
        
        assertEquals(type, errorResponse.getType());
        assertEquals(title, errorResponse.getTitle());
        assertEquals(status, errorResponse.getStatus());
        assertEquals(detail, errorResponse.getDetail());
        assertEquals(instance, errorResponse.getInstance());
        assertEquals(timestamp, errorResponse.getTimestamp());
    }
}