package com.example.transfera.security.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtUtil {

    private final String secretKey;


    public JwtUtil( @Value( "${jwt.secret}") String secretKey ) {
        this.secretKey = secretKey;
    }



    public String generateToken( User user ) {
        return Jwts
                .builder()
                .subject(user.getUsername() )
                .expiration( new Date( System.currentTimeMillis() + 300_000) ) // 5 min
                .signWith( getSigningKey() )
                .compact();
    }

    public Claims getClaims( String token ) {
        return Jwts
                .parser()
                .verifyWith( getSigningKey() )
                .build()
                .parseSignedClaims( token )
                .getPayload();
    }

    public boolean isTokenValid( String token ) {
        return !isExpired( token );
    }

    private boolean isExpired( String token) {
        return getClaims( token ).getExpiration().before( new Date() );
    }

    private SecretKey getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode( secretKey );
        return Keys.hmacShaKeyFor( keyBytes );
    }
}
