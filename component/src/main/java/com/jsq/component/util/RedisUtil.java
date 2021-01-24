package com.jsq.component.util;

import org.springframework.data.redis.core.RedisTemplate;

/**
 * redis工具类
 * 1.加载bean容器使用
 * 2.get instance 使用
 * @author jsq
 */
public class RedisUtil {

    public RedisUtil(RedisTemplate<String,Object> redisTemplate) {

    }
}
