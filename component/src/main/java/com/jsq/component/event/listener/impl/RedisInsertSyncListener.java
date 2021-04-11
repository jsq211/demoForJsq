package com.jsq.component.event.listener.impl;

import com.alibaba.fastjson.JSONObject;
import com.jsq.component.config.MybatisPlusSyncProperties;
import com.jsq.component.event.RedisInsertEvent;
import com.jsq.component.util.BeanUtil;
import com.jsq.component.util.RedisUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import java.util.Set;

/**
 * @author jsq
 * created on 2021/1/27
 **/
public class RedisInsertSyncListener implements ApplicationListener<RedisInsertEvent> {
    private static final Logger logger = LoggerFactory.getLogger(RedisInsertSyncListener.class);
    private static final String SELECT_SQL = "select * from `%s` where id = %s";

    private static final String KEY_FORMAT= "%s:%s";
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private MybatisPlusSyncProperties mybatisPlusSyncProperties;

    @Override
    public void onApplicationEvent(RedisInsertEvent event) {
        logger.info("insert sync event,event msg:{}",event.toString());
        if (event.hasNullValue()){
            return;
        }
        JSONObject redisObject = new JSONObject();
        Set<String> props = mybatisPlusSyncProperties.getTableProps(event.getTableName());
        String sql = createInsertSql(event);
        SqlRowSet sqlRowSet = jdbcTemplate.queryForRowSet(sql);
        while (sqlRowSet.next()){
            for (String columnName: sqlRowSet.getMetaData().getColumnNames()) {
                String name = BeanUtil.convertName(columnName);
                if (props.contains(name)){
                    Object target = sqlRowSet.getObject(columnName);
                    redisObject.put(name,target);
                }
            }
        }
        RedisUtil.getInstance().set(String.format(KEY_FORMAT,event.getTableName(),event.getId()),redisObject);
    }

    private String createInsertSql(RedisInsertEvent redisInsertEvent) {
        return String.format(SELECT_SQL,redisInsertEvent.getTableName(),redisInsertEvent.getId());
    }
}
