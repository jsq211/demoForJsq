package com.jsq.demo.controller;

import com.alibaba.fastjson.JSON;
import com.jsq.demo.common.TransactionalRollbackConstants;
import com.jsq.demo.common.annotation.LoggerAnnotation;
import com.jsq.demo.common.annotation.TransactionalRollback;
import com.jsq.demo.dao.TestDAO;
import com.jsq.demo.pojo.dto.ThreadPoolParamDTO;
import com.jsq.demo.service.TestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.jsq.demo.manager.ThreadManager;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;


/**
 * 方法测试类
 * @author jsq
 */
@RestController
@RequestMapping("/test")
public class TestController {
    @Autowired
    private TestService testService;
    @Autowired
    private TestDAO testDAO;

    @LoggerAnnotation
    @GetMapping("/hello")
    public String sayHello(String name,String n){
        if (StringUtils.isEmpty(n)){
            throw new RuntimeException("名称不能为空！！");
        }
        return "hello，" + name + " and " + n;
    }

    @GetMapping("/hi")
//    @TransactionalRollback(transactionDefinition = TransactionalRollbackConstants.PROPAGATION_REQUIRES_NEW)
    public String sayHi(){
        return testService.sayHi("jsq","test");
    }

    public String get() {
        List<String> str = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            String name = i == 2?null:"jsq" + i;
            String ans = sayHi(name,"jsq" + i);
            str.add(ans);
        }
        return JSON.toJSONString(str);
    }

    public String sayHi(String name,String n){
        testDAO.insert(name,n);
        return "hi " + name + " and " + n;
    }
    @GetMapping("/test")
    public String testForTest(){
        ThreadPoolParamDTO threadPoolParamDTO = new ThreadPoolParamDTO();

        LinkedList<ThreadPoolParamDTO.ThreadParam> threadParamLinkedList = new LinkedList<>();
        ThreadPoolParamDTO.ThreadParam threadParamA = new ThreadPoolParamDTO.ThreadParam();
        threadParamA.setClassName(TestController.class);
        try {
            threadParamA.setMethod(TestController.class.getDeclaredMethod("sayHello", String.class,String.class));
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        threadParamA.setRequestParam(new Object[]{"jsq","testasdas"});
        threadParamLinkedList.add(threadParamA);
        ThreadPoolParamDTO.ThreadParam threadParamB = new ThreadPoolParamDTO.ThreadParam();
        threadParamB.setClassName(TestController.class);
        try {
            threadParamB.setMethod(TestController.class.getDeclaredMethod("sayHi", String.class,String.class));
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        threadParamB.setRequestParam(new Object[]{"asd","sadasd"});
        threadParamLinkedList.addLast(threadParamB);
        threadPoolParamDTO.setParamList(threadParamLinkedList);
        Map<String, Object> result = ThreadManager.getThreadResult(threadPoolParamDTO);
        String a = result.get("sayHello").toString();
        String b = result.get("sayHi").toString();
        List<String> re = new ArrayList<>();
        re.add(a);
        re.add(b);
        return JSON.toJSONString(re);
    }


}
