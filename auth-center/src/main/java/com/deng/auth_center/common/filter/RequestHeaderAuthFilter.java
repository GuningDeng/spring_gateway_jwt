package com.deng.auth_center.common.filter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j 
@Component 
public class RequestHeaderAuthFilter extends OncePerRequestFilter {@Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String from = request.getHeader("from");
        if (!"Y".equals(from)) {
            log.debug("Non-gateway request: {}", request.getRequestURI());
            filterChain.doFilter(request, response);
            return;
        }

        String username = request.getHeader("X-Username");
        String rolesStr = request.getHeader("X-Roles");

        if (username != null && !username.isEmpty()) {
            List<SimpleGrantedAuthority> authorities = rolesStr != null && !rolesStr.isEmpty()
            ? Arrays.stream(rolesStr.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                .collect(Collectors.toList())
            : List.of();

            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(username, null, authorities);
            authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);

            log.debug("Rebuild authentication using request header - user: {}, role: {}", username, rolesStr);
        }

        filterChain.doFilter(request, response);
    }
    
}
