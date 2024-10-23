package com.mango.mangogatewayservice.auth;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import java.util.Date;

@Slf4j
@Component
public class JwtProvider {
    @Value("${jwt.secret}")
    private String key;
    private static final String AUTH_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";

    public String createToken(String email) {
        Claims claims = Jwts.claims().setSubject(email);

        Date now = new Date();
        Date validity = new Date(now.getTime() + 10800000);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(validity)
                .signWith(SignatureAlgorithm.HS256, key)
                .compact();
    }

    protected String extractToken(ServerWebExchange exchange) {
        String token = exchange.getRequest()
                .getHeaders()
                .getFirst(AUTH_HEADER);

        if (token != null && token.startsWith(BEARER_PREFIX)) {
            return token.substring(7);
        }
        return null;
    }

    protected boolean validateToken(String token) {
        try {
            Jwts.parser().setSigningKey(key)
                    .parseClaimsJws(token);

            return true;
        } catch (Exception e) {
            return false;
        }
    }

    protected Claims getClaimsFromToken(String token) {
        return Jwts.parser().setSigningKey(key)
                .parseClaimsJws(token)
                .getBody();
    }
}
