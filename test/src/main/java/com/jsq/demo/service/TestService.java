package com.jsq.demo.service;

import com.google.common.collect.Lists;
import com.jsq.component.config.MybatisPlusSyncProps;
import com.jsq.component.util.RedisUtil;
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
    @Resource
    private MybatisPlusSyncProps mybatisPlusSyncProps;
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

    public Integer testBatchUpdate() {

        List<TestPO> testPOList = createPOUpdate(Lists.newArrayList(1L,2L,3L));
        return testMapper.batchUpdate(testPOList);
    }

    private List<TestPO> createPOUpdate(List<Long> list) {
        List<TestPO> testPOList = Lists.newArrayList();
        list.forEach(e->{
            TestPO po = new TestPO();
            po.setEnabled(false);
            po.setId(e);
            testPOList.add(po);
        });
        return testPOList;

    }
}