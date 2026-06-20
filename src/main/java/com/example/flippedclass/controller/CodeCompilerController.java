package com.example.flippedclass.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/compiler")
public class CodeCompilerController {

    private static final String JDOODLE_URL = "https://api.jdoodle.com/v1/execute";
    // JDoodle API Credentials
    private final String clientId = "1b5c47c63c34297590498482135468cd";
    private final String clientSecret = "b910469e4647a0db018914a38d0915fd706499aa01c82b8651a2c5f8689a5a99";

    @PostMapping("/execute")
    public ResponseEntity<?> executeCode(@RequestBody Map<String, String> payload) {
        String script = payload.get("script");

        if (script == null || script.trim().isEmpty()) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Code không được để trống");
            return ResponseEntity.badRequest().body(error);
        }

        // Chuẩn bị Data gửi sang JDoodle
        Map<String, Object> requestData = new HashMap<>();
        requestData.put("clientId", clientId);
        requestData.put("clientSecret", clientSecret);
        requestData.put("script", script);
        requestData.put("language", "java");
        requestData.put("versionIndex", "4"); // JDK 17

        RestTemplate restTemplate = new RestTemplate();
        try {
            // Bắn API sang JDoodle
            ResponseEntity<Map> response = restTemplate.postForEntity(JDOODLE_URL, requestData, Map.class);
            return ResponseEntity.ok(response.getBody());
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Lỗi khi gọi máy chủ JDoodle: " + e.getMessage());
            return ResponseEntity.status(500).body(error);
        }
    }
}
