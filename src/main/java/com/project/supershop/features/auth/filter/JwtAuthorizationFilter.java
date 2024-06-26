package com.project.supershop.features.auth.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.supershop.features.auth.services.JwtTokenService;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtAuthorizationFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthorizationFilter.class);

    private final JwtTokenService jwtTokenService;
    private final ObjectMapper mapper;

    public JwtAuthorizationFilter(JwtTokenService jwtTokenService, ObjectMapper mapper) {
        this.jwtTokenService = jwtTokenService;
        this.mapper = mapper;
    }

    @Override
    protected void doFilterInternal(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull FilterChain filterChain)
            throws ServletException, IOException {
        Map<String, Object> errorDetails = new HashMap<>();
        try {
            System.out.println(request);
            System.out.println(response);
            String accessToken = jwtTokenService.resolveToken(request);
            if (accessToken == null) {
                filterChain.doFilter(request, response);
                return;
            }
            logger.info("Access Token: {}", accessToken);
            Claims claims = jwtTokenService.resolveClaims(request);
            if (claims != null && jwtTokenService.validateClaims(claims)) {
                String email = claims.getSubject();
                logger.info("Email: {}", email);
                Authentication authentication = new UsernamePasswordAuthenticationToken(email, "", new ArrayList<>());
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (Exception e) {
            errorDetails.put("message", "Authentication Error");
            errorDetails.put("details", e.getMessage());
            response.setStatus(HttpStatus.FORBIDDEN.value());
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            mapper.writeValue(response.getWriter(), errorDetails);
            return;
        }
        filterChain.doFilter(request, response);
    }
}
