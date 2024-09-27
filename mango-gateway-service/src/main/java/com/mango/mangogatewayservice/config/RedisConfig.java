package com.mango.mangogatewayservice.config;

import com.mango.mangogatewayservice.dto.UserInfoDto;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.ReactiveKeyCommands;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.connection.ReactiveStringCommands;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {

    @Bean
    public ReactiveRedisTemplate<String, UserInfoDto> reactiveRedisTemplate(ReactiveRedisConnectionFactory factory) {
        Jackson2JsonRedisSerializer<UserInfoDto> serializer = new Jackson2JsonRedisSerializer<>(UserInfoDto.class);
        RedisSerializationContext.RedisSerializationContextBuilder<String, UserInfoDto> builder = RedisSerializationContext.newSerializationContext(new StringRedisSerializer());
        RedisSerializationContext<String, UserInfoDto> context = builder.value(serializer)
                .build();
        return new ReactiveRedisTemplate<>(factory, context);
    }

    @Bean
    public ReactiveKeyCommands keyCommands(final ReactiveRedisConnectionFactory reactiveRedisConnectionFactory) {
        return reactiveRedisConnectionFactory.getReactiveConnection()
                .keyCommands();
    }

    @Bean
    public ReactiveStringCommands stringCommands(final ReactiveRedisConnectionFactory reactiveRedisConnectionFactory) {
        return reactiveRedisConnectionFactory.getReactiveConnection()
                .stringCommands();
    }
}
