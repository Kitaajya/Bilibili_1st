package org.designer.bilibili_1st.config;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Configuration
@EnableCaching

public class Config {
    @Bean
    public CacheManager cacheManager() {
        CaffeineCacheManager cacheManager=new CaffeineCacheManager();
        //10秒内若再查记录直接从缓存里找，不再拷打数据库，缓存最多存1000条
        cacheManager.setCaffeine(Caffeine.newBuilder().maximumSize(1000).expireAfterAccess(10,TimeUnit.SECONDS));
        return cacheManager;
    }
}
