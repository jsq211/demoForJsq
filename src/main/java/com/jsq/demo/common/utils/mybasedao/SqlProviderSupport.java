package com.jsq.demo.common.utils.mybasedao;

import com.baomidou.mybatisplus.core.metadata.TableInfo;
import org.apache.ibatis.builder.annotation.ProviderContext;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author jsq
 */
abstract class SqlProviderSupport {
    /**
     * 表前缀
     */
    private static final String TABLE_PREFIX = "";
    /**
     * 主键
     */
    private static final String DEFAULT_PRIMARY_KEY = "id";
    /**
     * 映射字段
     */
    private static Map<Class,TableInfo> tableInfoMap = new ConcurrentHashMap<>(256);


    protected TableInfo tableInfo (ProviderContext context){
        TableInfo info = tableInfoMap.get(context.getMapperType());
        if (info != null){
            return info;
        }
        Class
    }
}
