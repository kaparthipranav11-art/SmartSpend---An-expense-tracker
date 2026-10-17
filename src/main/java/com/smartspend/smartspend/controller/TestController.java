package com.smartspend.smartspend.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @GetMapping("/api/status")
    public String getStatus() {
        return "SmartSpend Backend is running successfully! Database connected.";
    }
}