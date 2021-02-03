package com.jsq.component.event;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * @author jsq
 * created on 2021/1/27
 **/
@Getter
@EqualsAndHashCode(callSuper = false)
public abstract class BaseRedisEvent extends ApplicationEvent {
    private String baseEventName;
    private String aggregateId;
    private static final String EVENT_PREFIX = "REDIS_";

    BaseRedisEvent(String eventName, String aggregateId) {
        super(eventName);
        this.baseEventName = EVENT_PREFIX+eventName;
        this.aggregateId = aggregateId;
    }

}
