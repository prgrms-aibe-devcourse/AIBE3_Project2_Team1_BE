package com.hotsix.server.user.controller;

import com.hotsix.server.user.dto.UserLoginRequestDto;
import com.hotsix.server.user.dto.UserRegisterRequestDto;
import com.hotsix.server.user.dto.UserResponseDto;
import com.hotsix.server.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<UserResponseDto> register(@RequestBody UserRegisterRequestDto request) {
        UserResponseDto response = userService.register(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    public ResponseEntity<UserResponseDto> login(@RequestBody UserLoginRequestDto request) {
        UserResponseDto response = userService.login(request);
        return ResponseEntity.ok(response);
    }
}
