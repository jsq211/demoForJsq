package com.jsq.component.event.listener.impl;

import com.alibaba.fastjson.JSONObject;
import com.jsq.component.event.RedisUpdateEvent;
import com.jsq.component.util.RedisUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.scheduling.annotation.Async;

/**
 * @author jsq
 * created on 2021/1/27
 **/

public class RedisUpdateSyncListener implements ApplicationListener<RedisUpdateEvent> {
    private static final Logger logger = LoggerFactory.getLogger(RedisUpdateSyncListener.class);
    private static final String SELECT_SQL = "select * from `%s`.`%s` where id = %s";
    private static final String KEY_FORMAT= "%s:%s:%s";
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    @Async
    public void onApplicationEvent(RedisUpdateEvent redisUpdateEvent) {
        logger.info("update sync event,event msg:{}",redisUpdateEvent.toString());
        if (redisUpdateEvent.hasNullValue()){
            return;
        }
        String sql = createUpdateSql(redisUpdateEvent);
        SqlRowSet sqlRowSet = jdbcTemplate.queryForRowSet(sql);
        while (sqlRowSet.next()){
            JSONObject jsonObject = new JSONObject();
            for (String columnName: sqlRowSet.getMetaData().getColumnNames()) {
                jsonObject.put(columnName,sqlRowSet.getString(columnName));
            }
            RedisUtil.getInstance().set(String.format(KEY_FORMAT,redisUpdateEvent.getDatabaseName(),redisUpdateEvent.getTableName(),redisUpdateEvent.getId()),jsonObject);
        }
    }

    private String createUpdateSql(RedisUpdateEvent redisUpdateEvent) {
        return String.format(SELECT_SQL,redisUpdateEvent.getDatabaseName(),redisUpdateEvent.getTableName(),redisUpdateEvent.getId());
    }

}
