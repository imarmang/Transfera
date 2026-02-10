package com.example.transfera;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.Arrays;

@Configuration
@EnableCaching
@EnableScheduling
public class CacheConfiguration {

    @Bean  // gets injected into the Spring Container using dependency injection
    // will talk about this more after the security section
    public CacheManager cacheManager() {
        ConcurrentMapCacheManager cacheManager = new ConcurrentMapCacheManager();
        cacheManager.setAllowNullValues( false );
        cacheManager.setCacheNames( Arrays.asList( "productCache" ) );
        return cacheManager;
    }

    @CacheEvict( value = "productCache", allEntries = true )
    @Scheduled( fixedRate = 7000 )
    public void evictProductCache() {
        System.out.println( "Evicting Product Cache" );
    }
}
