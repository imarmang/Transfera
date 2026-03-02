// TODO: THE TOKENS BLACKLIST SET IS TEMPORARY AND IS ONLY USED FOR THE DEVELOPMENT

package com.example.transfera.service.jwt;

import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class TokenBlacklistService {

    private final Set<String> blacklistedTokens = ConcurrentHashMap.newKeySet();

    // BLACK LIST THE TOKEN
//    public void blacklistToken( String token ) {
//        blacklistedTokens.add( token );
//    }

    public void blacklistToken( String token ) {
        boolean added = blacklistedTokens.add(token);

        System.out.println("=== BLACKLIST ADD ===");
        System.out.println("Added? " + added);
        System.out.println("Blacklist size: " + blacklistedTokens.size());
        System.out.println("Token prefix: " + token.substring(0, Math.min(20, token.length())));
    }

    // CHECK IF THE TOKEN IS IN THE SET
    public boolean isBlacklisted( String token ) {
        return blacklistedTokens.contains( token );
    }
}
