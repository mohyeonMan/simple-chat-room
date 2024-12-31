package com.jhpark.simple_chat_room.security.filter;

import java.io.IOException;

import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.jhpark.simple_chat_room.security.service.AuthService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final String AUTH_PREFIX = "Bearer ";

    private final AuthService authService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        final String header = request.getHeader("Authorization");

        if (header != null && header.startsWith(AUTH_PREFIX)) {
            final String token = header.substring(AUTH_PREFIX.length());
            authService.checkAccessTokenAndSetAuthentication(token);
        }

        chain.doFilter(request, response);
    }
}