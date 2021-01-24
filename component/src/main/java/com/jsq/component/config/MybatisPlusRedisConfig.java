package com.jsq.component.config;

import com.jsq.component.interceptor.MybatisSyncInterceptor;
import com.jsq.component.util.MybatisSyncComponent;
import com.jsq.component.util.RedisUtil;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;


/**
 * mybatis-redis缓存配置
 * @author jsq
 */
@Configuration
public class MybatisPlusRedisConfig {

    @Bean
    public RedisUtil redisUtil(RedisTemplate<String,Object> redisTemplate){
        return new RedisUtil(redisTemplate);
    }

    @Bean
    @ConditionalOnBean(RedisUtil.class)
    public MybatisSyncInterceptor mybatisSyncInterceptor(){
        return new MybatisSyncInterceptor();
    }
    @Bean
    public MybatisSyncComponent mybatisSyncComponent(){
        return new MybatisSyncComponent();
    }

}
