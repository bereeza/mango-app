package com.mango.mangoauthservice.controller;

import com.mango.mangoauthservice.dto.UserInfoDto;
import com.mango.mangoauthservice.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping
    public ResponseEntity<UserInfoDto> getCurrentUser() {
        return ResponseEntity.ok(userService.getCurrentUser());
    }
}
