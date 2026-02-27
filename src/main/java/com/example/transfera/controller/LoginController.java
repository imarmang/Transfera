package com.example.transfera.controller;

import com.example.transfera.domain.user.UserCredentials;
import com.example.transfera.domain.user.UserCredentialsRepository;
import com.example.transfera.dto.UserDTO.UserCredentialsResponseDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;

import java.util.Optional;


/**
 * ⚠️  UNSECURE LOGIN — FOR TESTING / FRONTEND CONNECTION ONLY ⚠️
 *
 * This controller does a plain-text email + password comparison.
 * No JWT, no hashing, no session.  Replace with proper security before production.
 */

@RestController
@CrossOrigin( origins = "*" )

public class LoginController {
    private final UserCredentialsRepository userCredentialsRepository;

    public LoginController( UserCredentialsRepository userCredentialsRepository  ) {
        this.userCredentialsRepository = userCredentialsRepository;
    }

    public record LoginResponse(String loginSuccessful, UserCredentialsResponseDTO userCredentialsResponseDTO) {
    }
    public record LoginRequest( String email, String password ) {}

    @PostMapping("/login")
    public ResponseEntity<?> login( @RequestBody LoginRequest loginRequest ){
        Optional<UserCredentials> maybeUser = userCredentialsRepository.findByEmail( loginRequest.email() );

        if (maybeUser.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body( "Invalid email" );
        }

        UserCredentials userCredentials = maybeUser.get();

        if ( !loginRequest.password().equals( userCredentials.getPassword()) ) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body( "Invalid password" );
        }
        return ResponseEntity.ok( new LoginResponse( "Login successful", new UserCredentialsResponseDTO(userCredentials) ) );

    }
}


//public class LoginController {
//    private final AuthenticationManager manager;
//
//
//    public LoginController( AuthenticationManager manager ) {
//        this.manager = manager;
//    }
//
//    @PostMapping( "/login" )
//    public ResponseEntity<String> login( @RequestBody CustomUser user ) {
//
//        // this token is different from JWT json web token
//        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(
//                user.getUsername(),
//                user.getPassword()
//        );
//
//        // this will fault if the credentials are not valid
//        Authentication authentication = manager.authenticate( token );
//
//        SecurityContextHolder.getContext().setAuthentication( authentication );
//
//        String jwtToken = JwtUtil.generateToken( (User) authentication.getPrincipal() );
//        return ResponseEntity.ok( jwtToken );
//    }
//
//}
