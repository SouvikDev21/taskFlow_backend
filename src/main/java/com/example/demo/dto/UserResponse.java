package com.example.demo.dto;

import com.example.demo.Entity.User;
import jakarta.validation.constraints.NotBlank;

public record UserResponse(
        Long id,
        String username ,
        String email,
        String department
) {}
