package com.ecommerce.order.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class OrderController {

    @GetMapping("/hello")
    public String hello() {
        return "Order service is running!";
    }

}
