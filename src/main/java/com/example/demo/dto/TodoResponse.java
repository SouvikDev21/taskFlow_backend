package com.example.demo.dto;

import com.example.demo.utils.TodoStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record TodoResponse(
        Long id, String title, String description, TodoStatus status, LocalDateTime created_at, LocalDateTime updated_at,
        LocalDate dueDate,Boolean isDeleted
) {

}
