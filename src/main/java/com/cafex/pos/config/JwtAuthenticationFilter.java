package com.cafex.pos.config;

import com.cafex.pos.service.AuthService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final AuthService authService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String requestURI = request.getRequestURI();
        log.debug("Processing request: {} {}", request.getMethod(), requestURI);

        try {
            String token = parseToken(request);
            log.debug("Token present: {}", token != null);

            if (token != null) {
                boolean isValid = authService.validateToken(token);
                log.debug("Token valid: {}", isValid);

                if (isValid) {
                    String username = authService.extractUsernameFromToken(token);
                    log.debug("Extracted username: {}", username);

                    authService.getCurrentUser(username).ifPresentOrElse(user -> {
                        log.debug("User found: {}, active: {}", user.getUsername(), user.getIsActive());
                        if (user.getIsActive() == com.cafex.pos.entity.User.ActiveStatus.Y) {
                            UserDetails userDetails = org.springframework.security.core.userdetails.User
                                .withUsername(user.getUsername())
                                .password("")
                                .authorities("ROLE_" + user.getRole().name())
                                .accountExpired(false)
                                .accountLocked(false)
                                .credentialsExpired(false)
                                .disabled(false)
                                .build();

                            UsernamePasswordAuthenticationToken authentication =
                                new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                            SecurityContextHolder.getContext().setAuthentication(authentication);
                            log.debug("Authentication set for user: {}", username);
                        } else {
                            log.warn("User {} is not active", username);
                        }
                    }, () -> log.warn("User {} not found in database", username));
                } else {
                    log.warn("Invalid token for request: {}", requestURI);
                }
            } else {
                log.debug("No token provided for secured endpoint: {}", requestURI);
            }
        } catch (Exception e) {
            log.error("Cannot set user authentication: {}", e.getMessage(), e);
        }

        filterChain.doFilter(request, response);
    }

    private String parseToken(HttpServletRequest request) {
        String headerAuth = request.getHeader("Authorization");

        if (StringUtils.hasText(headerAuth) && headerAuth.startsWith("Bearer ")) {
            return headerAuth.substring(7);
        }

        return null;
    }
}