package com.ecommerce.common.exception;

import com.ecommerce.common.dto.ErrorResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.WebRequest;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler globalExceptionHandler;

    @Mock
    private WebRequest webRequest;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        globalExceptionHandler = new GlobalExceptionHandler();
    }

    @Test
    void testHandleGenericException() {
        String errorMessage = "Generic error occurred";
        String requestUri = "/test-endpoint";
        Exception exception = new Exception(errorMessage);
        
        when(webRequest.getDescription(false)).thenReturn("uri=" + requestUri);
        
        ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleGenericException(exception, webRequest);
        
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        
        ErrorResponse errorResponse = response.getBody();
        assertNotNull(errorResponse);
        assertEquals(errorMessage, errorResponse.getMessage());
        assertEquals("INTERNAL_SERVER_ERROR", errorResponse.getCode());
        assertEquals(requestUri, errorResponse.getPath());
        assertNotNull(errorResponse.getTimestamp());
    }

    @Test
    void testHandleIllegalArgumentException() {
        String errorMessage = "Invalid argument provided";
        String requestUri = "/test-endpoint";
        IllegalArgumentException exception = new IllegalArgumentException(errorMessage);
        
        when(webRequest.getDescription(false)).thenReturn("uri=" + requestUri);
        
        ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleIllegalArgument(exception, webRequest);
        
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        
        ErrorResponse errorResponse = response.getBody();
        assertNotNull(errorResponse);
        assertEquals(errorMessage, errorResponse.getMessage());
        assertEquals("BAD_REQUEST", errorResponse.getCode());
        assertEquals(requestUri, errorResponse.getPath());
        assertNotNull(errorResponse.getTimestamp());
    }

    @Test
    void testHandleExceptionWithNullMessage() {
        Exception exception = new Exception();

        when(webRequest.getDescription(false)).thenReturn("uri=/test");

        ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleGenericException(exception, webRequest);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        ErrorResponse errorResponse = response.getBody();
        if (errorResponse != null) {
            assertNull(errorResponse.getMessage());
        }
    }

    @Test
    void should_ReturnACCompliantResponse_When_GenericExceptionThrown() {
        Exception exception = new Exception("Database connection failed");
        String requestPath = "/api/users/123";

        when(webRequest.getDescription(false)).thenReturn("uri=" + requestPath);

        ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleGenericException(exception, webRequest);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        ErrorResponse errorResponse = response.getBody();
        assertNotNull(errorResponse);
        assertNotNull(errorResponse.getTimestamp());
        assertEquals(requestPath, errorResponse.getPath());
        assertEquals("INTERNAL_SERVER_ERROR", errorResponse.getCode());
        assertEquals("Database connection failed", errorResponse.getMessage());
    }

    @Test
    void should_ReturnACCompliantResponse_When_IllegalArgumentExceptionThrown() {
        IllegalArgumentException exception = new IllegalArgumentException("Invalid user ID");
        String requestPath = "/api/users/abc";

        when(webRequest.getDescription(false)).thenReturn("uri=" + requestPath);

        ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleIllegalArgument(exception, webRequest);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        ErrorResponse errorResponse = response.getBody();
        assertNotNull(errorResponse);
        assertNotNull(errorResponse.getTimestamp());
        assertEquals(requestPath, errorResponse.getPath());
        assertEquals("BAD_REQUEST", errorResponse.getCode());
        assertEquals("Invalid user ID", errorResponse.getMessage());
    }
}