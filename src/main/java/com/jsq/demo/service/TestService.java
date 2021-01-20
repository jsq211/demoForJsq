package com.jsq.demo.service;

import com.google.common.collect.Lists;
import com.jsq.demo.dao.TestMapper;
import com.jsq.demo.pojo.TestPO;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.UUID;

/**
 * 测试
 * @author jsq
 */
@Service
public class TestService {
    @Resource
    private TestMapper testMapper;

    public Integer testBatchInsert() {
        List<TestPO> testPOList = Lists.newArrayList();
        for (int i = 0; i <3; i++) {
            testPOList.add(createPO(i));
        }
        return testMapper.batchInsert(testPOList);
    }

    private TestPO createPO(int i) {
        TestPO testPO = new TestPO();
        testPO.setN(String.valueOf(i));
        testPO.setName(UUID.randomUUID().toString());
        return testPO;
    }

    public Integer testInsert() {
        return testMapper.insert(createPO(1));

    }
}
