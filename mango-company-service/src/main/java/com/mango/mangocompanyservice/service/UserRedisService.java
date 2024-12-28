package com.mango.mangocompanyservice.service;

import com.mango.mangocompanyservice.dto.user.UserInfoDto;
import com.mango.mangocompanyservice.dto.user.UserRedisInfo;
import com.mango.mangocompanyservice.entity.User;
import com.mango.mangocompanyservice.exception.UnauthorizedUserException;
import com.mango.mangocompanyservice.repository.UserRepository;
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
                    log.error("Error getting current user from Redis: {}", e.getMessage());
                    return Mono.error(new UnauthorizedUserException("Error getting current user from Redis."));
                });
    }

    private Mono<UserRedisInfo> getUserInfoFromRedis(ServerWebExchange exchange) {
        return tokenService.extractToken(exchange)
                .flatMap(token -> redisTemplate.opsForValue().get(token))
                .switchIfEmpty(Mono.error(new UnauthorizedUserException("Error getting current user from Redis.")));
    }

    private UserInfoDto buildUserInfo(User user) {
        return UserInfoDto.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .avatar(user.getAvatar())
                .email(user.getEmail())
                .cv(user.getCv())
                .build();
    }
}
