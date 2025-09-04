package com.ecommerce.common.dto;

import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class ErrorResponseTest {

    @Test
    void testNoArgsConstructor() {
        ErrorResponse errorResponse = new ErrorResponse();
        
        assertNull(errorResponse.getMessage());
        assertNull(errorResponse.getError());
        assertEquals(0, errorResponse.getStatus());
        assertNull(errorResponse.getTimestamp());
        assertNull(errorResponse.getPath());
    }

    @Test
    void testParameterizedConstructor() {
        String message = "Test message";
        String error = "Test error";
        int status = 400;
        String path = "/test-path";
        LocalDateTime before = LocalDateTime.now();

        ErrorResponse errorResponse = new ErrorResponse(message, error, status, path);
        LocalDateTime after = LocalDateTime.now();

        assertEquals(message, errorResponse.getMessage());
        assertEquals(error, errorResponse.getError());
        assertEquals(status, errorResponse.getStatus());
        assertEquals(path, errorResponse.getPath());
        assertNotNull(errorResponse.getTimestamp());
        assertTrue(errorResponse.getTimestamp().isAfter(before.minusSeconds(1)));
        assertTrue(errorResponse.getTimestamp().isBefore(after.plusSeconds(1)));
    }

    @Test
    void should_ReturnACCompliantFormat_When_ErrorResponseCreated() {
        String message = "User not found";
        String code = "USER_NOT_FOUND";
        String path = "/api/users/123";
        LocalDateTime before = LocalDateTime.now();

        ErrorResponse errorResponse = new ErrorResponse(message, code, path);

        assertNotNull(errorResponse.getTimestamp());
        assertEquals(path, errorResponse.getPath());
        assertEquals(code, errorResponse.getCode());
        assertEquals(message, errorResponse.getMessage());
        assertTrue(errorResponse.getTimestamp().isAfter(before.minusSeconds(1)));
        assertTrue(errorResponse.getTimestamp().isBefore(LocalDateTime.now().plusSeconds(1)));
    }

    @Test
    void testSettersAndGetters() {
        ErrorResponse errorResponse = new ErrorResponse();
        String message = "Updated message";
        String error = "Updated error";
        int status = 500;
        LocalDateTime timestamp = LocalDateTime.now();
        String path = "/updated-path";
        
        errorResponse.setMessage(message);
        errorResponse.setError(error);
        errorResponse.setStatus(status);
        errorResponse.setTimestamp(timestamp);
        errorResponse.setPath(path);
        
        assertEquals(message, errorResponse.getMessage());
        assertEquals(error, errorResponse.getError());
        assertEquals(status, errorResponse.getStatus());
        assertEquals(timestamp, errorResponse.getTimestamp());
        assertEquals(path, errorResponse.getPath());
    }
}