package com.example.demo.dto;

import com.example.demo.utils.TodoStatus;

import java.time.LocalDate;

public record CreateTodoRequest(
        Long userId,
        String title,
        String description,
        TodoStatus status,
        LocalDate dueDate

) {
}
