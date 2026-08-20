package com.example.demo.Service;

import com.example.demo.Entity.User;
import com.example.demo.Repository.UserRepository;
import com.example.demo.dto.LoginReq;
import com.example.demo.dto.LoginregisterResponse;
import com.example.demo.dto.RegisterRequest;
import com.example.demo.dto.UserResponse;
import com.example.demo.security.CustomUserDetailsService;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


@Service
@AllArgsConstructor
public class UserService {
    private  UserRepository userRepository;
    private final PasswordEncoder passwordEncoder ;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    public LoginregisterResponse createUser(RegisterRequest request) {
        //check for existing user if exists then throw error else proceed
        // Check if username already exists
        if (userRepository.findByEmail(request.username()).isPresent()) {
            throw new RuntimeException("Username already exists");
        }

        // Check if email already exists
        if (userRepository.findByEmail(request.email()).isPresent()) {
            throw new RuntimeException("Email already exists");
        }
        //create new user object and set the fields
        User user = new User();
        user.setUsername(request.username());
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setEmail(request.email());
        user.setDepartment(request.department());
        //save the user in db
        User savedUser = userRepository.save(user);
        //generate token for new user
        String accessToken = jwtService.generateToken(request);
        //send the response
        return new LoginregisterResponse(
                savedUser.getId(),
                savedUser.getUsername(),
                savedUser.getEmail(),
                savedUser.getDepartment(),
                accessToken
        );
    }
    public void deleteUser(Long id){
        userRepository.deleteById(id);
    }

    public LoginregisterResponse loginUser(LoginReq req) {
        //chcek if user exists and password is correct
    try {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(req.username(), req.password())
        );
    }
    catch (Exception e) {
        throw new UsernameNotFoundException(e.getMessage());
    }
        // 2. Load user details

        User user = (User)userDetailsService.loadUserByUsername(req.username());

        // 3. Generate JWT token
        RegisterRequest regreq =new RegisterRequest(user.getUsername(),user.getEmail(),user.getPassword(),user.getDepartment());
        String token = jwtService.generateToken(regreq);

        // 4. Return response with token
        return new LoginregisterResponse(user.getId(),user.getUsername(),user.getEmail(),user.getDepartment(),token);
    }

    public UserResponse getUser(String username) {
        //find the user in DB
        User user=userRepository.findByUsername(username).orElseThrow(()->new UsernameNotFoundException("User not found"));
        UserResponse res=new UserResponse(user.getId(),user.getUsername(),user.getEmail(),user.getDepartment());
        return res;
    }
}
