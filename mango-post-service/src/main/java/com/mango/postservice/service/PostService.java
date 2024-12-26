package com.mango.postservice.service;

import com.mango.postservice.dto.comment.CommentInfoDto;
import com.mango.postservice.dto.comment.CommentSaveDto;
import com.mango.postservice.dto.post.PostInfoDto;
import com.mango.postservice.dto.Response;
import com.mango.postservice.dto.post.PostShortInfoDto;
import com.mango.postservice.dto.user.UserInfoDto;
import com.mango.postservice.entity.Comment;
import com.mango.postservice.entity.Post;
import com.mango.postservice.exception.AccessForbiddenException;
import com.mango.postservice.exception.BadRequestException;
import com.mango.postservice.exception.CommentNotFoundException;
import com.mango.postservice.exception.PostNotFoundException;
import com.mango.postservice.repository.CommentRepository;
import com.mango.postservice.repository.PostRepository;
import com.mango.postservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class PostService {
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final BucketService bucketService;
    private final UserRedisService userRedisService;
    private final UserRepository userRepository;

    public Mono<Response<String>> savePost(ServerWebExchange exchange, String text, FilePart file) {
        return userRedisService.buildUser(exchange)
                .flatMap(user -> savePostAndIncrementReputation(user, text, file))
                .map(post -> Response.<String>builder()
                        .code(HttpStatus.OK.value())
                        .message("Post successfully saved.")
                        .body("Post successfully saved.")
                        .build())
                .onErrorResume(e -> {
                    log.error("Post wasn't saved: {}", e.getMessage());
                    return Mono.error(new BadRequestException(e.getMessage()));
                });
    }

    public Mono<PostInfoDto> getPostById(long id) {
        return postRepository.findById(id)
                .map(this::buildPostById)
                .switchIfEmpty(Mono.error(new PostNotFoundException("Post not found.")));
    }

    public Mono<Response<String>> deletePost(ServerWebExchange exchange, long id) {
        return userRedisService.buildUser(exchange)
                .flatMap(user -> postRepository.findById(id)
                        .flatMap(post -> {
                            if (user.getId() != post.getUserId()) {
                                log.error("You cannot delete other users' posts.");
                                return Mono.error(new AccessForbiddenException("You cannot delete other users' posts."));
                            }

                            bucketService.dropFile(post.getPhotoLink());

                            return postRepository.deleteById(id)
                                    .then(Mono.just(Response.<String>builder()
                                            .code(HttpStatus.NO_CONTENT.value())
                                            .message("Post deleted successfully.")
                                            .body("Post deleted successfully.")
                                            .build()));
                        })
                        .switchIfEmpty(Mono.error(new PostNotFoundException("Post not found."))))
                .onErrorResume(e -> Mono.error(new AccessForbiddenException(e.getMessage())));
    }

    public Mono<Response<String>> updatePost(ServerWebExchange exchange,
                                             long id,
                                             String text) {
        return userRedisService.buildUser(exchange)
                .flatMap(user -> postRepository.findById(id)
                        .flatMap(post -> {
                            if (user.getId() != post.getUserId()) {
                                log.error("You cannot update other users' posts.");
                                return Mono.error(new AccessForbiddenException("You cannot update other users' posts."));
                            }

                            return postRepository.updateText(post.getId(), text)
                                    .then(Mono.just(Response.<String>builder()
                                            .code(HttpStatus.OK.value())
                                            .message("Post updated successfully.")
                                            .body("Post updated successfully.")
                                            .build()));

                        })
                        .switchIfEmpty(Mono.error(new PostNotFoundException("Post not found."))))
                .onErrorResume(e -> Mono.error(new AccessForbiddenException(e.getMessage())));
    }

    public Flux<PostInfoDto> findAllPosts(Pageable pageable) {
        return postRepository.findAllPosts(pageable)
                .onErrorResume(e -> {
                    log.error("Bad request: {}", e.getMessage());
                    return Mono.error(new BadRequestException(e.getMessage()));
                });
    }

    public Flux<PostInfoDto> findAllUserPosts(Pageable pageable, long id) {
        return postRepository.findAllUserPosts(pageable, id)
                .switchIfEmpty(Flux.empty());
    }

    public Flux<PostInfoDto> findAllByText(Pageable pageable, String text) {
        return postRepository.findAllByText(pageable, text)
                .switchIfEmpty(Flux.empty());
    }

    public Flux<PostShortInfoDto> findTopPostsByComments() {
        return postRepository.findTopPostsByComments()
                .switchIfEmpty(Flux.empty());
    }

    public Mono<Response<String>> saveComment(ServerWebExchange exchange, long id, CommentSaveDto dto) {
        return userRedisService.buildUser(exchange)
                .flatMap(user -> postRepository.findById(id)
                        .flatMap(post -> saveCommentAndIncrementReputation(user, post, dto))
                        .switchIfEmpty(Mono.error(new PostNotFoundException("Post not found."))))
                .onErrorResume(e -> Mono.error(new PostNotFoundException(e.getMessage())));
    }

    public Mono<Response<String>> deleteComment(ServerWebExchange exchange, long id, long commentId) {
        return userRedisService.buildUser(exchange)
                .flatMap(user -> postRepository.findById(id)
                        .flatMap(post -> commentRepository.findById(commentId)
                                .flatMap(comment -> {
                                    if (user.getId() != post.getUserId()) {
                                        return Mono.error(new AccessForbiddenException("You don't have permission."));
                                    }

                                    return commentRepository.deleteById(commentId)
                                            .then(Mono.just(Response.<String>builder()
                                                    .code(HttpStatus.OK.value())
                                                    .message("Comment deleted successfully.")
                                                    .body("Comment deleted successfully.")
                                                    .build()));
                                })
                                .switchIfEmpty(Mono.error(new CommentNotFoundException("Comment not found.")))
                        )
                        .switchIfEmpty(Mono.error(new PostNotFoundException("Post not found.")))
                )
                .onErrorResume(e -> Mono.error(new PostNotFoundException(e.getMessage())));
    }

    public Flux<CommentInfoDto> findAll(long postId, Pageable pageable) {
        return commentRepository.findAllBy(postId, pageable)
                .onErrorResume(e -> {
                    log.error("Post not found: {}", e.getMessage());
                    return Mono.error(new PostNotFoundException(e.getMessage()));
                });
    }

    private Mono<Post> savePostAndIncrementReputation(UserInfoDto user, String text, FilePart file) {
        long maxSize = 5 * 1024 * 1024;

        if (!file.filename().matches(".*\\.(png|jpg|jpeg|webp)$") || file.headers().getContentLength() > maxSize) {
            return Mono.error(new BadRequestException("Upload an image file (PNG, JPG, WEBP) up to 5 MB."));
        }

        return bucketService.saveFile(file)
                .flatMap(link -> {
                    Post post = buildPost(user.getId(), text, link);

                    return postRepository.save(post)
                            .flatMap(savedPost ->
                                    userRepository.updateReputation(user.getId(), 2L).thenReturn(savedPost));
                });
    }

    private Mono<Response<String>> saveCommentAndIncrementReputation(UserInfoDto user,
                                                                     Post post,
                                                                     CommentSaveDto dto) {
        Comment comment = buildComment(user, post.getId(), dto);

        return commentRepository.save(comment)
                .flatMap(savedComment -> userRepository.updateReputation(user.getId(), 1L)
                        .then(Mono.just(Response.<String>builder()
                                .code(HttpStatus.OK.value())
                                .message("Comment successfully saved and reputation updated.")
                                .body(dto.getComment())
                                .build())));
    }

    private Comment buildComment(UserInfoDto user, long id, CommentSaveDto dto) {
        return Comment.builder()
                .postId(id)
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .avatar(user.getAvatar())
                .comment(dto.getComment())
                .createdAt(LocalDateTime.now())
                .build();
    }

    private PostInfoDto buildPostById(Post post) {
        return PostInfoDto.builder()
                .id(post.getId())
                .userId(post.getUserId())
                .text(post.getText())
                .photoLink(post.getPhotoLink())
                .createdAt(post.getCreatedAt())
                .build();
    }

    private Post buildPost(long id, String text, String link) {
        return Post.builder()
                .userId(id)
                .photoLink(link)
                .text(text)
                .createdAt(LocalDateTime.now())
                .build();
    }
}
