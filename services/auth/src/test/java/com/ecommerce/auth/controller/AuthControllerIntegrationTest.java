package com.ecommerce.auth.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class AuthControllerIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void should_ReturnACCompliantErrorResponse_When_ExceptionThrown() throws Exception {
        String url = "http://localhost:" + port + "/error-endpoint";

        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

        assertEquals(500, response.getStatusCode().value());
        String responseBody = response.getBody();
        assertNotNull(responseBody);
        assertTrue(responseBody.contains("\"timestamp\""));
        assertTrue(responseBody.contains("\"path\":\"/error-endpoint\""));
        assertTrue(responseBody.contains("\"code\":\"INTERNAL_SERVER_ERROR\""));
        assertTrue(responseBody.contains("\"message\":\"Test exception for global error handling\""));
    }
}