package com.mango.postservice.controller;

import com.mango.postservice.dto.PagePayload;
import com.mango.postservice.dto.comment.CommentSaveDto;
import com.mango.postservice.dto.post.PostInfoDto;
import com.mango.postservice.dto.post.PostSaveDto;
import com.mango.postservice.dto.Response;
import com.mango.postservice.dto.post.PostTextUpdate;
import com.mango.postservice.entity.Comment;
import com.mango.postservice.entity.Post;
import com.mango.postservice.service.PostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
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

    @PatchMapping("/{id}")
    public Mono<Response> updatePost(ServerWebExchange exchange,
                                     @PathVariable long id,
                                     @RequestBody PostTextUpdate text) {
        return postService.updatePost(exchange, id, text.getText());
    }

    @PostMapping
    public Flux<Post> findPosts(@RequestBody PagePayload request) {
        int page = request.getPage();
        int size = request.getSize();
        Pageable pageable = PageRequest.of(page, size);
        return postService.findAll(pageable);
    }

    @PostMapping("/{id}/comment")
    public Mono<Response> saveComment(ServerWebExchange exchange,
                                      @PathVariable long id,
                                      @RequestBody CommentSaveDto comment) {
        return postService.saveComment(exchange, id, comment);
    }

    @DeleteMapping("/{id}/comment/{commentId}")
    public Mono<Response> deleteComment(ServerWebExchange exchange,
                                        @PathVariable long id,
                                        @PathVariable long commentId) {
        return postService.deleteComment(exchange, id, commentId);
    }

    @PostMapping("/{id}/comment/all")
    public Flux<Comment> findComments(@PathVariable long id,
                                      @RequestBody PagePayload request) {
        int page = request.getPage();
        int size = request.getSize();
        Pageable pageable = PageRequest.of(page, size);
        return postService.findAll(id, pageable);
    }
}
