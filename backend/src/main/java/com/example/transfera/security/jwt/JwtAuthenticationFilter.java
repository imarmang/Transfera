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

        System.out.println(">>> [JwtFilter] " + request.getMethod() + " " + request.getRequestURI());

        String authHeader = request.getHeader( "Authorization" );
        System.out.println(">>> [JwtFilter] Auth header: " + authHeader);

        String token = null;

        if ( authHeader != null && authHeader.startsWith( "Bearer " ) ) {
            token = authHeader.substring( 7 );
            System.out.println(">>> [JwtFilter] Token extracted: " + token.substring(0, Math.min(20, token.length())) + "...");
        } else {
            System.out.println(">>> [JwtFilter] No Bearer token found");
        }

        if ( token != null ) {
            System.out.println( ">>> [JwtFilter] Token valid: " + jwtUtil.isTokenValid( token ) );
            System.out.println( ">>> [JwtFilter] Token blacklisted: " + tokenBlacklistService.isBlacklisted( token ) );
        }

        if ( token != null && jwtUtil.isTokenValid( token ) && !tokenBlacklistService.isBlacklisted( token ) ) {
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                    jwtUtil.getClaims( token ).getSubject(),
                    null,
                    Collections.emptyList()
            );
            SecurityContextHolder.getContext().setAuthentication( authentication );
            System.out.println( ">>> [JwtFilter] Authentication set for: " + jwtUtil.getClaims( token ).getSubject() );
        } else {
            System.out.println( ">>> [JwtFilter] Authentication NOT set" );
        }

        filterChain.doFilter( request, response );
    }
}
