package com.jsq.component.util;

import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.data.redis.core.ValueOperations;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * redis工具类
 * 1.加载bean容器使用
 * 2.get instance 使用
 * @author jsq
 */
public class RedisUtil {

    private final RedisTemplate<String, Object> redisTemplate;
    private final ValueOperations<String, Object> strVal;
    private final ListOperations<String, Object> strList;
    private final SetOperations<String, Object> strSet;

    @SuppressWarnings("unchecked")
    public RedisUtil(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
        this.strVal = redisTemplate.opsForValue();
        this.strList = redisTemplate.opsForList();
        this.strSet = redisTemplate.opsForSet();
    }
    private static volatile RedisUtil instance = null;

    public static RedisUtil getInstance() {
        if (instance == null) {
            synchronized (RedisUtil.class) {
                if (instance == null) {
                    instance = SpringUtil.getBean(RedisUtil.class);
                }
            }
        }
        return instance;
    }

    public void set(String key, Object val) {
        this.strVal.set(key, val);
    }

    public void set(String key, String val, long expires) {
        this.strVal.set(key, val, expires, TimeUnit.MILLISECONDS);
    }

    public void set(String key, String val, long expires, TimeUnit timeUnit) {
        this.strVal.set(key, val, expires, timeUnit);
    }

    public Boolean setIfAbsent(String key, String val) {
        return this.strVal.setIfAbsent(key, val);
    }

    public Boolean setIfAbsent(String key, String val, long expires, TimeUnit timeUnit) {
        return this.strVal.setIfAbsent(key, val, expires, timeUnit);
    }

    public String getString(String key) {
        return (String) this.strVal.get(key);
    }

    public <T> T getObj(String key) {
        return (T) this.strVal.get(key);
    }

    public  <T> T  getAndSet(String key, Object value) {
        return (T) this.strVal.getAndSet(key, value);
    }

    public List<Object> multiGet(Collection<String> keys) {
        return this.strVal.multiGet(keys);
    }

    public void setList(String key, int index, Object val) {
        this.strList.set(key, index, val);
    }

    public Object indexList(String key, int index) {
        return this.strList.index(key, index);
    }

    public Long pushList(String key, Object... vals) {
        return this.strList.rightPushAll(key, vals);
    }

    public Long addSet(String key, Object... vals) {
        return this.strSet.add(key, vals);
    }

    public Object popSet(String key) {
        return this.strSet.pop(key);
    }

    public RedisTemplate<String,Object> getRedisTemplate(){
        return redisTemplate;
    }

    public Boolean delete(String redisKey) {
        return redisTemplate.delete(redisKey);
    }
    public Long delete(Collection<String> redisKeyList) {
        return redisTemplate.delete(redisKeyList);
    }

    public Boolean refreshAll(){
        Set<String> keys = redisTemplate.keys("*");
        try {
            redisTemplate.delete(keys);
            return Boolean.TRUE;
        } catch (Exception e) {
            return Boolean.FALSE;
        }

    }
}
