package com.mango.mangogatewayservice.controller;

import com.mango.mangogatewayservice.dto.AuthenticationRequest;
import com.mango.mangogatewayservice.dto.Response;
import com.mango.mangogatewayservice.dto.user.UserSaveDto;
import com.mango.mangogatewayservice.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class AuthenticationController {
    private final UserService userService;

    @GetMapping
    public Response ping() {
        return Response.builder()
                .code(200)
                .message("Please sign in or sign up.")
                .build();
    }

    @PostMapping("/register")
    public ResponseEntity<UserSaveDto> register(@Valid @RequestBody AuthenticationRequest request) {
        return ResponseEntity.ok(userService.save(request));
    }

    @PostMapping("/auth")
    public ResponseEntity<String> authenticate(@Valid @RequestBody AuthenticationRequest request) {
        return ResponseEntity.ok("AuthenticationRequest");
    }

    @PostMapping("/register/g")
    public ResponseEntity<String> registerWithGoogle() {
        return ResponseEntity.ok("Google AuthenticationRequest");
    }
}
