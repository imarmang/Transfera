package com.example.transfera.service.google;

import com.example.transfera.domain.user.UserCredentials;
import com.example.transfera.domain.user.UserCredentialsRepository;
import com.example.transfera.dto.AuthDTO.google.GoogleAuthRequestDTO;
import com.example.transfera.dto.AuthDTO.google.GoogleAuthResponseDTO;
import com.example.transfera.exceptions.customExceptions.GoogleAuthException;
import com.example.transfera.exceptions.customExceptions.UserNotFound;
import com.example.transfera.security.jwt.JwtUtil;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class GoogleAuthService {

    private final UserCredentialsRepository userCredentialsRepository;
    private final JwtUtil jwtUtil;

    @Value("${google.client.id}")
    private String googleClientId;

    public GoogleAuthService(UserCredentialsRepository userCredentialsRepository, JwtUtil jwtUtil) {
        this.userCredentialsRepository = userCredentialsRepository;
        this.jwtUtil = jwtUtil;
    }

    // Checks if a user email is already registered in the system so if not return a user not found error and direct the user to register
    public GoogleAuthResponseDTO execute( GoogleAuthRequestDTO googleAuthRequestDTO ) {
        System.out.println( "GoogleAuthService: Verifying Google ID token" );

        try {
            // 1. Build the verifier using my credentials
            GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(
                    new NetHttpTransport(),
                    new GsonFactory()
            ).setAudience(Collections.singletonList(googleClientId))
                    .build();

            // 2. Verify the ID token sent
            GoogleIdToken idToken = verifier.verify( googleAuthRequestDTO.idToken() );

            if ( idToken == null ) {
                throw new GoogleAuthException();
            }

            // 3. Extract the email
            GoogleIdToken.Payload payload = idToken.getPayload();
            String email = payload.getEmail();

            System.out.println( "GoogleAuthService: Token verified for email " + email );

            // 4. Check if user exists, if not create one with GOOGLE provider
            UserCredentials userCredentials = userCredentialsRepository
                    .findByEmail( email )
                    .orElseThrow(UserNotFound::new);

            User jwtUser = new User ( userCredentials.getEmail(), "", List.of() );
            String jwt = jwtUtil.generateToken( jwtUser );
            System.out.println( "GoogleAuthService: JWT issued for: " + email );
            return new GoogleAuthResponseDTO( jwt );

        } catch ( UserNotFound e ) {
            throw e;
        }
        catch ( Exception e ) {
            System.out.println( "GoogleAuthService: Token verification failed - " + e.getMessage() );
            throw new RuntimeException( "Google authentication failed: " + e.getMessage() );
        }
    }

    public GoogleAuthResponseDTO register( GoogleAuthRequestDTO googleAuthRequestDTO ) {
        System.out.println("GoogleAuthService: Registering new Google user" );

        try {
            // 1. Build the verifier using my credentials
            GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(
                    new NetHttpTransport(),
                    new GsonFactory()
            ).setAudience(Collections.singletonList(googleClientId))
                    .build();

            GoogleIdToken idToken = verifier.verify( googleAuthRequestDTO.idToken() );

            if (  idToken == null ) {
                throw new GoogleAuthException();
            }

            GoogleIdToken.Payload payload = idToken.getPayload();
            String email = payload.getEmail();

            System.out.println( "GoogleAuthService: Token verified for email " + email );

            if ( userCredentialsRepository.existsByEmail( email ) ) {
                throw new RuntimeException( "Account already exists. Please sing in." );
            }

            UserCredentials userCredentials = new UserCredentials( email, null, "GOOGLE" );
            userCredentialsRepository.save( userCredentials );

            User jwtUser = new User( userCredentials.getEmail(), "", List.of() );

            String jwt = jwtUtil.generateToken( jwtUser );
            System.out.println( "GoogleAuthService: JWT issued for: " + email );
            return new GoogleAuthResponseDTO( jwt );
        }
        catch( Exception e ) {

            throw new GoogleAuthException();
        }

    }
}
