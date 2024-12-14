package com.mango.postservice.service;

import com.mango.postservice.dto.post.PostInfoDto;
import com.mango.postservice.dto.Response;
import com.mango.postservice.entity.Post;
import com.mango.postservice.exception.PostNotFoundException;
import com.mango.postservice.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@Slf4j
public class PostService {
    private final PostRepository postRepository;
    private final BucketService bucketService;
    private final UserRedisService userRedisService;

    public Mono<Response> savePost(ServerWebExchange exchange, String text, FilePart file) {
        return userRedisService.buildUser(exchange)
                .flatMap(user -> {
                    String link = bucketService.save(file);
                    Post post = buildPost(user.getId(), text, link);
                    return postRepository.save(post)
                            .then(Mono.just(Response.builder()
                                    .message("Post successfully saved")
                                    .status(HttpStatus.OK)
                                    .build()));
                })
                .onErrorResume(e -> {
                    log.error("Post wasn't saved: {}", e.getMessage());
                    return Mono.error(new IllegalArgumentException(e.getMessage()));
                });
    }

    public Mono<PostInfoDto> getPostById(long id) {
        return postRepository.findById(id)
                .map(this::buildPostById)
                .switchIfEmpty(Mono.error(new PostNotFoundException("Post not found.")));
    }

    public Mono<Response> deletePost(ServerWebExchange exchange, long id) {
        return userRedisService.buildUser(exchange)
                .flatMap(user -> postRepository.findById(id)
                        .flatMap(post -> {
                            if (user.getId() != post.getUserId()) {
                                log.error("You cannot delete other users' posts.");
                                return Mono.error(new IllegalArgumentException("You cannot delete other users' posts."));
                            }

                            bucketService.delete(post.getPhotoLink());

                            return postRepository.deleteById(id)
                                    .then(Mono.just(Response.builder()
                                            .status(HttpStatus.OK)
                                            .message("Post deleted successfully.")
                                            .build()));
                        })
                        .switchIfEmpty(Mono.error(new PostNotFoundException("Post not found.")))
                )
                .onErrorResume(e -> {
                    log.error("Post wasn't deleted: {}", e.getMessage());
                    return Mono.error(new IllegalArgumentException(e.getMessage()));
                });
    }

    public Mono<Response> updatePost(ServerWebExchange exchange,
                                     long id,
                                     String text) {
        return userRedisService.buildUser(exchange)
                .flatMap(user -> postRepository.findById(id)
                        .flatMap(post -> {
                            if (user.getId() != post.getUserId()) {
                                log.error("You cannot update other users' posts.");
                                return Mono.error(new IllegalArgumentException("You cannot update other users' posts."));
                            }

                            return postRepository.updateText(post.getId(), text)
                                    .then(Mono.just(Response.builder()
                                            .message("Post updated successfully.")
                                            .status(HttpStatus.OK)
                                            .build()));

                        }))
                .onErrorResume(e -> {
                    log.error("Post not found: {}", e.getMessage());
                    return Mono.error(new IllegalArgumentException("Post not found"));
                });
    }

    public Flux<Post> findAll(Pageable pageable) {
        return postRepository.findAllBy(pageable)
                .onErrorResume(e -> {
                    log.error("Something went wrong: {}", e.getMessage());
                    return Mono.error(new IllegalArgumentException(e.getMessage()));
                });
    }

    private PostInfoDto buildPostById(Post post) {
        return PostInfoDto.builder()
                .id(post.getId())
                .userId(post.getUserId())
                .text(post.getText())
                .photoLink(post.getPhotoLink())
                .reputation(post.getReputation())
                .build();
    }

    private Post buildPost(long id, String text, String link) {
        return Post.builder()
                .userId(id)
                .photoLink(link)
                .text(text)
                .build();
    }
}
