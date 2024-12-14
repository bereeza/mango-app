package com.mango.postservice.service;

import com.mango.postservice.dto.user.UserInfoDto;
import com.mango.postservice.dto.user.UserRedisInfo;
import com.mango.postservice.entity.User;
import com.mango.postservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserRedisService {
    private final ReactiveRedisTemplate<String, UserRedisInfo> redisTemplate;
    private final TokenService tokenService;
    private final UserRepository userRepository;

    public Mono<UserInfoDto> buildUser(ServerWebExchange exchange) {
        return getUserInfoFromRedis(exchange)
                .flatMap(user -> userRepository.findById(user.getId())
                        .map(this::buildUserInfo))
                .onErrorResume(e -> {
                    log.error("User building error: {}", e.getMessage());
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

    private UserInfoDto buildUserInfo(User user) {
        return UserInfoDto.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .avatar(user.getAvatar())
                .reputation(user.getReputation())
                .build();
    }
}
