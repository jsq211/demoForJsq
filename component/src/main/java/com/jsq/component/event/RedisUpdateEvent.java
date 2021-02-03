package com.jsq.component.event;

import lombok.Getter;
import lombok.ToString;
import org.springframework.util.StringUtils;

/**
 * @author jsq
 * created on 2021/1/27
 **/
@Getter
@ToString
public class RedisUpdateEvent extends BaseRedisEvent {
    private static final String EVENT_NAME = "RedisUpdateEvent";
    private String databaseName;
    private String tableName;
    private Long id;

    public RedisUpdateEvent(String redisKey,String databaseName,String tableName,Long id) {
        super(EVENT_NAME, redisKey);
        this.databaseName = databaseName;
        this.tableName = tableName;
        this.id = id;
    }

    public boolean hasNullValue(){
        return this.id == null || StringUtils.isEmpty(tableName) || StringUtils.isEmpty(databaseName);
    }
}
