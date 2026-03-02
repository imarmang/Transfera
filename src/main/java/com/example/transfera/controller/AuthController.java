package com.example.transfera.controller;

import com.example.transfera.dto.AuthDTO.LoginRequestDTO;
import com.example.transfera.exceptions.FeatureNotImplemented;
import com.example.transfera.security.jwt.JwtUtil;
import com.example.transfera.service.jwt.TokenBlacklistService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin( origins = "*" )
@RequestMapping("/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final TokenBlacklistService tokenBlacklistService;

    public AuthController(AuthenticationManager authenticationManager, JwtUtil jwtUtil, TokenBlacklistService tokenBlacklistService ) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.tokenBlacklistService = tokenBlacklistService;
    }

    // Record is immutable, and I want the LoginResponse and LogoutResponse to stay immutable
    public record LoginResponse( String token ) {}

    public record LogoutResponse( String message ) {}

    @PostMapping( "/login" )
    public ResponseEntity<LoginResponse> login( @RequestBody LoginRequestDTO loginRequest ) {
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                loginRequest.getEmail(),
                loginRequest.getPassword()
        );

        Authentication authentication = authenticationManager.authenticate( authToken );

        String jwt = jwtUtil.generateToken( ( User) authentication.getPrincipal() );

        return ResponseEntity.ok( new LoginResponse( jwt ) );
    }

    @PostMapping( "/logout" )
    public ResponseEntity<LogoutResponse> logout( HttpServletRequest request ) {

        String authHeader = request.getHeader( "Authorization" );

        if ( authHeader == null || !authHeader.startsWith( "Bearer " ) ) {
            return ResponseEntity.badRequest().body( new LogoutResponse( "No token provided" ) );
        }

        String token = authHeader.substring( 7 );

        // TODO: add the token to the blacklist, temporary solution
        tokenBlacklistService.blacklistToken( token );

        return ResponseEntity.ok( new LogoutResponse( "Logged out Successfully" ) );
    }

    // TODO token expiration
    //  User actively using app     → never logged out
    //  User closes app             → 3 min grace period
    //  User returns within 3 min   → still logged in
    //  User returns after 3 min    → must log in again
    //  Implement jti instead of storing the full token
    //  1. Tokens are large → storing full strings wastes memory.
    //  2. Doesn’t expire automatically → unless you manually clean, the set grows forever.
    //  3. Doesn’t survive restart → if the app restarts, all “revoked” tokens become valid again.
    //  4. Doesn’t work in multi-instance deployments (multiple servers) unless you share the blacklist.
    //  Storing just jti solves #1, and using something like Redis with TTL solves #2–#4.
}
