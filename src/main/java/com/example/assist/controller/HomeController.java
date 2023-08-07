package com.example.assist.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Controller
 */
@Controller
public class HomeController {
    
    @GetMapping("/")
    public String homePage() {
        return "home";
    }
}
