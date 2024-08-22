package com.mango.mangogatewayservice.controller;

import com.mango.mangogatewayservice.dto.user.UserInfoDto;
import com.mango.mangogatewayservice.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/u")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping("/{id}")
    public ResponseEntity<UserInfoDto> getCurrentUser(@PathVariable long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @DeleteMapping("/{id}")
    public void deleteCurrentUser(@PathVariable long id) {
        userService.deleteUser(id);
    }

    @PatchMapping
    public ResponseEntity<String> updateCurrentUser() {
        return ResponseEntity.ok("User updated");
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout() {
        return ResponseEntity.ok("User logged out");
    }
}
