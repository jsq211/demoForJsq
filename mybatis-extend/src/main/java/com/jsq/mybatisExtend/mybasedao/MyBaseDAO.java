package com.jsq.mybatisExtend.mybasedao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 批量新增更新
 * @author jsq
 */
public interface MyBaseDAO<T> extends BaseMapper<T> {
    /**
     * 自定义批量插入
     * @param list 集合
     * @return 总条数
     */
    Integer batchInsert(@Param("list") List<T> list);

    /**
     * 自定义批量更新，忽略null值 条件为主键
     * @param list 集合
     * @return always 1 （mybatis解析问题
     */
    Integer batchUpdateIgnoreNull(@Param("list") List<T> list);

    /**
     * 自定义批量更新，包含null值 条件为主键
     * @param list 集合
     * @return always 1 （mybatis解析问题
     */
    Integer batchUpdate(@Param("list") List<T> list);
}
