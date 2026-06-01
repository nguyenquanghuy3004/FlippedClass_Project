package com.example.flippedclass.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class UIController {

    @GetMapping("/login")
    public String loginPage() {
        return "signin"; // Trả về template signin.html
    }

    @GetMapping("/register")
    public String registerPage() {
        return "signup"; // Trả về template signup.html
    }

    @GetMapping("/")
    public String indexPage() {
        return "index"; // Trả về trang chủ index.html của template inapp
    }
}
