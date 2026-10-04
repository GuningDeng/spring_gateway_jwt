package com.deng.auth_center.service;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import com.deng.auth_center.entity.SysRefreshToken;
import com.deng.auth_center.repository.SysRefreshTokenRepository;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;

@Slf4j 
@Service 
public class JwtService {
    @Value("${app.jwt.secret}")
    private String secret;

    @Value("${app.jwt.issuer}")
    private String issuer;

    @Value("${app.jwt.access-ttl-minutes}")
    private Integer accessTtlMintes;

    @Value("${app.jwt.refresh-ttl-days}")
    private Integer refreshTtlDays;

    @Value("${app.jwt.clock-skew-seconds:30}")
    private Integer skewSeconds;
    
    private final SysRefreshTokenRepository sysRefreshTokenRepository;

    public JwtService(SysRefreshTokenRepository sysRefreshTokenRepository) {
        this.sysRefreshTokenRepository = sysRefreshTokenRepository;
    }

    public Claims parseClaims(String token) {
        SecretKey key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        return Jwts.parserBuilder()
            .requireIssuer(issuer)
            .setSigningKey(key)
            .setAllowedClockSkewSeconds(skewSeconds)
            .build()
            .parseClaimsJws(token)
            .getBody();
    }

    public Boolean isExpired(Claims claims) {
        Date exp = claims.getExpiration();
        return exp != null && exp.before(new Date());
    }

    public UsernamePasswordAuthenticationToken builPasswordAuthenticationToken(Claims claims) {
        String username = claims.getSubject();
        String rolesStr = (String) claims.get("roles");
        List<SimpleGrantedAuthority> authorities = rolesStr == null ? List.of() : Arrays.stream(rolesStr.split(","))
            .filter(s -> !s.isBlank())
            .map(s -> new SimpleGrantedAuthority("ROLE_" + s))
            .collect(Collectors.toList());

        return new UsernamePasswordAuthenticationToken(username, null, authorities);
    }

    public String createAccessToken(String subject, List<String> roleCodes) {
        Long now = System.currentTimeMillis();
        Date iat = new Date(now);
        Date exp = new Date(now + accessTtlMintes * 60_000L);
        String roles = (roleCodes == null || roleCodes.isEmpty()) ? "" : String.join(",", roleCodes);
        String jti = UUID.randomUUID().toString();
        return Jwts.builder()
            .setSubject(subject)
            .setIssuer(issuer)
            .setIssuedAt(iat)
            .setExpiration(exp)
            .claim("roles", roles)
            .claim("jti", jti)
            .claim("type", "access")
            .signWith(getKey())
            .compact(); 
    }

    public String createRefreshToken(Long userId, String subject) {
        log.info("[Jwtservice] create refresh token with userId: {}, subject: {}", userId, subject);
        Long now = System.currentTimeMillis();
        Date iat = new Date(now);
        Date exp = new Date(now + refreshTtlDays * 24L * 60L * 60L * 1000L);
        String jti = UUID.randomUUID().toString();
        String token = Jwts.builder()
            .setSubject(subject)
            .setIssuer(issuer)
            .setIssuedAt(iat)
            .setExpiration(exp)
            .claim("jti", jti)
            .claim("type", "refresh")
            .signWith(getKey())
            .compact();

        log.info("[jwtService] JWT created with jti: {}", jti);

        SysRefreshToken refreshToken = new SysRefreshToken();
        refreshToken.setUserId(userId);
        refreshToken.setJti(jti);
        refreshToken.setStatus((byte) 0);
        refreshToken.setIssuedAt(iat);
        refreshToken.setExpiresAt(exp);
        log.info("[JwtService] Preparing to insert data into the database with userId: {}, jti: {}, status: {}", refreshToken.getUserId(), refreshToken.getJti(), refreshToken.getStatus());

        sysRefreshTokenRepository.save(refreshToken);
        log.info("[JwtService] Data inserted successfully");

        return token;
    }

    private SecretKey getKey() {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }
    
}
