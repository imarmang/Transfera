package com.example.transfera.controller;

import com.example.transfera.dto.AuthDTO.LoginRequestDTO;
import com.example.transfera.dto.AuthDTO.LoginResponseDTO;
import com.example.transfera.dto.AuthDTO.LogoutResponseDTO;
import com.example.transfera.dto.AuthDTO.RegisterResponseDTO;
import com.example.transfera.dto.UserDTO.CreateUserRequestDTO;
import com.example.transfera.dto.UserDTO.UserCredentialsResponseDTO;
import com.example.transfera.security.jwt.JwtUtil;
import com.example.transfera.service.jwt.TokenBlacklistService;
import com.example.transfera.service.userCredential.CreateUserCredentialsService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
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
    private final CreateUserCredentialsService createUserCredentialsService;

    public AuthController(AuthenticationManager authenticationManager, JwtUtil jwtUtil, TokenBlacklistService tokenBlacklistService, CreateUserCredentialsService createUserCredentialsService) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.tokenBlacklistService = tokenBlacklistService;
        this.createUserCredentialsService = createUserCredentialsService;
    }

    @PostMapping( "/login" )
    public ResponseEntity<LoginResponseDTO> login(@RequestBody LoginRequestDTO loginRequest ) {
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                loginRequest.getEmail(),
                loginRequest.getPassword()
        );

        Authentication authentication = authenticationManager.authenticate( authToken );

        String jwt = jwtUtil.generateToken( ( User) authentication.getPrincipal() );

        return ResponseEntity.ok( new LoginResponseDTO( jwt ) );
    }

    // POST  --> REGISTER THE USER
    // IF SUCCESSFUL, THE FRONT END WILL render to the fill out your personal information page
    @PostMapping("/register" )
    public ResponseEntity<RegisterResponseDTO> createUser( @RequestBody CreateUserRequestDTO user ) {
        ResponseEntity<UserCredentialsResponseDTO> created =  createUserCredentialsService.execute( user );

        // CHECK IF IT SUCCESSFULLY ADDED THE EMAIL AND PASSWORD
        if ( !created.getStatusCode().is2xxSuccessful() ) {
            return ResponseEntity.status( created.getStatusCode() ).build();
        }

        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                user.getEmail(),
                user.getPassword()
        );

        Authentication authentication = authenticationManager.authenticate( authToken );
        String jwt = jwtUtil.generateToken( ( User) authentication.getPrincipal() );

        return ResponseEntity.status(HttpStatus.CREATED ).body( new RegisterResponseDTO( jwt ) );

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
    @PostMapping("/logout")
    public ResponseEntity<LogoutResponseDTO> logout(HttpServletRequest request ) {

        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            System.out.println("LOGOUT FAILED: No token provided");
            return ResponseEntity.badRequest().body(new LogoutResponseDTO("No token provided"));
        }

        String token = authHeader.substring(7);

        // TODO: add the token to the blacklist, temporary solution
        tokenBlacklistService.blacklistToken(token);

        LogoutResponseDTO resp = new LogoutResponseDTO("Logged out Successfully");
        System.out.println("Logout response JSON: {\"message\":\"" + resp.message() + "\"}");

        return ResponseEntity.ok(resp);
    }

}
