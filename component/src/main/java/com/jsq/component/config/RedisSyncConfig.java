package com.jsq.component.config;

import com.jsq.component.factory.RedisFactory;
import com.jsq.component.util.SpringUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisPassword;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.StringUtils;

/**
 * redis配置加载
 * @author jsq
 */
@Configuration
@ConditionalOnProperty(prefix = "jsq.sync",name = "enabled",havingValue = "true")
public class RedisSyncConfig {
    @Value("${jsq.sync.database:7}")
    private String database;
    @Bean
    @ConfigurationProperties(
            prefix = "spring.redis"
    )
    public RedisSyncProperties redisSyncProperties(){
        return new RedisSyncProperties();
    }

    @Bean
    public SpringUtil springUtil(){
        return new SpringUtil();
    }
    @Bean("redisSyncConnectionFactory")
    @ConditionalOnBean(RedisSyncProperties.class)
    public LettuceConnectionFactory redisSyncConnectionFactory(RedisSyncProperties redisSyncProperties){
        RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration();
        redisStandaloneConfiguration.setDatabase(Integer.valueOf(database));
        redisStandaloneConfiguration.setHostName(redisSyncProperties.getHost());
        redisStandaloneConfiguration.setPort(redisSyncProperties.getPort());
        if (!StringUtils.isEmpty(redisStandaloneConfiguration.getPassword())){
            redisStandaloneConfiguration.setPassword(RedisPassword.of(redisSyncProperties.getPassword()));
        }
        LettuceClientConfiguration.LettuceClientConfigurationBuilder lettuceClientConfigurationBuilder = LettuceClientConfiguration.builder();
        LettuceConnectionFactory factory = new LettuceConnectionFactory(redisStandaloneConfiguration,
                lettuceClientConfigurationBuilder.build());
        return factory;
    }

    @Bean(name = "redisSyncTemplate")
    public RedisTemplate<String, Object> redisSyncTemplate(LettuceConnectionFactory lettuceConnectionFactory){
        return RedisFactory.createTemplate(lettuceConnectionFactory);
    }

}
