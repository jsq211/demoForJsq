package com.jsq.demo.service;

import com.alibaba.fastjson.JSON;
import com.jsq.demo.common.TransactionalRollbackConstants;
import com.jsq.demo.common.annotation.TransactionalRollback;
import com.jsq.demo.dao.TestDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    @TransactionalRollback(transactionDefinition = TransactionalRollbackConstants.PROPAGATION_REQUIRES_NEW)
    public String get() {
        List<String> str = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            String name = i == 2?null:"jsq" + i;
            String ans = sayHi(name,"jsq" + i);
            str.add(ans);
        }
        return JSON.toJSONString(str);
    }

    @TransactionalRollback(transactionDefinition = TransactionalRollbackConstants.PROPAGATION_REQUIRES_NEW)
    public String sayHi(String name,String n){
        testDAO.insert(name,n);
        return "hi " + name + " and " + n;
    }
}
