package com.mango.mangogatewayservice.service;

import com.mango.mangogatewayservice.dto.user.UserRedisInfo;
import com.mango.mangogatewayservice.dto.user.UserSaveDto;
import com.mango.mangogatewayservice.dto.auth.AuthRequest;
import com.mango.mangogatewayservice.dto.auth.AuthResponse;
import com.mango.mangogatewayservice.entity.User;
import com.mango.mangogatewayservice.exception.UserAlreadyExistsException;
import com.mango.mangogatewayservice.exception.UserNotFoundException;
import com.mango.mangogatewayservice.auth.JwtProvider;
import com.mango.mangogatewayservice.repository.UserRepository;
import com.mango.mangogatewayservice.utils.GravatarUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    private final JwtProvider jwtProvider;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ReactiveRedisTemplate<String, UserRedisInfo> redisTemplate;

    public Mono<AuthResponse> findByEmail(AuthRequest req) {
        return userRepository.findByEmail(req.getEmail())
                .flatMap(user -> {
                    if (passwordEncoder.matches(req.getPassword(), user.getPassword())) {
                        return this.getAuthResponseMono(user);
                    } else {
                        return Mono.error(new BadCredentialsException("Invalid credentials."));
                    }
                })
                .switchIfEmpty(Mono.error(new UserNotFoundException("User not found.")));
    }

    public Mono<AuthResponse> saveUser(UserSaveDto req) {
        return userRepository.findByEmail(req.getEmail())
                .flatMap(existingUser -> Mono.error(new UserAlreadyExistsException("User already exists.")))
                .then(Mono.defer(() -> {
                    User newUser = buildSavedUser(req);
                    String avatar = GravatarUtil.gravatar(newUser.getEmail());
                    newUser.setAvatar(avatar);
                    
                    return userRepository.save(newUser)
                            .flatMap(this::getAuthResponseMono);
                }));
    }

    public Mono<Void> signOut(ServerWebExchange exchange) {
        return Mono.justOrEmpty(jwtProvider.extractToken(exchange))
                .flatMap(token -> redisTemplate.delete(token)
                        .doOnSuccess(deleted -> log.info("Token removed from Redis."))
                        .then(Mono.just(token)))
                .doOnTerminate(() -> log.info("User signed out.")).then();
    }

    private Mono<AuthResponse> getAuthResponseMono(User user) {
        UserRedisInfo userInfo = UserRedisInfo.builder()
                .id(user.getId())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .avatar(user.getAvatar())
                .build();

        return getResponseMono(user, userInfo);
    }

    private Mono<AuthResponse> getResponseMono(User savedUser, UserRedisInfo userInfo) {
        String token = jwtProvider.createToken(savedUser.getEmail());

        return saveUserToRedis(token, userInfo)
                .then(Mono.just(AuthResponse.builder()
                        .token(token)
                        .build()));
    }

    private Mono<Void> saveUserToRedis(String token, UserRedisInfo userInfoDto) {
        long expirationTime = 10800000;

        return redisTemplate.opsForValue()
                .set(token, userInfoDto, Duration.ofMillis(expirationTime))
                .then()
                .onErrorResume(e -> {
                    log.error("Error saving user info to Redis: {}", e.getMessage(), e);
                    return Mono.empty();
                });
    }

    private User buildSavedUser(UserSaveDto req) {
        return User.builder()
                .email(req.getEmail())
                .password(passwordEncoder.encode(req.getPassword()))
                .firstName(req.getFirstName())
                .lastName(req.getLastName())
                .build();
    }
}
