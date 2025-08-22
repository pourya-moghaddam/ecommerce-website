package com.ecommerce.payment.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PaymentController {

    @GetMapping("/hello")
    public String hello() {
        return "Payment service is running!";
    }

}
