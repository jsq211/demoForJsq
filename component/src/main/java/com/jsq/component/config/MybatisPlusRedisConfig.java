package com.jsq.component.config;

import com.jsq.component.util.RedisUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;


/**
 * mybatis-redis缓存配置
 * @author jsq
 */
@Configuration
@ConditionalOnProperty(name = "test", havingValue = "true")
public class MybatisPlusRedisConfig {
    @Value("${redis.sync.prefix:DefaultValue}")
    private String value;
    @Value("${redis.sync.table}")
    private String[] tableNames;

    @Bean
    public RedisUtil redisUtil(RedisTemplate<String,Object> redisTemplate){
        return new RedisUtil(redisTemplate);
    }

}
