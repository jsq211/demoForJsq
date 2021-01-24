package com.jsq.component.util;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.api.R;
import com.jsq.component.config.MybatisPlusSyncProps;
import org.apache.ibatis.mapping.ParameterMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * 同步redis
 * @author jsq
 */
public class MybatisSyncComponent {

    @Autowired
    private RedisUtil redisUtil;

    private static Boolean NOT_ALLOWED = null;
    private static List<String> LIMIT_LIST = null;
    private static String PREFIX = null;
    private static boolean getEnabled(){
        if (null == NOT_ALLOWED){
            synchronized (MybatisSyncComponent.class){
                if (null == NOT_ALLOWED){
                    NOT_ALLOWED = !MybatisPlusSyncProps.getInstance().isEnabled();
                }
            }
        }
        return NOT_ALLOWED;
    }

    public void insertList(Object parameter, ParameterMap parameterMap) {
        if (NOT_ALLOWED){
            return;
        }
        Class<?> clazz = parameterMap.getType();
        TableName tableName = clazz.getAnnotation(TableName.class);
        String table = tableName.value();

        if (!CollectionUtils.isEmpty(LIMIT_LIST)&&(LIMIT_LIST.contains(table))){
            
        }

        if (table.startsWith(PREFIX)){

        }


    }
}
