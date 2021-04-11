package com.jsq.component.event;

import lombok.Getter;
import org.springframework.util.StringUtils;

/**
 * @author jsq
 * created on 2021/4/11
 **/
@Getter
public class RedisInsertEvent extends BaseRedisEvent {
    private static final String EVENT_NAME = "RedisInsertEvent";
    private String tableName;
    private String id;

    public boolean hasNullValue(){
        return this.id == null || StringUtils.isEmpty(tableName);
    }
    public RedisInsertEvent(String tableName, String id) {
        super(EVENT_NAME, tableName+":"+id);
        this.tableName = tableName;
        this.id = id;
    }
}
