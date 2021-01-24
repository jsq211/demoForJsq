package com.jsq.demo.dao;

import com.jsq.component.MyBaseDAO;
import com.jsq.demo.pojo.TestPO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

/**
 * @author jsq
 */
@Mapper
@Component
public interface TestMapper extends MyBaseDAO<TestPO> {

    Integer insert(@Param("name") String name, @Param("n") String n);
}
