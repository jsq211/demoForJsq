package com.jsq.component.event.listener.impl;

import com.alibaba.fastjson.JSONObject;
import com.jsq.component.config.MybatisPlusSyncProperties;
import com.jsq.component.event.RedisUpdateEvent;
import com.jsq.component.util.BeanUtil;
import com.jsq.component.util.RedisUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.lang.NonNull;
import org.springframework.scheduling.annotation.Async;

import java.util.Set;

/**
 * @author jsq
 * created on 2021/1/27
 **/

public class RedisUpdateSyncListener implements ApplicationListener<RedisUpdateEvent> {
    private static final Logger logger = LoggerFactory.getLogger(RedisUpdateSyncListener.class);
    private static final String SELECT_SQL = "select * from `%s` where id = %s";
    private static final String KEY_FORMAT= "%s:%s";
    private final JdbcTemplate jdbcTemplate;
    private final MybatisPlusSyncProperties mybatisPlusSyncProperties;

    public RedisUpdateSyncListener(JdbcTemplate jdbcTemplate, MybatisPlusSyncProperties mybatisPlusSyncProperties) {
        this.jdbcTemplate = jdbcTemplate;
        this.mybatisPlusSyncProperties = mybatisPlusSyncProperties;
    }

    @Override
    @Async
    public void onApplicationEvent(@NonNull RedisUpdateEvent redisUpdateEvent) {
        logger.info("update sync event,event msg:{}",redisUpdateEvent.toString());
        if (redisUpdateEvent.hasNullValue()){
            return;
        }
        String sql = createUpdateSql(redisUpdateEvent);
        SqlRowSet sqlRowSet = jdbcTemplate.queryForRowSet(sql);
        Set<String> props = mybatisPlusSyncProperties.getTableProps(redisUpdateEvent.getTableName());
        JSONObject jsonObject = BeanUtil.convertRedisObject(sqlRowSet,props);
        RedisUtil.getInstance().set(String.format(KEY_FORMAT,redisUpdateEvent.getTableName(),redisUpdateEvent.getId()),jsonObject);

    }

    private String createUpdateSql(RedisUpdateEvent redisUpdateEvent) {
        return String.format(SELECT_SQL,redisUpdateEvent.getTableName(),redisUpdateEvent.getId());
    }

}
