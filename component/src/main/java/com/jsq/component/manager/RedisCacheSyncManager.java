package com.jsq.component.manager;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.jsq.component.annotation.RedisCacheInput;
import com.jsq.component.util.RedisUtil;
import com.jsq.component.util.SpringUtil;
import org.apache.commons.beanutils.PropertyUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.lang.reflect.Field;
import java.util.*;

/**
 * @author jsq
 * created on 2021/2/3
 **/
public class RedisCacheSyncManager {
    private static Logger logger = LoggerFactory.getLogger(RedisCacheSyncManager.class);

    private static RedisCacheSyncManager instance = null;

    private static final String REDIS_KEY_FORMAT = "%s:%s:%s";

    public static RedisCacheSyncManager getInstance() {
        if (instance == null) {
            synchronized (RedisUtil.class) {
                if (instance == null) {
                    instance = SpringUtil.getBean(RedisCacheSyncManager.class);
                }
            }
        }
        return instance;
    }

    public void init(List<?> objectList){
        if (CollectionUtils.isEmpty(objectList)){
            return;
        }
        logger.info("init object :{}", JSON.toJSONString(objectList));
        objectList.forEach(this::init);
    }

    public void init(Object object){
        if (Objects.isNull(object)){
            return;
        }
        logger.info("init object :{}", JSON.toJSONString(object));
        Class<?> clazz = object.getClass();
        Map<String, String> fieldMap = Maps.newHashMap();
        Map<String, RedisCacheInput> redisCacheInputMap = Maps.newHashMap();
        Field[] fields = clazz.getDeclaredFields();
        List<String> redisKeys = addRedisKeys(object,fields,fieldMap,redisCacheInputMap);
        for (String redisKey: redisKeys) {
            Object obj = RedisUtil.getInstance().getObj(redisKey);
            if (Objects.isNull(obj)){
                continue;
            }
            String fieldName = fieldMap.get(redisKey);
            RedisCacheInput redisCacheInput = redisCacheInputMap.get(redisKey);
            if ( obj instanceof JSONObject){
                Object value = ((JSONObject) obj).get(redisCacheInput.outPutKey());
                setFieldValue(object,fieldName,value);
                continue;
            }
            if (obj instanceof Map){
                Object value = ((Map) obj).get(redisCacheInput.outPutKey());
                setFieldValue(object,fieldName,value);
            }
        }
    }

    private List<String> addRedisKeys(Object object, Field[] fields, Map<String, String> fieldMap,
                                      Map<String, RedisCacheInput> redisCacheInputMap) {
        List<String> redisKeys = Lists.newArrayList();
        for(Field field : fields){
            if (field.isAnnotationPresent(RedisCacheInput.class)){
                String redisKey = getRedisKey(object,field,redisKeys);
                if (!StringUtils.isEmpty(redisKey)){
                    fieldMap.put(redisKey,field.getName());
                    redisCacheInputMap.put(redisKey,field.getAnnotation(RedisCacheInput.class));
                }
            }
        }
        getParentField(object, fieldMap,redisCacheInputMap,redisKeys);
        return redisKeys;
    }


    private String getRedisKey(Object object,Field field, List<String> redisKeys) {
        try {
            RedisCacheInput redisCacheInput = field.getAnnotation(RedisCacheInput.class);
            Object id = PropertyUtils.getProperty(object,redisCacheInput.inputKey());
            String key = String.format(REDIS_KEY_FORMAT,redisCacheInput.database(),redisCacheInput.table(),String.valueOf(id));
            redisKeys.add(key);
            return key;
        } catch (Exception e) {
            logger.warn("redisKey convert error,msg:{}",e.getMessage());
        }
        return null;
    }

    private void getParentField(Object object, Map<String, String> fieldMap, Map<String, RedisCacheInput> redisCacheInputMap, List<String> redisKeys){
        Class<?> clazz = object.getClass();
        Class<?> superClazz = clazz.getSuperclass();
        Field[] superFields = superClazz.getDeclaredFields();
        if (superFields.length>0) {
            for(Field field : superFields){
                if (field.isAnnotationPresent(RedisCacheInput.class)) {
                    String redisCacheInput = getRedisKey(object,field,redisKeys);
                    if (StringUtils.isEmpty(redisCacheInput)){
                        continue;
                    }
                    fieldMap.put(redisCacheInput,field.getName());
                    redisCacheInputMap.put(redisCacheInput,field.getAnnotation(RedisCacheInput.class));
                }
            }
            getParentField(superClazz, fieldMap, redisCacheInputMap, redisKeys);
        }
    }

    private void setFieldValue(Object obj, String fieldName, Object value) {
        try {
            PropertyUtils.setProperty(obj,fieldName,value);
        } catch (Exception e) {
            logger.warn("set value failed ,msg:{}",e.getMessage());
        }
    }
}
