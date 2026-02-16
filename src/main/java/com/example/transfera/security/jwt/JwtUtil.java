package com.example.transfera.security.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.User;

import javax.crypto.SecretKey;
import java.util.Date;

public class JwtUtil {

    public static String generateToken( User user ) {
        return Jwts
                .builder()
                .subject(user.getUsername() )
                .expiration( new Date( System.currentTimeMillis() + 300_000) ) // 5 min
                .signWith( getSigningKey() )
                .compact();
    }

    public static Claims getClaims(String token ) {
        return Jwts
                .parser()
                .verifyWith( getSigningKey() )
                .build()
                .parseSignedClaims( token )
                .getPayload();
    }

    public static boolean isTokenValid( String token ) {
        return !isExpired( token );
    }

    private static boolean isExpired(String token) {
        return getClaims( token ).getExpiration().before( new Date() );
    }

    private static SecretKey getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode( "nX6m5VxQyC0x9mQyK4K1Q0x3Z8s7pWbFq9YcT2uLr8E=" );
        return Keys.hmacShaKeyFor( keyBytes );
    }
}
