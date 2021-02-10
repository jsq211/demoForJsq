package com.jsq.component.config;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.annotation.TableName;
import com.google.common.collect.Lists;
import com.jsq.component.event.RedisUpdateEvent;
import com.jsq.component.util.BeanUtil;
import com.jsq.component.util.RedisUtil;
import com.jsq.component.util.SpringUtil;
import com.mysql.cj.util.StringUtils;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.ibatis.mapping.ParameterMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 同步redis
 * @author jsq
 */
@Async("redisAsyncTaskExecutor")
@SuppressWarnings("all")
public class MybatisSyncComponent implements ApplicationEventPublisherAware, AsyncConfigurer {

    private static volatile Boolean NOT_ALLOWED = null;
    private static volatile Set<String> TABLE_SET = null;
    private static volatile String PREFIX = null;
    private static final String KEY_FORMAT= "%s:%s:%s";

    private static final Logger logger = LoggerFactory.getLogger(MybatisSyncComponent.class);

    @Autowired
    private RedisUtil redisUtil;
    private ApplicationEventPublisher applicationEventPublisher;

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
        try {
            TableName tableName = clazz.getAnnotation(TableName.class);
            return tableName.value();
        } catch (NullPointerException e) {
            String clazzName = clazz.getSimpleName();
            return castTableName(clazzName);
        }
    }

    private String castTableName(String clazzName) {
        if (StringUtils.isNullOrEmpty(clazzName)){
            return "";
        }
        StringBuilder builder=new StringBuilder(clazzName);
        Pattern p = Pattern.compile("[A-Z]");
        Matcher mc=p.matcher(clazzName);
        int len = 0;
        while(mc.find()){
            if (len == 0) {
                builder.replace(mc.start(), mc.end(), mc.group().toLowerCase());
            } else {
                builder.replace(mc.start(), mc.end(), "_" + mc.group().toLowerCase());
            }
            len++;
        }
        return builder.toString();
    }

    private void setRedisSingle(String database, Object parameter, String tableName) {
        try {
            String id = String.valueOf(PropertyUtils.getProperty(parameter,"id"));
            redisUtil.set(String.format(KEY_FORMAT,database,tableName,id), JSONObject.toJSON(parameter));
        } catch (Exception e) {
            logger.info("sync failed message:{}",e.getMessage());
        }
    }

    private void setRedisList(String database, Object parameter, String tableName) {
        try {
            List<Object> entityList = ((Map<?, List<Object>>) parameter).get("list");
            for (Object object:entityList ) {
                String id = String.valueOf(PropertyUtils.getProperty(object,"id"));
                redisUtil.set(String.format(KEY_FORMAT,database,tableName,id),JSONObject.toJSON(object));
            }
        } catch (Exception e) {
            logger.info("sync failed message:{}",e.getMessage());
        }
    }

    public void updateRedis(String databaseName, Object parameter, ParameterMap parameterMap) {
        if (notAllowed()){
            return;
        }
        String table = getTableName(parameterMap);
        if (!CollectionUtils.isEmpty(tableList())&&(TABLE_SET.contains(table))){
            if (parameter instanceof Map){
                updateRedisList(databaseName,parameter,table);
                return;
            }
            updateRedisSingle(databaseName,parameter,table);
            return;
        }

        if (StringUtils.isNullOrEmpty(getPrefix()) && table.startsWith(getPrefix())){
            if (parameter instanceof Map){
                updateRedisList(databaseName,parameter,table);
                return;
            }
            updateRedisSingle(databaseName,parameter,table);
        }

    }

    private void updateRedisSingle(String databaseName, Object parameter, String tableName) {
        try {
            String id = String.valueOf(PropertyUtils.getProperty(parameter,"id"));
            String redisKey = String.format(KEY_FORMAT,databaseName,tableName,id);
            Object object = redisUtil.getObj(redisKey);
            Boolean isDelete = MybatisPlusSyncProps.getInstance().isLogicDelete(parameter);
            if (isDelete){
                deleteRedisKey(redisKey);
                return;
            }
            if (null ==object){
                applicationEventPublisher.publishEvent(new RedisUpdateEvent(redisKey,databaseName,tableName,Long.valueOf(id)));
                return;
            }
            BeanUtil.copyPropertiesIgnoreNull(parameter,object);
            redisUtil.set(redisKey,JSONObject.toJSON(parameter));
        } catch (Exception e) {
            logger.info("sync failed message:{}",e.getMessage());
        }
    }

    private void updateRedisList(String databaseName, Object parameter, String tableName) {
        try {
            List<Object> entityList = ((Map<?, List<Object>>) parameter).get("list");
            for (Object obj:entityList ) {
                String id = String.valueOf(PropertyUtils.getProperty(obj,"id"));
                String redisKey = String.format(KEY_FORMAT,databaseName,tableName,id);
                Boolean isDelete = MybatisPlusSyncProps.getInstance().isLogicDelete(obj);
                if (isDelete){
                    deleteRedisKey(redisKey);
                    continue;
                }
                Object object = redisUtil.getObj(redisKey);
                if (null == object){
                    applicationEventPublisher.publishEvent(new RedisUpdateEvent(redisKey,databaseName,tableName,Long.valueOf(id)));
                    continue;
                }
                BeanUtil.copyPropertiesIgnoreNull(obj,object);
                redisUtil.set(redisKey,JSONObject.toJSON(object));
            }

        } catch (Exception e) {
            logger.info("sync failed message:{}",e.getMessage());
        }
    }

    private void deleteRedisKey(String redisKey) {
        Object object = redisUtil.getObj(redisKey);
        if (null != object){
            redisUtil.delete(redisKey);
        }
    }

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        this.applicationEventPublisher = applicationEventPublisher;
    }

    @Override
    public Executor getAsyncExecutor() {
        return (ThreadPoolTaskExecutor)SpringUtil.getBean("redisAsyncTaskExecutor");
    }

    public void deleteRedis(String databaseName, Object parameter, String table) {
        if (notAllowed()){
            return;
        }
        if (!CollectionUtils.isEmpty(tableList())&&(TABLE_SET.contains(table))){
            if (parameter instanceof Map){
                deleteRedisList(databaseName,parameter,table);
                return;
            }
            deleteRedisSingle(databaseName,parameter,table);
            return;
        }

        if (StringUtils.isNullOrEmpty(getPrefix()) && table.startsWith(getPrefix())){
            if (parameter instanceof Map){
                deleteRedisList(databaseName,parameter,table);
                return;
            }
            deleteRedisSingle(databaseName,parameter,table);
        }
    }

    private void deleteRedisSingle(String databaseName, Object parameter, String tableName) {
        try {
            String id = String.valueOf(parameter);
            String redisKey = String.format(KEY_FORMAT,databaseName,tableName,id);
            deleteRedisKey(redisKey);
        } catch (Exception e) {
            logger.info("sync failed message:{}",e.getMessage());
        }
    }

    private void deleteRedisList(String databaseName, Object parameter, String tableName) {
        try {
            List<Object> entityList = ((Map<?, List<Object>>) parameter).get("coll");
            List<String> redisKeyList = Lists.newArrayList();
            for (Object obj:entityList ) {
                String id = String.valueOf(obj);
                String redisKey = String.format(KEY_FORMAT,databaseName,tableName,id);
                redisKeyList.add(redisKey);
            }
            redisUtil.delete(redisKeyList);
        } catch (Exception e) {
            logger.info("sync failed message:{}",e.getMessage());
        }
    }
}
