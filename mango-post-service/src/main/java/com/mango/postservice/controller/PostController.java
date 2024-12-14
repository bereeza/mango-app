package com.mango.postservice.controller;

import com.mango.postservice.dto.PostInfoDto;
import com.mango.postservice.dto.PostSaveDto;
import com.mango.postservice.dto.Response;
import com.mango.postservice.service.PostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Slf4j
@RestController
@RequestMapping("/api/v1/post")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    @PostMapping(consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public Mono<Response> savePost(ServerWebExchange exchange,
                                   @ModelAttribute PostSaveDto post) {
        return postService.savePost(exchange, post.getText(), post.getFile());
    }

    @GetMapping("/{id}")
    public Mono<PostInfoDto> getPostById(@PathVariable long id) {
        return postService.getPostById(id);
    }

    @DeleteMapping("/{id}")
    public Mono<Response> deletePost(ServerWebExchange exchange,
                                     @PathVariable long id) {
        return postService.deletePost(exchange, id);
    }
}
