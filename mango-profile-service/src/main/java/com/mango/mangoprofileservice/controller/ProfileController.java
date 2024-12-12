package com.mango.mangoprofileservice.controller;

import com.mango.mangoprofileservice.dto.user.UpdateRequest;
import com.mango.mangoprofileservice.dto.Response;
import com.mango.mangoprofileservice.dto.user.UserById;
import com.mango.mangoprofileservice.service.UserService;
import com.mango.mangoprofileservice.dto.user.UserInfoDto;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FilePart;
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

    @GetMapping("/{id}")
    public Mono<UserById> getUserById(@PathVariable long id) {
        return userService.getUserById(id);
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

    @PostMapping(value = "/update/cv")
    public Mono<Response> updateUserCV(
            ServerWebExchange exchange,
            @RequestPart("file") FilePart file) {

        return userService.updateUserCV(exchange, file);
    }

    @GetMapping("/cv/{id}")
    public Mono<ResponseEntity<ByteArrayResource>> getUserCV(@PathVariable long id) {
        return userService.findCVById(id);
    }
}
