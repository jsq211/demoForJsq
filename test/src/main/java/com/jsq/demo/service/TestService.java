package com.jsq.demo.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.jsq.component.manager.RedisCacheManualManager;
import com.jsq.component.manager.RedisCacheSyncManager;
import com.jsq.demo.dao.TestMapper;
import com.jsq.demo.pojo.TestPO;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;

/**
 * 测试
 * @author jsq
 */
@Service
public class TestService {
    @Resource
    private TestMapper testMapper;
    private final RedisCacheSyncManager redisCacheSyncManager;
    private final RedisCacheManualManager redisCacheManualManager;

    public TestService(RedisCacheSyncManager redisCacheSyncManager, RedisCacheManualManager redisCacheManualManager) {
        this.redisCacheSyncManager = redisCacheSyncManager;
        this.redisCacheManualManager = redisCacheManualManager;
    }

    public Integer testBatchInsert() {
        List<TestPO> testPOList = Lists.newArrayList();
        for (int i = 0; i <3; i++) {
            testPOList.add(createPO(i));
        }
        return testMapper.batchInsert(testPOList);
    }

    private TestPO createPO(int i) {
        TestPO testPO = new TestPO();
//        testPO.setN(String.valueOf(i));
//        testPO.setName(UUID.randomUUID().toString());
        return testPO;
    }

    public Integer testInsert() {
        return testMapper.insert(createPO(1));

    }

    public Integer testBatchUpdate() {

        List<TestPO> testPOList = createPOUpdate(Lists.newArrayList(1L,2L,3L));
        return testMapper.batchUpdateIgnoreNull(testPOList);
    }

    private List<TestPO> createPOUpdate(List<Long> list) {
        List<TestPO> testPOList = Lists.newArrayList();
        list.forEach(e->{
            TestPO po = new TestPO();
//            po.setEnabled(false);
//            po.setId(e);
            testPOList.add(po);
        });
        return testPOList;

    }

    public Integer testBatchUpdateAll() {
        List<TestPO> testPOList = createPOUpdate(Lists.newArrayList(1L,2L,3L));
        return testMapper.batchUpdate(testPOList);
    }

    public List<TestPO> cacheTest() {
        List<TestPO> testPOList = createPOUpdate(Lists.newArrayList(1L,2L,3L));
        redisCacheSyncManager.init(testPOList);
        return testPOList;
    }

    public void testSync() {
        redisCacheManualManager.manualAll(Sets.newHashSet("test"));
    }

    public Integer testBatchDelete() {
        return testMapper.deleteBatchIds(Lists.newArrayList(1,2,3));
    }

    public Integer testSingleDelete() {
        return testMapper.deleteById(10L);
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
