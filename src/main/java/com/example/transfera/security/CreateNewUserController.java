package com.example.transfera.security;

import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
public class CreateNewUserController {

    private final PasswordEncoder passwordEncoder;

    private final CustomUserRepository customUserRepository;

    public CreateNewUserController( PasswordEncoder passwordEncoder, CustomUserRepository customUserRepository ) {
        this.passwordEncoder = passwordEncoder;
        this.customUserRepository = customUserRepository;
    }

    @PostMapping( "/createnewuser" )
    public ResponseEntity<String> createNewUser( @RequestBody CustomUser customUser ) {

        // THIS SHOULD BE IN A SERVICE CLASS --> KEEPING IT SIMPLE

        // need better error handling with custom exceptions ->

        Optional<CustomUser> optionalUser = customUserRepository.findById(customUser.getUsername() );

        if ( !optionalUser.isPresent() ) {
            customUserRepository.save( new CustomUser( customUser.getUsername(), passwordEncoder.encode(customUser.getPassword() ) ) );
            return ResponseEntity.ok().body( "Success" );

        }

        return ResponseEntity.badRequest().body( "Failure" );
    }
}
