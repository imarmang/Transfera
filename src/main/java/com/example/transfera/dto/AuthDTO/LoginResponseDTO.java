package com.example.transfera.dto.AuthDTO;


// Record is immutable, and I want the LoginResponse and LogoutResponse to stay immutable
public record LoginResponseDTO( String token ) {}

