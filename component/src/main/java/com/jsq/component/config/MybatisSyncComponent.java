package com.jsq.component.config;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.annotation.TableName;
import com.google.common.collect.Lists;
import com.jsq.component.event.RedisInsertEvent;
import com.jsq.component.event.RedisUpdateEvent;
import com.jsq.component.util.BeanUtil;
import com.jsq.component.util.RedisUtil;
import com.jsq.component.util.SpringUtil;
import com.mysql.cj.util.StringUtils;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.ibatis.mapping.ParameterMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.List;
import java.util.Map;
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

    private static final String KEY_FORMAT= "%s:%s";
    private static final Logger logger = LoggerFactory.getLogger(MybatisSyncComponent.class);
    private final RedisUtil redisUtil;
    private final MybatisPlusSyncProperties mybatisPlusSyncProperties;
    private ApplicationEventPublisher applicationEventPublisher;

    public MybatisSyncComponent(RedisUtil redisUtil, MybatisPlusSyncProperties mybatisPlusSyncProperties) {
        this.redisUtil = redisUtil;
        this.mybatisPlusSyncProperties = mybatisPlusSyncProperties;
    }

    private boolean notAllowed(){
        return !mybatisPlusSyncProperties.getEnabled();
    }

    public void insertRedis(Object parameter, ParameterMap parameterMap) {
        if (notAllowed()){
            return;
        }
        String table = getTableName(parameterMap);
        if (mybatisPlusSyncProperties.containTable(table)){
            if (parameter instanceof Map){
                setRedisList(parameter,table);
            }
            setRedisSingle(parameter,table);
            return;
        }
    }

    /**
     * 获取表名
     * @param parameterMap
     * @return tableName
     */
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

    private void setRedisSingle(Object parameter, String tableName) {
        try {
            String id = String.valueOf(PropertyUtils.getProperty(parameter,"id"));
            applicationEventPublisher.publishEvent(new RedisInsertEvent(tableName,id));
        } catch (Exception e) {
            logger.info("sync failed message:{}",e.getMessage());
        }
    }

    private void setRedisList(Object parameter, String tableName) {
        try {
            List<Object> entityList = ((Map<?, List<Object>>) parameter).get("list");
            for (Object object:entityList ) {
                String id = String.valueOf(PropertyUtils.getProperty(object,"id"));
                redisUtil.set(String.format(KEY_FORMAT,tableName,id),JSONObject.toJSON(object));
            }
        } catch (Exception e) {
            logger.info("sync failed message:{}",e.getMessage());
        }
    }

    public void updateRedis(Object parameter, ParameterMap parameterMap) {
        if (notAllowed()){
            return;
        }
        String table = getTableName(parameterMap);
        if (mybatisPlusSyncProperties.containTable(table)){
            if (parameter instanceof Map){
                updateRedisList(parameter,table);
                return;
            }
            updateRedisSingle(parameter,table);
            return;
        }
    }

    private void updateRedisSingle(Object parameter, String tableName) {
        try {
            String id = String.valueOf(PropertyUtils.getProperty(parameter,"id"));
            String redisKey = String.format(KEY_FORMAT,tableName,id);
            Object object = redisUtil.getObj(redisKey);
            Boolean isDelete = mybatisPlusSyncProperties.isLogicDelete(parameter);
            if (isDelete){
                deleteRedisKey(redisKey);
                return;
            }
            if (null ==object){
                applicationEventPublisher.publishEvent(new RedisUpdateEvent(redisKey,tableName,Long.valueOf(id)));
                return;
            }
            BeanUtil.copyPropertiesIgnoreNull(parameter,object);
            redisUtil.set(redisKey,JSONObject.toJSON(parameter));
        } catch (Exception e) {
            logger.info("sync failed message:{}",e.getMessage());
        }
    }

    private void updateRedisList(Object parameter, String tableName) {
        try {
            List<Object> entityList = ((Map<?, List<Object>>) parameter).get("list");
            for (Object obj:entityList ) {
                String id = String.valueOf(PropertyUtils.getProperty(obj,"id"));
                String redisKey = String.format(KEY_FORMAT,tableName,id);
                Boolean isDelete = mybatisPlusSyncProperties.isLogicDelete(obj);
                if (isDelete){
                    deleteRedisKey(redisKey);
                    continue;
                }
                applicationEventPublisher.publishEvent(new RedisUpdateEvent(redisKey,tableName,Long.valueOf(id)));
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

    public void deleteRedis(Object parameter, String table) {
        if (notAllowed()){
            return;
        }
        if (mybatisPlusSyncProperties.containTable(table)){
            if (parameter instanceof Map){
                deleteRedisList(parameter,table);
                return;
            }
            deleteRedisSingle(parameter,table);
            return;
        }
    }

    private void deleteRedisSingle(Object parameter, String tableName) {
        try {
            String id = String.valueOf(parameter);
            String redisKey = String.format(KEY_FORMAT,tableName,id);
            deleteRedisKey(redisKey);
        } catch (Exception e) {
            logger.info("sync failed message:{}",e.getMessage());
        }
    }

    private void deleteRedisList(Object parameter, String tableName) {
        try {
            List<Object> entityList = ((Map<?, List<Object>>) parameter).get("coll");
            List<String> redisKeyList = Lists.newArrayList();
            for (Object obj:entityList ) {
                String id = String.valueOf(obj);
                String redisKey = String.format(KEY_FORMAT,tableName,id);
                redisKeyList.add(redisKey);
            }
            redisUtil.delete(redisKeyList);
        } catch (Exception e) {
            logger.info("sync failed message:{}",e.getMessage());
        }
    }
}
