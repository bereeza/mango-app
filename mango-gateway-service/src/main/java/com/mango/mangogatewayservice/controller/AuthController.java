package com.mango.mangogatewayservice.controller;

import com.mango.mangogatewayservice.dto.auth.AuthRequest;
import com.mango.mangogatewayservice.dto.auth.AuthResponse;
import com.mango.mangogatewayservice.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/gateway")
public class AuthController {
    private final UserService userService;

    @PostMapping("/signin")
    public Mono<AuthResponse> signin(@RequestBody AuthRequest authRequest) {
        return userService.findByEmail(authRequest);
    }

    @PostMapping("/signup")
    public Mono<AuthResponse> signup(@RequestBody AuthRequest authRequest) {
        return userService.saveUser(authRequest);
    }
}
