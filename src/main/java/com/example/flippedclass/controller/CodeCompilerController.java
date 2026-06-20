package com.example.flippedclass.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.example.flippedclass.service.LocalCompilerService;

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

        try {
            String output = localCompilerService.executeJavaCode(script, stdin);
            return ResponseEntity.ok(Map.of("output", output));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", "Lỗi Server Compiler: " + e.getMessage()));
        }
    }
}
