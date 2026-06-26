package com.example.student_api.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UpdateStudentRequest(

        @NotBlank(message = "First name is required")
        String firstName,

        @NotBlank(message = "Last name is required")
        String lastName,

        @Email(message = "Invalid email format")
        @NotBlank(message = "Email is required")
        String email,

        @NotNull(message = "Age is required")
        @Min(value = 1, message = "Age must be at least 1")
        @Max(value = 150, message = "Age must be at most 150")
        Integer age
) {
}
