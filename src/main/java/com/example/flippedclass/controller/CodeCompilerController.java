package com.example.flippedclass.controller;

import com.example.flippedclass.service.LocalCompilerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/compiler")
public class CodeCompilerController {

    @Autowired
    private LocalCompilerService localCompilerService;

    @PostMapping("/execute")
    public ResponseEntity<?> executeCode(@RequestBody Map<String, String> payload) {
        String script = payload.get("script");
        String stdin = payload.get("stdin");

        if (script == null || script.trim().isEmpty()) {
            Map<String, String> error = new HashMap<>();
            error.put("output", "Code is empty. Please write some code before running.");
            return ResponseEntity.badRequest().body(error);
        }

        try {
            String result = localCompilerService.executeJavaCode(script, stdin);
            Map<String, String> response = new HashMap<>();
            response.put("output", result);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("output", "Error executing code: " + e.getMessage());
            return ResponseEntity.status(500).body(errorResponse);
        }
    }
}
