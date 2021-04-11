package com.jsq.component.manager;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.jsq.component.annotation.RedisCacheInput;
import com.jsq.component.config.MybatisPlusSyncProperties;
import com.jsq.component.dto.RedisPropertyDTO;
import com.jsq.component.util.RedisUtil;
import com.jsq.component.util.SpringUtil;
import org.apache.commons.beanutils.PropertyUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.lang.reflect.Field;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author jsq
 * created on 2021/2/3
 **/
public class RedisCacheSyncManager {
    private static Logger logger = LoggerFactory.getLogger(RedisCacheSyncManager.class);

    private static RedisCacheSyncManager instance = null;

    private static final String REDIS_KEY_FORMAT = "%s:%s";

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
        if (MybatisPlusSyncProperties.getInstance().logEnabled()){
            logger.info("init object :{}", JSON.toJSONString(objectList));
        }
        objectList.forEach(this::init);
    }

    public void init(Object object){
        if (Objects.isNull(object)){
            return;
        }
        if (MybatisPlusSyncProperties.getInstance().logEnabled()){
            logger.info("init object :{}", JSON.toJSONString(object));
        }
        Class<?> clazz = object.getClass();
        List<RedisPropertyDTO> propertyList = Lists.newArrayList();
        Field[] fields = clazz.getDeclaredFields();
        List<String> redisKeys = addRedisKeys(object,fields,propertyList);
        if (CollectionUtils.isEmpty(redisKeys)){
            return;
        }
        Map<String,List<RedisPropertyDTO>> redisMap = propertyList.stream().collect(Collectors.groupingBy(RedisPropertyDTO::getRedisKey));
        for (String redisKey: redisKeys) {
            Object obj = RedisUtil.getInstance().getObj(redisKey);
            if (Objects.isNull(obj)){
                continue;
            }
            List<RedisPropertyDTO> redisPropertyList = redisMap.get(redisKey);
            if ( obj instanceof JSONObject){
                redisPropertyList.forEach(redisProperty -> {
                    Object value = ((JSONObject) obj).get(redisProperty.getOutPutKey());
                    setFieldValue(object,redisProperty.getFieldName(),value);
                });
                continue;
            }
            if (obj instanceof Map){
                redisPropertyList.forEach(redisProperty -> {
                    Object value = ((Map) obj).get(redisProperty.getOutPutKey());
                    setFieldValue(object,redisProperty.getFieldName(),value);
                });
            }
        }
    }

    private List<String> addRedisKeys(Object object, Field[] fields, List<RedisPropertyDTO> propertyList) {
        List<String> redisKeys = Lists.newArrayList();
        for(Field field : fields){
            if (field.isAnnotationPresent(RedisCacheInput.class)){
                String redisKey = getRedisKey(object,field,redisKeys);
                if (!StringUtils.isEmpty(redisKey)){
                    propertyList.add(new RedisPropertyDTO(field.getName(),
                            field.getAnnotation(RedisCacheInput.class).outPutKey()
                            ,redisKey));
                }
            }
        }
        getParentField(object, propertyList,redisKeys);
        return redisKeys;
    }


    private String getRedisKey(Object object,Field field, List<String> redisKeys) {
        try {
            RedisCacheInput redisCacheInput = field.getAnnotation(RedisCacheInput.class);
            Object id = PropertyUtils.getProperty(object,redisCacheInput.inputKey());
            String key = String.format(REDIS_KEY_FORMAT,redisCacheInput.table(), id);
            redisKeys.add(key);
            return key;
        } catch (Exception e) {
            logger.warn("redisKey convert error,msg:{}",e.getMessage());
        }
        return "";
    }

    private void getParentField(Object object,List<RedisPropertyDTO> propertyList, List<String> redisKeys){
        Class<?> clazz = object.getClass();
        Class<?> superClazz = clazz.getSuperclass();
        Field[] superFields = superClazz.getDeclaredFields();
        if (superFields.length>0) {
            for(Field field : superFields){
                if (field.isAnnotationPresent(RedisCacheInput.class)) {
                    String redisKey = getRedisKey(object,field,redisKeys);
                    if (StringUtils.isEmpty(redisKey)){
                        continue;
                    }
                    propertyList.add(new RedisPropertyDTO(field.getName(),
                            field.getAnnotation(RedisCacheInput.class).outPutKey(),
                            redisKey));
                }
            }
            getParentField(superClazz, propertyList, redisKeys);
        }
    }

    private void setFieldValue(Object obj, String fieldName, Object value) {
        try {
            Class type = PropertyUtils.getPropertyType(obj,fieldName);
            if (type == Date.class){
                PropertyUtils.setProperty(obj,fieldName,new Date(Long.parseLong((String) value)));
            }else {
                PropertyUtils.setProperty(obj,fieldName,value);
            }
        } catch (Exception e) {
            logger.warn("set value failed ,msg:{}",e.getMessage());
        }
    }

}
