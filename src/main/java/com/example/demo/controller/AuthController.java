package com.example.demo.controller;

import com.example.demo.Service.UserService;
import com.example.demo.dto.LoginReq;
import com.example.demo.dto.LoginregisterResponse;
import com.example.demo.dto.RegisterRequest;
import com.example.demo.dto.UserResponse;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("api/auth")
@CrossOrigin(origins = "*")
public class AuthController {
    private  final UserService userService;
    @PostMapping("/register")
    public ResponseEntity<LoginregisterResponse> RegisterUser(@RequestBody RegisterRequest createUserRequest){
        return ResponseEntity.ok(userService.createUser(createUserRequest));
    }
    @PostMapping("/login")
    public ResponseEntity<LoginregisterResponse> LoginUser(@RequestBody LoginReq req){
        return ResponseEntity.ok(userService.loginUser(req));
    }

}
