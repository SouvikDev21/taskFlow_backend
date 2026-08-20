package com.example.demo.controller;

import com.example.demo.Service.TodoService;
import com.example.demo.dto.*;
import com.example.demo.utils.TodoStatus;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/api/todo")
@CrossOrigin("*")
public class TodoController {
    private final TodoService todoService;
    @PostMapping("/add")
    public ResponseEntity<TodoResponse> addTodo(@RequestBody CreateTodoRequest req){
        return  ResponseEntity.ok( todoService.createTodo(req));
    }
    @GetMapping("/allTodo")
    public ResponseEntity<List<TodoResponse>> getAllTodos(@RequestParam Long id){
        return ResponseEntity.ok(todoService.getAllTodos(id));
    }
    @PatchMapping("/setStatus")
    public ResponseEntity<TodoResponse> setStatus(@RequestBody updateTodo req){
        return ResponseEntity.ok(todoService.updateTodo(req));
    }
    @PostMapping("/delete")
    public ResponseEntity<String> deleteTodo(@RequestParam Long id){
        todoService.softDeleteTodo(id);
        return ResponseEntity.ok("successfully deleted");
    }
}
