package com.jsq.mybatisExtend.config;

import com.jsq.mybatisExtend.mybasedao.inject.MySqlInjector;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * mybatis配置
 * 批量插入
 * 批量更新
 * @author jsq
 */
@Configuration
public class MybatisExtendPlusConfig {
    @Bean
    public MySqlInjector mybatisBatchInjector() {
        return new MySqlInjector();
    }

}
