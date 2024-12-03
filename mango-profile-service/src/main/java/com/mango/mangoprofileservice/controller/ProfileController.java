package com.mango.mangoprofileservice.controller;

import com.mango.mangoprofileservice.dto.UpdateRequest;
import com.mango.mangoprofileservice.dto.Response;
import com.mango.mangoprofileservice.service.UserService;
import com.mango.mangoprofileservice.dto.UserInfoDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/u")
@RequiredArgsConstructor
@Slf4j
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

    @PutMapping("/update/link")
    public Mono<Response> updateUserLinks(
            ServerWebExchange exchange,
            @RequestBody UpdateRequest data) {
        return userService.updateUserLinks(exchange, data.getData());
    }

    @PutMapping("/update/about")
    public Mono<Response> updateUserAboutSection(
            ServerWebExchange exchange,
            @RequestBody UpdateRequest data) {
        return userService.updateUserAboutSection(exchange, data.getData());
    }
}
