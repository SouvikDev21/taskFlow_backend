package com.example.demo.dto;

import com.example.demo.utils.TodoStatus;

public record updateTodo(Long id, TodoStatus status) {
}
