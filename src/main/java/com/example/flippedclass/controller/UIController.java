package com.example.flippedclass.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class UIController {

    @GetMapping("/login")
    public String loginPage() {
        return "Authen/signin"; // Trả về template signin.html
    }

    @GetMapping("/register")
    public String registerPage() {
        return "Authen/signup"; // Trả về template signup.html
    }

}
