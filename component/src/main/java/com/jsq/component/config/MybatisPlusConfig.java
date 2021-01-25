package com.jsq.component.config;


import com.jsq.component.mybasedao.inject.MySqlInjector;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * mybatis配置
 * 批量插入
 * 批量更新
 * @author jsq
 */
@Configuration
public class MybatisPlusConfig {
    @Bean
    public MySqlInjector sqlInjector() {
        return new MySqlInjector();
    }
}
