package com.mango.mangoprofileservice.service;

import com.mango.mangoprofileservice.dto.Response;
import com.mango.mangoprofileservice.dto.UserInfoDto;
import com.mango.mangoprofileservice.dto.UserRedisInfo;
import com.mango.mangoprofileservice.entity.User;
import com.mango.mangoprofileservice.exception.UserNotFoundException;
import com.mango.mangoprofileservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    private final ReactiveRedisTemplate<String, UserRedisInfo> redisTemplate;
    private final UserRepository userRepository;
    private final TokenService tokenService;
    private final BucketService bucketService;

    public Mono<UserInfoDto> getCurrentUser(ServerWebExchange exchange) {
        return buildUser(exchange);
    }

    public Mono<Response> deleteCurrentUser(ServerWebExchange exchange) {
        return getCurrentUser(exchange)
                .flatMap(this::deleteUserIfExists);
    }

    public Mono<Response> updateUserLinks(ServerWebExchange exchange, String link) {
        return getCurrentUser(exchange)
                .flatMap(currentUser ->
                        userRepository.updateLink(currentUser.getId(), link)
                                .then(Mono.just(Response.builder()
                                        .message("Link updated successfully.")
                                        .status(HttpStatus.OK)
                                        .build()))
                )
                .onErrorResume(e -> {
                    log.error("Error updating link: {}", e.getMessage());
                    return Mono.error(new IllegalArgumentException(e.getMessage()));
                });
    }

    public Mono<Response> updateUserAboutSection(ServerWebExchange exchange, String about) {
        return getCurrentUser(exchange)
                .flatMap(currentUser ->
                        userRepository.updateUserAbout(currentUser.getId(), about)
                                .then(Mono.just(Response.builder()
                                        .message("About section updated successfully.")
                                        .status(HttpStatus.OK)
                                        .build()))
                )
                .onErrorResume(e -> {
                    log.error("Error updating about section: {}", e.getMessage());
                    return Mono.error(new IllegalArgumentException(e.getMessage()));
                });
    }

    public Mono<Response> updateUserCV(ServerWebExchange exchange, FilePart file) {
        return getCurrentUser(exchange)
                .flatMap(currentUser -> {
                    String link = bucketService.save(currentUser.getId(), file);
                    return userRepository.updateUserCV(currentUser.getId(), link)
                            .then(Mono.just(Response.builder()
                                    .status(HttpStatus.OK)
                                    .message("CV successfully saved")
                                    .build()));
                })
                .onErrorResume(e -> {
                    log.error("Error updating file. {}", e.getMessage());
                    return Mono.error(new IllegalArgumentException(e.getMessage()));
                });
    }

    @SneakyThrows
    public Mono<ResponseEntity<Resource>> findCVById(long id) {
        return userRepository.findById(id)
                .flatMap(user -> {
                    String url = user.getCv();

                    if (url == null || url.isEmpty()) {
                        return Mono.error(new IllegalArgumentException("CV not found for user"));
                    }

                    Resource fileResource = bucketService.getFileByUrl(url);

                    return Mono.just(ResponseEntity.ok()
                            .contentType(MediaType.APPLICATION_PDF)
                            .body(fileResource));
                })
                .onErrorResume(e -> {
                    log.error("Error fetching CV. {}", e.getMessage());
                    return Mono.error(new IllegalArgumentException(e.getMessage()));
                });
    }

    private Mono<Response> deleteUserIfExists(UserInfoDto userInfo) {
        Long userId = userInfo.getId();
        return userRepository.existsById(userId)
                .flatMap(exists -> exists ? deleteUser(userId) : Mono.error(new UserNotFoundException("Current user not found")));
    }

    private Mono<Response> deleteUser(Long userId) {
        return userRepository.deleteById(userId)
                .then(Mono.fromSupplier(() -> Response.builder()
                        .message("User deleted successfully.")
                        .status(HttpStatus.NO_CONTENT)
                        .build()))
                .onErrorResume(e -> {
                    log.error("User wasn't deleted.");
                    return Mono.empty();
                });
    }

    private Mono<UserInfoDto> buildUser(ServerWebExchange exchange) {
        return getUserInfoFromRedis(exchange)
                .flatMap(user -> userRepository.findById(user.getId())
                        .map(this::buildUserInfo))
                .onErrorResume(e -> {
                    log.error("User creation error {}", e.getMessage());
                    return Mono.empty();
                });
    }

    private Mono<UserRedisInfo> getUserInfoFromRedis(ServerWebExchange exchange) {
        return tokenService.extractToken(exchange)
                .flatMap(token -> redisTemplate.opsForValue().get(token))
                .onErrorResume(e -> {
                    log.error("Error getting current user from Redis: {}", e.getMessage());
                    return Mono.empty();
                });
    }

    private UserInfoDto buildUserInfo(User userInfo) {
        return UserInfoDto.builder()
                .id(userInfo.getId())
                .email(userInfo.getEmail())
                .firstName(userInfo.getFirstName())
                .lastName(userInfo.getLastName())
                .avatar(userInfo.getAvatar())
                .cv(userInfo.getCv())
                .about(userInfo.getAbout())
                .reputation(userInfo.getReputation())
                .link(userInfo.getLink())
                .build();
    }
}
