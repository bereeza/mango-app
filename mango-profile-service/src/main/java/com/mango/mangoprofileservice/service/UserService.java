package com.mango.mangoprofileservice.service;

import com.mango.mangoprofileservice.dto.Response;
import com.mango.mangoprofileservice.dto.UserInfoDto;
import com.mango.mangoprofileservice.entity.User;
import com.mango.mangoprofileservice.exception.UserNotFoundException;
import com.mango.mangoprofileservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    private final ReactiveRedisTemplate<String, UserInfoDto> redisTemplate;
    private final UserRepository userRepository;
    private final TokenService tokenService;

    public Mono<UserInfoDto> getCurrentUser(ServerWebExchange exchange) {
        return buildUser(exchange);
    }

    public Mono<Response> deleteCurrentUser(ServerWebExchange exchange) {
        return getCurrentUser(exchange)
                .flatMap(this::deleteUserIfExists);
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

    private Mono<UserInfoDto> getUserInfoFromRedis(ServerWebExchange exchange) {
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
                .links(userInfo.getLinks())
                .build();
    }
}
