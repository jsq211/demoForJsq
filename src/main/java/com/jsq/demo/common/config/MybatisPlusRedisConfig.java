package com.jsq.demo.common.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


/**
 * mybatis-redis缓存配置
 * @author jsq
 */
@Configuration
@ConditionalOnProperty(name = "test", havingValue = "true")
public class MybatisPlusRedisConfig {
    @Value("${test.value:DefaultValue}")
    private String value;

}
