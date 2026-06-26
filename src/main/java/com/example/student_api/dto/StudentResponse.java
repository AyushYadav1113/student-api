package com.example.student_api.dto;

import java.time.LocalDateTime;

public record StudentResponse(
        Long id,
        String firstName,
        String lastName,
        String email,
        Integer age,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}