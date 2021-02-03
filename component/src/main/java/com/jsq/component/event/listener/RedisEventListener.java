package com.jsq.component.event.listener;

import com.jsq.component.event.BaseRedisEvent;

/**
 * @author jsq
 * created on 2021/1/27
 **/
public interface RedisEventListener<T extends BaseRedisEvent>{
    /**
     * 处理事件
     * @param redisEvent 事件
     */
    void handleEvent( T redisEvent);

    /**
     * 类型
     * @return 返回事件类
     */
    Class<T> subscribedToEventType();
}
