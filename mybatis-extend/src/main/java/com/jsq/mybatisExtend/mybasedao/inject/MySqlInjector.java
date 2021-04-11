package com.jsq.mybatisExtend.mybasedao.inject;

import com.baomidou.mybatisplus.core.injector.AbstractMethod;
import com.baomidou.mybatisplus.core.injector.AbstractSqlInjector;
import com.baomidou.mybatisplus.core.injector.methods.*;
import com.jsq.mybatisExtend.mybasedao.method.BatchInsert;
import com.jsq.mybatisExtend.mybasedao.method.BatchUpdate;
import com.jsq.mybatisExtend.mybasedao.method.BatchUpdateIgnoreNull;

import java.util.List;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

/**
 * sql注入
 * @author jsq
 */
public class MySqlInjector extends AbstractSqlInjector {
    @Override
    public List<AbstractMethod> getMethodList(Class<?> mapperClass) {
        return Stream.of(
                new Insert(),
                new Delete(),
                new DeleteByMap(),
                new DeleteById(),
                new DeleteBatchByIds(),
                new Update(),
                new UpdateById(),
                new SelectById(),
                new SelectBatchByIds(),
                new SelectByMap(),
                new SelectOne(),
                new SelectCount(),
                new SelectMaps(),
                new SelectMapsPage(),
                new SelectObjs(),
                new SelectList(),
                new SelectPage(),
                new BatchInsert(),
                new BatchUpdateIgnoreNull(),
                new BatchUpdate()
        ).collect(toList());
    }

}
