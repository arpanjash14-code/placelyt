package com.placelyt.placelyt.controller;

import com.placelyt.placelyt.dto.LoginRequest;
import com.placelyt.placelyt.dto.UserRequest;
import com.placelyt.placelyt.dto.UserResponse;

import com.placelyt.placelyt.dto.UpdateUserRequest;
import com.placelyt.placelyt.service.UserService;
import org.springframework.security.core.Authentication;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
public UserResponse createUser(@Valid @RequestBody UserRequest request) {
    return userService.createUser(request);
}
    @PostMapping("/login")
    public UserResponse login(@RequestBody LoginRequest request) { 
    return userService.login(request);
    }

    

    @GetMapping
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @GetMapping("/me")
public ResponseEntity<UserResponse> getCurrentUser(
        Authentication authentication) {

    return ResponseEntity.ok(
            userService.getCurrentUser(authentication.getName())
    );
}

@PutMapping("/me")
public ResponseEntity<UserResponse> updateCurrentUser(
        Authentication authentication,
        @Valid @RequestBody UpdateUserRequest request) {

    return ResponseEntity.ok(
            userService.updateCurrentUser(
                    authentication.getName(),
                    request
            )
    );
}

    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUserById(
            @PathVariable Long id) {

        return userService.getUserById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}