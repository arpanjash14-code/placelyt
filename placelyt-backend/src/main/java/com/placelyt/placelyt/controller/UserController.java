package com.placelyt.placelyt.controller;

import com.placelyt.placelyt.dto.LoginRequest;
import com.placelyt.placelyt.dto.UserRequest;
import com.placelyt.placelyt.dto.UserResponse;
import com.placelyt.placelyt.service.UserService;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
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

    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUserById(
            @PathVariable Long id) {

        return userService.getUserById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}