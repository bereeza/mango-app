package com.mango.postservice.controller;

import com.mango.postservice.dto.SearchRequest;
import com.mango.postservice.dto.comment.CommentInfoDto;
import com.mango.postservice.dto.comment.CommentSaveDto;
import com.mango.postservice.dto.post.PostInfoDto;
import com.mango.postservice.dto.post.PostSaveDto;
import com.mango.postservice.dto.Response;
import com.mango.postservice.dto.post.PostShortInfoDto;
import com.mango.postservice.dto.post.PostTextUpdateDto;
import com.mango.postservice.service.PostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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

    @PostMapping
    public Mono<Response<String>> savePost(ServerWebExchange exchange,
                                   @ModelAttribute PostSaveDto post) {
        return postService.savePost(exchange, post.getText(), post.getFile());
    }

    @GetMapping("/{id}")
    public Mono<PostInfoDto> getPostById(@PathVariable long id) {
        return postService.getPostById(id);
    }

    @DeleteMapping("/{id}")
    public Mono<Response<String>> deletePost(ServerWebExchange exchange,
                                     @PathVariable long id) {
        return postService.deletePost(exchange, id);
    }

    @PatchMapping("/{id}")
    public Mono<Response<String>> updatePost(ServerWebExchange exchange,
                                     @PathVariable long id,
                                     @RequestBody PostTextUpdateDto text) {
        return postService.updatePost(exchange, id, text.getText());
    }

    @PostMapping("/all")
    public Flux<PostInfoDto> findAllPosts(@RequestBody SearchRequest request) {
        int page = request.getPage();
        int size = request.getSize();
        Pageable pageable = PageRequest.of(page, size);
        return postService.findAllPosts(pageable);
    }

    @PostMapping("/all/filter")
    public Flux<PostInfoDto> findAllByText(@RequestBody SearchRequest request) {
        int page = request.getPage();
        int size = request.getSize();
        Pageable pageable = PageRequest.of(page, size);
        return postService.findAllByText(pageable, request.getText());
    }

    @PostMapping("/{id}/all")
    public Flux<PostInfoDto> findAllUserPosts(@PathVariable long id,
                                              @RequestBody SearchRequest request) {
        int page = request.getPage();
        int size = request.getSize();
        Pageable pageable = PageRequest.of(page, size);
        return postService.findAllUserPosts(pageable, id);
    }

    @GetMapping("/top")
    public Flux<PostShortInfoDto> findTopPostsByComments() {
        return postService.findTopPostsByComments();
    }

    @PostMapping("/{id}/comment")
    public Mono<Response<String>> saveComment(ServerWebExchange exchange,
                                      @PathVariable long id,
                                      @RequestBody CommentSaveDto comment) {
        return postService.saveComment(exchange, id, comment);
    }

    @DeleteMapping("/{id}/comment/{commentId}")
    public Mono<Response<String>> deleteComment(ServerWebExchange exchange,
                                        @PathVariable long id,
                                        @PathVariable long commentId) {
        return postService.deleteComment(exchange, id, commentId);
    }

    @PostMapping("/{id}/comment/all")
    public Flux<CommentInfoDto> findComments(@PathVariable long id,
                                             @RequestBody SearchRequest request) {
        int page = request.getPage();
        int size = request.getSize();
        Pageable pageable = PageRequest.of(page, size);
        return postService.findAll(id, pageable);
    }
}
