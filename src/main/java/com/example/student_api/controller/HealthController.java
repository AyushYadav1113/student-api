package com.example.student_api.controller;

import com.example.student_api.util.ApiConstants;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class HealthController {

    @GetMapping(ApiConstants.HEALTH_CHECK)
    public ResponseEntity<Map<String, String>> healthCheck() {
        return ResponseEntity.ok(Map.of(
                "status", "UP",
                "service", ApiConstants.SERVICE_NAME
        ));
    }
}
