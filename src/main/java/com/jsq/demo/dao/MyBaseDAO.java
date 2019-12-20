package com.jsq.demo.dao;


import com.jsq.demo.common.utils.mybasedao.MyBaseSqlProvider;
import org.apache.ibatis.annotations.SelectProvider;

/**
 * 数据库通用字段
 * @param <T>
 */
public interface MyBaseDAO<T> {
    @SelectProvider(type = MyBaseSqlProvider.class,method = "sql")
    <T> findOne(Object id);
}
