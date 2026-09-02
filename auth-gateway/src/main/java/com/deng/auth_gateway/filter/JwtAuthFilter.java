package com.deng.auth_gateway.filter;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class JwtAuthFilter implements GlobalFilter, Ordered {
    @Value("${app.jwt.secret}")
    private String secret;

    @Value("${app.jwt.issuer}")
    private String issuer;

    @Value("${app.jwt.clock-skew-seconds}")
    private Long clockSkewSeconds;

    @Override
    public int getOrder() {
        return -100;
    }

    // whitelist
    private static final List<String> EXCLUDE_PATHS = Arrays.asList(
        "/api/auth/login",
        "/api/auth/refresh",
        "/api/auth/register"
    );

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String path = exchange.getRequest().getURI().getPath();

        log.info("isExcludePath: {}, path: {}",isExcludePath(path), path);
        if (isExcludePath(path)) {
            log.info("Allow authentication interface: {}", path);
            return chain.filter(exchange);
        }

        String token = extractToken(exchange.getRequest());
        log.info("Current token: {}", token);
        if (token == null) {
            log.warn("Token not found");
            return unauthorizedResponse(exchange, "Token not found");
        }

        try {
            log.info("Start SecretKey: {}", secret);
            SecretKey key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
            log.info("key: {}", key);
            log.info("Start claims");
            
            Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();

            if (claims.isEmpty()) {
                log.error("claims is empty.");                
            }
            String type = claims.get("type", String.class);
            log.info("Token type: {}", type);
            if (!"access".equals(type)) {
                log.warn("Illegal token type",type);
                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                return exchange.getResponse().setComplete();
            }

            String username = claims.getSubject();
            String roles = claims.get("roles", String.class);
            
            log.info("JWT verification successful - user: {}, role: {}", username, roles);

            ServerHttpRequest mutated = exchange.getRequest().mutate()
                .header("X-Username", username != null ? username : "")
                .header("X-Roles", roles != null ? roles :"")
                .header("from", "Y")
                .build();
            
            return chain.filter(exchange.mutate().request(mutated).build());


        } catch (Exception e) {
            log.error("JWT verification failed: {}", e.getMessage());
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }
    }

    private Boolean isExcludePath(String path) {
        return EXCLUDE_PATHS.stream().anyMatch(path::startsWith);
    }

    private String extractToken(ServerHttpRequest request) {
        String bearerToken = request.getHeaders().getFirst("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);            
        }
        return null;
    }

    @SuppressWarnings("null")
    private Mono<Void> unauthorizedResponse(ServerWebExchange exchange, String message) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);

        String body = String.format("{\"code\":401,\"msg\":\"%s\"}", message);
        
        DataBuffer buffer = response.bufferFactory()
            .wrap(body.getBytes(StandardCharsets.UTF_8));
        
        return response.writeWith(Mono.just(buffer));
    }
}
