package com.jsq.component;

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
     */
    Integer batchInsert(@Param("list") List<T> list);

    /**
     * 自定义批量更新，条件为主键
     */
    Integer batchUpdate(@Param("list") List<T> list);
}
