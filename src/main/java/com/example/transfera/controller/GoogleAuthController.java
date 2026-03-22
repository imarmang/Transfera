package com.example.transfera.controller;

import com.example.transfera.dto.AuthDTO.google.GoogleAuthRequestDTO;
import com.example.transfera.dto.AuthDTO.google.GoogleAuthResponseDTO;
import com.example.transfera.service.google.GoogleAuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/auth2")
@RestController
public class GoogleAuthController {
    private final GoogleAuthService googleAuthService;
    public GoogleAuthController( GoogleAuthService googleAuthService ) {
        this.googleAuthService = googleAuthService;
    }

    @PostMapping("/google")
    public ResponseEntity<GoogleAuthResponseDTO> googleLogin( @RequestBody GoogleAuthRequestDTO googleAuthRequestDTO ) {
        System.out.println( "GoogleAuthController: googleLogin: Received google login request" );
        GoogleAuthResponseDTO response = googleAuthService.execute( googleAuthRequestDTO );
        return ResponseEntity.ok( response );
    }

    @PostMapping("/google/register")
    public ResponseEntity<GoogleAuthResponseDTO> googleRegister( @RequestBody GoogleAuthRequestDTO googleAuthRequestDTO ) {
        System.out.println( "GoogleAuthController: googleRegister: Received google register request" );
        GoogleAuthResponseDTO response = googleAuthService.register( googleAuthRequestDTO );
        return ResponseEntity.ok( response );
    }
}
