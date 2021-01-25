package com.jsq.component.util;

import com.baomidou.mybatisplus.annotation.TableName;
import com.jsq.component.config.MybatisPlusSyncProps;
import com.mysql.cj.util.StringUtils;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.ibatis.mapping.ParameterMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 同步redis
 * @author jsq
 */
@Async
public class MybatisSyncComponent {

    @Autowired
    private RedisUtil redisUtil;

    private static volatile Boolean NOT_ALLOWED = null;
    private static volatile Set<String> TABLE_SET = null;
    private static volatile String PREFIX = null;
    private static final String KEY_FORMAT= "%s:%s:%s";

    private static final Logger logger = LoggerFactory.getLogger(MybatisSyncComponent.class);

    private static boolean notAllowed(){
        if (null == NOT_ALLOWED){
            synchronized (MybatisSyncComponent.class){
                if (null == NOT_ALLOWED){
                    NOT_ALLOWED = !MybatisPlusSyncProps.getInstance().isEnabled();
                }
            }
        }
        return NOT_ALLOWED;
    }


    private static Set<String> tableList(){
        if (null == TABLE_SET){
            synchronized (MybatisSyncComponent.class){
                if (null == TABLE_SET){
                    TABLE_SET = MybatisPlusSyncProps.getInstance().getTableList();
                }
            }
        }
        return TABLE_SET;
    }

    private static String getPrefix(){
        if (null == PREFIX){
            synchronized (MybatisSyncComponent.class){
                if (null == PREFIX){
                    PREFIX = MybatisPlusSyncProps.getInstance().getPrefix();
                }
            }
        }
        return PREFIX;
    }
    public void insertRedis(String database, Object parameter, ParameterMap parameterMap) {
        if (notAllowed()){
            return;
        }
        String table = getTableName(parameterMap);

        if (!CollectionUtils.isEmpty(tableList())&&(TABLE_SET.contains(table))){
            if (parameter instanceof Map){
                setRedisList(database,parameter,table);
            }
            setRedisSingle(database,parameter,table);
            return;
        }

        if (StringUtils.isNullOrEmpty(getPrefix()) && table.startsWith(getPrefix())){
            if (parameter instanceof Map){
                setRedisList(database,parameter,table);
            }
            setRedisSingle(database,parameter,table);
        }
    }

    private String getTableName(ParameterMap parameterMap) {
        Class<?> clazz = parameterMap.getType();
        TableName tableName = clazz.getAnnotation(TableName.class);
        return tableName.value();
    }

    private void setRedisSingle(String database, Object parameter, String tableName) {
        try {
            String id = String.valueOf(PropertyUtils.getProperty(parameter,"id"));
            redisUtil.set(String.format(KEY_FORMAT,database,tableName,id),parameter);
        } catch (Exception e) {
            logger.info("sync failed message:{}",e.getMessage());
        }
    }

    private void setRedisList(String database, Object parameter, String tableName) {
        try {
            List<Object> entityList = ((Map<?, List<Object>>) parameter).get("list");
            for (Object object:entityList ) {
                String id = String.valueOf(PropertyUtils.getProperty(object,"id"));
                redisUtil.set(String.format(KEY_FORMAT,database,tableName,id),object);
            }

        } catch (Exception e) {
            logger.info("sync failed message:{}",e.getMessage());
        }
    }

    public void updateRedis(String databaseName, Object parameter, ParameterMap parameterMap) {
        if (notAllowed()){
            return;
        }
        Class<?> clazz = parameterMap.getType();
        TableName tableName = clazz.getAnnotation(TableName.class);
        String table = tableName.value();
        if (!CollectionUtils.isEmpty(tableList())&&(TABLE_SET.contains(table))){
            if (parameter instanceof Map){
                updateRedisList(databaseName,parameter,table,clazz);
            }
            updateRedisSingle(databaseName,parameter,table,clazz);
            return;
        }

        if (StringUtils.isNullOrEmpty(getPrefix()) && table.startsWith(getPrefix())){
            if (parameter instanceof Map){
                updateRedisList(databaseName,parameter,table,clazz);
            }
            updateRedisSingle(databaseName,parameter,table,clazz);
        }

    }

    private void updateRedisSingle(String databaseName, Object parameter, String tableName,Class<?> clazz) {
        try {
            String id = String.valueOf(PropertyUtils.getProperty(parameter,"id"));
            String redisKey = String.format(KEY_FORMAT,databaseName,tableName,id);
            Object object = redisUtil.getObj(redisKey);
            BeanUtil.copyProperties(clazz.newInstance(),object);
            redisUtil.set(redisKey,object);
        } catch (Exception e) {
            logger.info("sync failed message:{}",e.getMessage());
        }
    }

    private void updateRedisList(String databaseName, Object parameter, String table,Class<?> clazz) {

    }
}
