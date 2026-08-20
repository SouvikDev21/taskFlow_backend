package com.example.demo.controller;
import com.example.demo.Service.UserService;
import com.example.demo.dto.UserResponse;
import com.example.demo.security.CustomUserDetailsService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("api/user")
@CrossOrigin(origins = "*")
public class UserController {
    private  final UserService userService;
    @GetMapping
    public ResponseEntity<UserResponse> getUser(@AuthenticationPrincipal UserDetails userDetails){

        return ResponseEntity.ok(userService.getUser(userDetails.getUsername()));
    }
}
