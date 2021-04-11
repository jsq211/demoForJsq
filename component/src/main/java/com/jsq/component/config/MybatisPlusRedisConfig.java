package com.jsq.component.config;

import com.jsq.component.event.listener.impl.RedisInsertSyncListener;
import com.jsq.component.event.listener.impl.RedisUpdateSyncListener;
import com.jsq.component.interceptor.MybatisSyncInterceptor;
import com.jsq.component.manager.RedisCacheManualManager;
import com.jsq.component.manager.RedisCacheSyncManager;
import com.jsq.component.util.RedisUtil;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.event.ApplicationEventMulticaster;
import org.springframework.context.event.SimpleApplicationEventMulticaster;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.RejectedExecutionHandler;


/**
 * mybatis-redis缓存配置
 * @author jsq
 */
@Configuration
@SuppressWarnings("all")
@AutoConfigureAfter(value = {RedisSyncConfig.class,DatabaseConfig.class})
public class MybatisPlusRedisConfig {
    @Bean("redisSyncUtil")
    public RedisUtil redisUtil(@Qualifier(value = "redisSyncTemplate") RedisTemplate<String,Object> redisTemplate){
        return new RedisUtil(redisTemplate);
    }

    @Bean
    @ConditionalOnBean(name ="redisSyncUtil")
    public MybatisSyncComponent mybatisSyncComponent(RedisUtil redisUtil,MybatisPlusSyncProperties mybatisPlusSyncProperties){
        return new MybatisSyncComponent(redisUtil, mybatisPlusSyncProperties);
    }

    @Bean
    @ConditionalOnBean(MybatisSyncComponent.class)
    public RedisUpdateSyncListener redisUpdateSyncListener(JdbcTemplate jdbcTemplate, MybatisPlusSyncProperties mybatisPlusSyncProperties){
        return new RedisUpdateSyncListener(jdbcTemplate, mybatisPlusSyncProperties);
    }
    @Bean
    @ConditionalOnBean(MybatisSyncComponent.class)
    public RedisInsertSyncListener redisInsertSyncListener(JdbcTemplate jdbcTemplate, MybatisPlusSyncProperties mybatisPlusSyncProperties){
        return new RedisInsertSyncListener(jdbcTemplate,mybatisPlusSyncProperties);
    }
    @Bean("redisAsyncTaskExecutor")
    @ConditionalOnBean(RedisUpdateSyncListener.class)
    public ThreadPoolTaskExecutor redisAsyncTaskExecutor() {
        ThreadPoolTaskExecutor factory = new ThreadPoolTaskExecutor();
        factory.setThreadNamePrefix("redis-async-");
        factory.setCorePoolSize(3);
        factory.setMaxPoolSize(4);
        factory.initialize();
        return factory;
    }

    @Bean
    @ConditionalOnBean(name = "redisAsyncTaskExecutor")
    public ApplicationEventMulticaster applicationEventMulticaster(){
        SimpleApplicationEventMulticaster eventMulticaster = new SimpleApplicationEventMulticaster();
        eventMulticaster.setTaskExecutor(this.redisAsyncTaskExecutor());
        return eventMulticaster;
    }

    @Bean
    @DependsOn("mybatisSyncComponent")
    public RedisCacheSyncManager redisSyncManager(){
        return new RedisCacheSyncManager();
    }

    @Bean
    @ConditionalOnBean(value = {MybatisSyncComponent.class,DatabaseConfig.class})
    public MybatisSyncInterceptor mybatisSyncInterceptor(MybatisSyncComponent mybatisSyncComponent, DatabaseConfig databaseConfig){
        return new MybatisSyncInterceptor(mybatisSyncComponent, databaseConfig);
    }
    @Bean
    @ConditionalOnBean(RedisCacheSyncManager.class)
    public RedisCacheManualManager redisCacheManualManager(JdbcTemplate jdbcTemplate){
        return new RedisCacheManualManager(jdbcTemplate);
    }

}
