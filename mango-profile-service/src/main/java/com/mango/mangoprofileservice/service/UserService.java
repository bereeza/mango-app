package com.mango.mangoprofileservice.service;

import com.mango.mangoprofileservice.dto.Response;
import com.mango.mangoprofileservice.dto.UserInfoDto;
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
        return getUserInfoDto(exchange);
    }

    public Mono<Response> changeNickname(ServerWebExchange exchange, String nickname) {
        return getUserInfoDto(exchange)
                .flatMap(userInfoDto -> userRepository.findById(userInfoDto.getId())
                        .flatMap(user -> {
                            user.setNickname(nickname);
                            return userRepository.save(user)
                                    .then(updateRedisCache(exchange, userInfoDto))
                                    .then(Mono.just(Response.builder()
                                            .status(HttpStatus.OK)
                                            .message("Nickname changed successfully")
                                            .build()));
                        }));
    }

    private Mono<UserInfoDto> getUserInfoDto(ServerWebExchange exchange) {
        return tokenService.extractToken(exchange)
                .flatMap(token -> redisTemplate.opsForValue().get(token))
                .doOnNext(currentUser -> log.info("CurrentUser: {}", currentUser))
                .onErrorResume(e -> {
                    log.error("Error getting current user from Redis: {}", e.getMessage());
                    return Mono.empty();
                });
    }

    private Mono<Void> updateRedisCache(ServerWebExchange exchange, UserInfoDto userInfo) {
        return tokenService.extractToken(exchange)
                .flatMap(token -> redisTemplate.opsForValue()
                        .set(token, userInfo))
                .then();
    }
}
