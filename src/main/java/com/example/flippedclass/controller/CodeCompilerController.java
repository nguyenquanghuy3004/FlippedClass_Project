package com.example.flippedclass.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/compiler")
public class CodeCompilerController {

    private static final String JDOODLE_URL = "https://api.jdoodle.com/v1/execute";

    @Value("${jdoodle.client.id}")
    private String clientId;

    @Value("${jdoodle.client.secret}")
    private String clientSecret;

    @PostMapping("/execute")
    public ResponseEntity<?> executeCode(@RequestBody Map<String, String> payload) {
        String script = payload.get("script");

        if (script == null || script.trim().isEmpty()) {
            Map<String, String> error = new HashMap<>();
            error.put("output", "Code is empty. Please write some code before running.");
            return ResponseEntity.badRequest().body(error);
        }

        // Chuẩn bị Data gửi sang JDoodle
        Map<String, Object> requestData = new HashMap<>();
        requestData.put("clientId", clientId);
        requestData.put("clientSecret", clientSecret);
        requestData.put("script", script);
        requestData.put("language", "java"); // Ngôn ngữ java
        requestData.put("versionIndex", "4"); // Phiên bản JDK 17

        try {
            RestTemplate restTemplate = new RestTemplate();
            // Bắn API sang JDoodle
            ResponseEntity<Map> response = restTemplate.postForEntity(JDOODLE_URL, requestData, Map.class);
            
            // Trả kết quả về lại cho Frontend
            return ResponseEntity.ok(response.getBody());
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("output", "Error connecting to compiler: " + e.getMessage());
            return ResponseEntity.status(500).body(errorResponse);
        }
    }
}
