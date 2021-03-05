package com.jsq.component.config;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.jsq.component.mybasedao.inject.MySqlInjector;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
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
    public MySqlInjector sqlInjector() {
        return new MySqlInjector();
    }


    static String json = "{\n" +
            "\"param\":{\n" +
            "       \"must\":[\"test\",\"user\"],\n" +
            "       \"or\":[\"a\",\"b\"]\n" +
            "   }\n" +
            "}";
    public static void main(String[] args) throws Exception{
        ExpressionParser parser = new SpelExpressionParser();
        Expression integerExpression = parser.parseExpression("100 * 2 + 400 * 1 + 66");
//        String str = "test";
//        String s = StringUtils.isEmpty(str)?null:str;
//        Expression booleanExpression = parser.parseExpression("('"+ s + "' != null)");
//        Expression existExpression = parser.parseExpression("(1 between {1,2})");
//        Expression checkExpression = parser.parseExpression("(1 between {1,2} and !true)");
        int integerResult = integerExpression.getValue(Integer.class);
        System.out.println(integerResult);
//        Boolean booleanResult = (Boolean) booleanExpression.getValue();
//        System.out.println(booleanResult);
//        System.out.println(existExpression.getValue());
//        System.out.println(checkExpression.getValue());
//
//
        StandardEvaluationContext context = new StandardEvaluationContext();
//        Method parseInt = Integer.class.getDeclaredMethod("parseInt", String.class);
        Method match = Pattern.class.getDeclaredMethod("matches",String.class,CharSequence.class);
        String matchStr = "#matchStr('%s','%s')";
        context.registerFunction("matchStr", match);
        System.out.println(parser.parseExpression(String.format(matchStr,"test","test")).getValue(context,Boolean.class));
        System.out.println(parser.parseExpression("99999 > 5").getValue());
//        String paresStr1 = "#parseInt1('%s')";
//        String paresStr2 = "#parseInt2('%s')";
//        String equals = "%s == %s";
//        context.registerFunction("parseInt1", parseInt);
//        context.setVariable("parseInt2", parseInt);
//
//        System.out.println(parser.parseExpression(String.format(paresStr1,"3")).getValue(context, int.class));
//        System.out.println(parser.parseExpression("#parseInt2('3')").getValue(context, int.class));
//
//        String expression1 = String.format(equals,String.format(paresStr1,3),String.format(paresStr2,3));
//        boolean result1 = parser.parseExpression(expression1).getValue(context, boolean.class);
//        System.out.println(result1);
        JSONObject jsonObject = JSON.parseObject(json);
        System.out.println(jsonObject);

        User user = new User();
        user.setAge(25);
        user.setAmount(BigDecimal.valueOf(25));
        user.setName("test");
        String parseAge = "%s > 20";
        String parseAmount = "%s > 15.44";
        Boolean userCheck = (Boolean) parser.parseExpression(String.format(parseAge,user.getAge())).getValue();
        Boolean amountCheck = (Boolean) parser.parseExpression(String.format(parseAmount,user.getAmount())).getValue();
        System.out.println(userCheck);
        System.out.println(amountCheck);
        System.out.println(userCheck&&amountCheck);

    }
    @Data
    static class User{
        private String name;
        private Integer age;
        private BigDecimal amount;
    }

}
