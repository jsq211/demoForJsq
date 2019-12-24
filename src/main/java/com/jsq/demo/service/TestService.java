package com.jsq.demo.service;

import com.alibaba.fastjson.JSON;
import com.jsq.demo.common.utils.SpringUtil;
import com.jsq.demo.dao.TestDAO;
import com.jsq.demo.pojo.po.TestPO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 测试
 * @author jsq
 */
@Service
public class TestService {
    @Autowired
    private TestDAO testDAO;

    public String get() {
        List<String> str = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            String name = i == 2?null:"jsq" + i;
            String ans = SpringUtil.getBean(this.getClass()).sayHi(name,"jsq" + i);
            str.add(ans);
        }
        return JSON.toJSONString(str);
    }

    public String sayHi(String name,String n){
        TestPO testPO = testDAO.findOne("1");
        testPO.setN(null);
        testDAO.updateByIdNotNull(testPO);
        System.out.println(JSON.toJSONString(testPO));
//        testDAO.insert(name,n);
        return "hi " + name + " and " + n;
    }
}
