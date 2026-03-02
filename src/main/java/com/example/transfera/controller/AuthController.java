package com.example.transfera.controller;

import com.example.transfera.dto.AuthDTO.LoginRequestDTO;
import com.example.transfera.exceptions.FeatureNotImplemented;
import com.example.transfera.security.jwt.JwtUtil;
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

    public AuthController(AuthenticationManager authenticationManager, JwtUtil jwtUtil ) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
    }

    // Record is immutable, and I want the LoginResponse to stay immutable
    public record LoginResponse( String token ) {}

    @PostMapping( "/login" )
    public ResponseEntity<LoginResponse> login( @RequestBody LoginRequestDTO loginRequest ) {
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                loginRequest.getEmail(),
                loginRequest.getPassword()
        );

        Authentication authentication = authenticationManager.authenticate( authToken );

        String jwt = jwtUtil.generateToken( (User) authentication.getPrincipal() );

        return ResponseEntity.ok( new LoginResponse( jwt ) );
    }

    @PostMapping( "/logout" )
    public ResponseEntity<?> logout() {
        throw new FeatureNotImplemented();
    }

    // TODO token expiration
    //  User actively using app     → never logged out
    //  User closes app             → 3 min grace period
    //  User returns within 3 min   → still logged in
    //  User returns after 3 min    → must log in again
}
