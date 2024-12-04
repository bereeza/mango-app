package com.mango.mangoprofileservice.controller;

import com.mango.mangoprofileservice.dto.UpdateRequest;
import com.mango.mangoprofileservice.dto.Response;
import com.mango.mangoprofileservice.exception.NotPDFFileException;
import com.mango.mangoprofileservice.service.UserService;
import com.mango.mangoprofileservice.dto.UserInfoDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/u")
@RequiredArgsConstructor
@Slf4j
public class ProfileController {

    private final UserService userService;
    private static final String DOC_FORMAT = ".pdf";

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

    @PostMapping(value = "/update/cv")
    public Mono<Response> updateUserCV(
            ServerWebExchange exchange,
            @RequestPart("file") FilePart file) {

        if (file.filename().endsWith(DOC_FORMAT)) {
            throw new NotPDFFileException("Unsupported file type");
        }

        return userService.updateUserCV(exchange, file);
    }

    @GetMapping("/cv/{id}")
    public Mono<ResponseEntity<Resource>> getUserCV(@PathVariable long id) {
        return userService.findCVById(id);
    }
}
