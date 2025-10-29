package com.jono.core.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
@EnableCaching
public class CaffeineConfig {

    @Bean
    public CacheManager cacheManager() {
        final var manager = new CaffeineCacheManager();
        manager.setAllowNullValues(false);
        manager.setAsyncCacheMode(true);
        manager.setCaffeine(Caffeine.newBuilder()
                                    .maximumSize(5_000)
                                    .expireAfterAccess(Duration.ofMinutes(10))
                                    .recordStats());

        manager.registerCustomCache(CacheName.CLIENT.getCacheName(), Caffeine.newBuilder()
                                                                             .maximumSize(100)
                                                                             .expireAfterAccess(Duration.ofMinutes(1))
                                                                             .recordStats()
                                                                             .build());
        manager.registerCustomCache(CacheName.ENTITY_CONSTANTS.getCacheName(), Caffeine.newBuilder()
                                                                                       .maximumSize(5_000)
                                                                                       .expireAfterAccess(Duration.ofHours(1))
                                                                                       .recordStats()
                                                                                       .build());

        return manager;
    }

}
