package com.jsq.component.config;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.jsq.component.mybasedao.inject.MySqlInjector;
import lombok.Data;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.regex.Pattern;

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
