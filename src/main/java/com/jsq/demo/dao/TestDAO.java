package com.jsq.demo.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;


/**
 * 测试数据库连接
 * @author jsq
 */

@Mapper
@Component
public interface TestDAO {

    void insert(@Param("name")String name, @Param("n") String n);
}
