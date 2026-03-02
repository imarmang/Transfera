package com.example.transfera.security.jwt;

import com.example.transfera.service.jwt.TokenBlacklistService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final TokenBlacklistService tokenBlacklistService;

    public JwtAuthenticationFilter(JwtUtil jwtUtil, TokenBlacklistService tokenBlacklistService) {
        this.jwtUtil = jwtUtil;
        this.tokenBlacklistService = tokenBlacklistService;
    }

    @Override
    protected void doFilterInternal( HttpServletRequest request,
                                     HttpServletResponse response,
                                     FilterChain filterChain ) throws ServletException, IOException {

        String authHeader = request.getHeader( "Authorization" );
        String token = null;

        // header: Authorization  Bearer[jwt]
        if ( authHeader != null && authHeader.startsWith( "Bearer " ) ) {
            token = authHeader.substring( 7 );
        }

        if ( token != null && jwtUtil.isTokenValid( token ) && !tokenBlacklistService.isBlacklisted( token ) ) {
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                    jwtUtil.getClaims( token ).getSubject(),
                    null,  // VERIFIED CREDENTIALS WHEN CALLING isTokenValid()
                    Collections.emptyList()  // roles and authorities
            );
            SecurityContextHolder.getContext().setAuthentication( authentication );
        }
        filterChain.doFilter( request, response );
    }
}
