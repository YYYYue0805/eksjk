package com.eksjk.web.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

/**
 * Caffeine 内存缓存配置
 *
 * <p>使用 Caffeine 作为 JVM 内存缓存，替代 Redis。
 * 适用于单实例部署场景，无需外部中间件依赖。</p>
 *
 * @author eksjk
 * @since 2.0.0
 */
@Configuration
@EnableCaching
public class CacheConfig {

    /** 数据字典缓存（1 小时过期，最多 500 条） */
    public static final String CACHE_DICT = "dict";

    /** 医院列表缓存（30 分钟过期，最多 200 条） */
    public static final String CACHE_HOSPITAL = "hospital";

    /** 用户信息缓存（15 分钟过期，最多 1000 条） */
    public static final String CACHE_USER = "user";

    /** 通用短期缓存（5 分钟过期，最多 2000 条） */
    public static final String CACHE_DEFAULT = "default";

    @Bean
    public CacheManager cacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager();
        // 默认缓存策略：最多 1000 条，写入后 10 分钟过期
        cacheManager.setCaffeine(Caffeine.newBuilder()
                .maximumSize(1000)
                .expireAfterWrite(10, TimeUnit.MINUTES)
                .recordStats());
        // 注册已知缓存名称
        cacheManager.setCacheNames(java.util.List.of(
                CACHE_DICT, CACHE_HOSPITAL, CACHE_USER, CACHE_DEFAULT));
        // 允许动态创建未注册的缓存
        cacheManager.setAllowNullValues(false);
        return cacheManager;
    }
}
