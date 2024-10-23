package com.mango.mangogatewayservice.service;

import com.mango.mangogatewayservice.dto.UserInfoDto;
import com.mango.mangogatewayservice.dto.auth.AuthRequest;
import com.mango.mangogatewayservice.dto.auth.AuthResponse;
import com.mango.mangogatewayservice.entity.User;
import com.mango.mangogatewayservice.exception.UserAlreadyExistsException;
import com.mango.mangogatewayservice.exception.UserNotFoundException;
import com.mango.mangogatewayservice.auth.JwtProvider;
import com.mango.mangogatewayservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    private final JwtProvider jwtProvider;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ReactiveRedisTemplate<String, UserInfoDto> redisTemplate;

    public Mono<AuthResponse> findByEmail(AuthRequest req) {
        return userRepository.findByEmail(req.getEmail())
                .flatMap(user -> {
                    if (passwordEncoder.matches(req.getPassword(), user.getPassword())) {
                        UserInfoDto userInfo = UserInfoDto.builder()
                                .id(user.getUserid())
                                .email(user.getEmail())
                                .build();

                        return getResponseMono(user, userInfo);
                    } else {
                        return Mono.error(new BadCredentialsException("Invalid credentials"));
                    }
                })
                .switchIfEmpty(Mono.error(new UserNotFoundException("User not found")));
    }

    public Mono<AuthResponse> saveUser(AuthRequest req) {
        return userRepository.findByEmail(req.getEmail())
                .flatMap(existingUser -> Mono.error(new UserAlreadyExistsException("User already exists")))
                .then(Mono.defer(() ->
                        userRepository.save(buildUser(req))
                                .flatMap(savedUser -> {
                                    UserInfoDto userInfo = UserInfoDto.builder()
                                            .id(savedUser.getUserid())
                                            .email(savedUser.getEmail())
                                            .build();

                                    return getResponseMono(savedUser, userInfo);
                                })
                ));
    }

    private Mono<AuthResponse> getResponseMono(User savedUser, UserInfoDto userInfo) {
        String token = jwtProvider.createToken(savedUser.getEmail());

        return saveUserToRedis(token, userInfo)
                .then(Mono.just(AuthResponse.builder()
                        .token(token)
                        .build()));
    }

    private Mono<Void> saveUserToRedis(String token, UserInfoDto userInfoDto) {
        long expirationTime = 10800000;

        return redisTemplate.opsForValue()
                .set(token, userInfoDto, Duration.ofMillis(expirationTime))
                .doOnNext(savedUserInfo -> log.info("Saved user info from Redis after save: {}", savedUserInfo))
                .then()
                .onErrorResume(e -> {
                    log.error("Error saving user info to Redis: {}", e.getMessage(), e);
                    return Mono.empty();
                });
    }

    private User buildUser(AuthRequest req) {
        return User.builder()
                .email(req.getEmail())
                .password(passwordEncoder.encode(req.getPassword()))
                .build();
    }
}
