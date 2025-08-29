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
        assertEquals("Internal Server Error", errorResponse.getError());
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), errorResponse.getStatus());
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
        assertEquals("Bad Request", errorResponse.getError());
        assertEquals(HttpStatus.BAD_REQUEST.value(), errorResponse.getStatus());
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
}