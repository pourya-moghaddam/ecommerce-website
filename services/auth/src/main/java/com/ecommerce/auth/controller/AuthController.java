package com.ecommerce.auth.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthController {

    @GetMapping("/hello")
    public String hello() {
        return "Auth service is running!";
    }

    @GetMapping("/error-endpoint")
    public String errorEndpoint() {
        throw new RuntimeException("Test exception for global error handling");
    }

}
