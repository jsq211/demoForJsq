package com.jsq.component.manager;

import com.alibaba.fastjson.JSONObject;
import com.jsq.component.config.DatabaseConfig;
import com.jsq.component.config.MybatisPlusSyncProps;
import com.jsq.component.util.RedisUtil;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.jdbc.support.rowset.SqlRowSetMetaData;
import org.springframework.util.CollectionUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * 手动触发同步更新
 * @author jsq
 * created on 2021/2/4
 **/
public class RedisCacheManualManager {
    private static final String SQL = "select * from %s where id >= %d and id <= %d";
    private static final String COUNT = "select count(*) from %s";
    private static final Integer SIZE = 1000;
    private final JdbcTemplate jdbcTemplate;

    private final DatabaseConfig databaseConfig;

    public RedisCacheManualManager(JdbcTemplate jdbcTemplate, DatabaseConfig databaseConfig) {
        this.jdbcTemplate = jdbcTemplate;
        this.databaseConfig = databaseConfig;
    }

    public void manualAll(Set<String> tableSet){
        if (!MybatisPlusSyncProps.getInstance().isEnabled()){
            return;
        }
        if (CollectionUtils.isEmpty(tableSet)){
            return;
        }
        tableSet.forEach(table->{
            String tablePrefix = table+":"+databaseConfig.getDatabaseName()+":";
            SqlRowSet sqlRowSet = jdbcTemplate.queryForRowSet(String.format(COUNT,table));
            int rowCount = 0;
            if(sqlRowSet.next()) {
                rowCount=sqlRowSet .getInt(1);
            }
            int maxId = SIZE;
            int minId = 1;
            while (rowCount>=maxId){
                SqlRowSet insertList = jdbcTemplate.queryForRowSet(String.format(SQL,table,minId,maxId));
                maxId+=SIZE;
                minId+=SIZE;
                Map<String, JSONObject> map = getInsertMap(tablePrefix,insertList,SIZE);
                RedisUtil.getInstance().getRedisTemplate().execute((RedisCallback<String>) redisConnection -> {
                    map.forEach((key, value) -> redisConnection.set(key.getBytes(), value.toJSONString().getBytes()));
                    return null;
                });
            }
            if (maxId!=rowCount){
                SqlRowSet insertList = jdbcTemplate.queryForRowSet(String.format(SQL,table,minId,rowCount));
                Map<String, JSONObject> map = getInsertMap(tablePrefix,insertList,SIZE);
                RedisUtil.getInstance().getRedisTemplate().execute((RedisCallback<String>) redisConnection -> {
                    map.forEach((key, value) -> redisConnection.set(key.getBytes(), value.toString().getBytes()));
                    return null;
                });
            }
        });

    }

    private Map<String, JSONObject> getInsertMap(String keyPrefix,SqlRowSet insertList, Integer size) {
        Map<String, JSONObject> map = new HashMap<>(size);
        while (insertList.next()){
            SqlRowSetMetaData rsMeta=insertList.getMetaData();
            int columnCount=rsMeta.getColumnCount();
            JSONObject jsonObject = new JSONObject();
            for (int i=1; i<=columnCount; i++) {
                jsonObject.put(rsMeta.getColumnLabel(i),insertList.getString(i));
            }
            map.put(keyPrefix+jsonObject.getString("id"),jsonObject);
        }
        return map;
    }
}
