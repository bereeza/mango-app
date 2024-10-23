package com.mango.mangoprofileservice.controller;

import com.mango.mangoprofileservice.dto.RequestNickname;
import com.mango.mangoprofileservice.dto.Response;
import com.mango.mangoprofileservice.service.UserService;
import com.mango.mangoprofileservice.dto.UserInfoDto;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/u")
@RequiredArgsConstructor
public class ProfileController {

    private final UserService userService;

    @GetMapping
    public Mono<UserInfoDto> getCurrentUser(ServerWebExchange exchange) {
        return userService.getCurrentUser(exchange);
    }

    @DeleteMapping
    public Mono<Response> deleteCurrentUser(ServerWebExchange exchange) {
        return userService.deleteCurrentUser(exchange);
    }
}
