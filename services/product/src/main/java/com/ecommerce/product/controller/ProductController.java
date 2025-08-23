package com.ecommerce.product.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ProductController {

    @GetMapping("/hello")
    public String hello() {
        return "Product service is running!";
    }

    @GetMapping("/error-test")
    public void testError() {
        throw new IllegalArgumentException("Test error using common ErrorResponse");
    }
}
