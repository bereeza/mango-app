package com.mango.mangogatewayservice.controller;

import com.mango.mangogatewayservice.dto.auth.Response;
import com.mango.mangogatewayservice.dto.user.UserSaveDto;
import com.mango.mangogatewayservice.dto.auth.AuthRequest;
import com.mango.mangogatewayservice.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/gateway")
public class AuthController {
    private final UserService userService;

    @PostMapping("/signin")
    public Mono<Response<String>> signIn(@RequestBody AuthRequest authRequest) {
        return userService.signIn(authRequest);
    }

    @PostMapping("/signup")
    public Mono<Response<String>> signUp(@RequestBody UserSaveDto authRequest) {
        return userService.signUp(authRequest);
    }

    @DeleteMapping("/signout")
    public Mono<Response<String>> signOut(ServerWebExchange exchange) {
        return userService.signOut(exchange);
    }
}
