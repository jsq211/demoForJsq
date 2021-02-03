package com.jsq.component.config;

import com.jsq.component.event.listener.impl.RedisUpdateSyncListener;
import com.jsq.component.interceptor.MybatisSyncInterceptor;
import com.jsq.component.util.MybatisSyncComponent;
import com.jsq.component.util.RedisUtil;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolExecutorFactoryBean;


/**
 * mybatis-redis缓存配置
 * @author jsq
 */
@Configuration
@EnableAsync
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
    @ConditionalOnBean(RedisUtil.class)
    public MybatisSyncComponent mybatisSyncComponent(){
        return new MybatisSyncComponent();
    }

    @Bean
    @ConditionalOnBean(MybatisSyncComponent.class)
    public RedisUpdateSyncListener redisUpdateSyncListener(){
        return new RedisUpdateSyncListener();
    }

    @Bean
    public ThreadPoolExecutorFactoryBean redisAsyncTaskExecutor() {
        ThreadPoolExecutorFactoryBean factory = new ThreadPoolExecutorFactoryBean();
        factory.setThreadNamePrefix("redis-async-");
        factory.setCorePoolSize(2);
        factory.setMaxPoolSize(4);
        factory.initialize();
        return factory;
    }
}
