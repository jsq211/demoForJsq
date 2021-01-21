package com.jsq.demo.common.config;

import com.jsq.demo.common.utils.mybasedao.inject.MySqlInjector;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * mybatis配置
 * 批量插入
 * 批量删除
 * @author jsq
 */
@Configuration
public class MybatisPlusConfig {
    @Bean
    public MySqlInjector sqlInjector() {
        return new MySqlInjector();
    }
}
