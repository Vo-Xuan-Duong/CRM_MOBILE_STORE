package com.example.Backend.configs;

import com.example.Backend.enums.TokenType;
import com.example.Backend.services.BlackListService;
import com.example.Backend.services.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;


@Slf4j
@Service
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;
    private final BlackListService blackListService;

    public  JwtAuthenticationFilter(JwtService jwtService, UserDetailsService userDetailsService, BlackListService blackListService) {
        this.userDetailsService = userDetailsService;
        this.jwtService = jwtService;
        this.blackListService = blackListService;
    }

    @Override
    protected void doFilterInternal(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull FilterChain filterChain) throws ServletException, IOException {
        log.info("---------------------------JWT_AUTHENTICATION_FILTER---------------------------------------");

        // Skip JWT validation for OpenAPI and Swagger endpoints
        String requestPath = request.getRequestURI();
        if (requestPath.startsWith("/swagger-ui") ||
            requestPath.startsWith("/v3/api-docs") ||
            requestPath.startsWith("/api-docs") ||
            requestPath.startsWith("/swagger-resources") ||
            requestPath.startsWith("/webjars") ||
            requestPath.equals("/swagger-ui.html") ||
            requestPath.startsWith("/api/auth/") ||
            requestPath.startsWith("/api/public/") ||
            requestPath.equals("/actuator/health")) {
            filterChain.doFilter(request, response);
            return;
        }

        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String jwtToken = authHeader.substring(7);
        log.info("JWT Token: {}", jwtToken);
        // Here you would typically validate the JWT token and set the authentication in the security context

        try{
            String username = jwtService.extractName(jwtToken, TokenType.ACCESS_TOKEN);
            if(username==null){
                log.warn("Invalid JWT token: unable to extract username");
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid JWT Token");
                return;
            }

            // Check if token is blacklisted
            String tokenId = jwtService.extractTokenId(jwtToken, TokenType.ACCESS_TOKEN);
            if (blackListService.isTokenBlacklisted(tokenId) || blackListService.areAllUserTokensBlacklisted(username)) {
                log.warn("Blacklisted token attempted access for user: {}", username);
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token has been revoked");
                return;
            }

            if(SecurityContextHolder.getContext().getAuthentication()==null){
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                log.info("Authorities: {}", userDetails.getAuthorities());
                if(jwtService.validateToken(jwtToken, userDetails, TokenType.ACCESS_TOKEN)) {
                    
                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                            userDetails, null, userDetails.getAuthorities());

                    authentication.setDetails(tokenId);
                    SecurityContextHolder.getContext().setAuthentication(authentication);

                    filterChain.doFilter(request, response);
                } else {
                    log.warn("Invalid JWT token for user: {}", username);
                    response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid JWT Token");
                }
            }
        }catch (Exception e){
            log.error("Error processing JWT token: {}", e.getMessage());
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid JWT Token");
        }
    }
}
