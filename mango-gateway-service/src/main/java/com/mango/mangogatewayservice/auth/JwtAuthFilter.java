package com.mango.mangogatewayservice.auth;

import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;


@Component
@RequiredArgsConstructor
public class JwtAuthFilter implements WebFilter {
    private final JwtProvider jwtProvider;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        String token = jwtProvider.extractToken(exchange);
        if (token != null && jwtProvider.validateToken(token)) {
            Claims claims = jwtProvider.getClaimsFromToken(token);

            UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                    claims.getSubject(), null, null
            );

            SecurityContext context = new SecurityContextImpl(auth);

            return chain.filter(exchange)
                    .contextWrite(ReactiveSecurityContextHolder.withSecurityContext(Mono.just(context)));
        }

        return chain.filter(exchange);
    }
}
