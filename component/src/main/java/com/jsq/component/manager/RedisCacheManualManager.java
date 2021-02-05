package com.jsq.component.manager;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import com.jsq.component.config.DatabaseConfig;
import com.jsq.component.config.MybatisPlusSyncProps;
import com.jsq.component.util.RedisUtil;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.jdbc.support.rowset.SqlRowSetMetaData;
import org.springframework.util.CollectionUtils;

import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 手动触发同步更新
 * @author jsq
 * created on 2021/2/4
 **/
public class RedisCacheManualManager {
    private static final String SQL = "select * from %s limit %d offset %d";
    private static final String COUNT = "select count(*) from %s";
    private static final Integer SIZE = 1000;
    private final JdbcTemplate jdbcTemplate;

    private final DatabaseConfig databaseConfig;

    private static Pattern linePattern = Pattern.compile("_(\\w)");

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
            String tablePrefix = databaseConfig.getDatabaseName()+":"+table+":";
            SqlRowSet sqlRowSet = jdbcTemplate.queryForRowSet(String.format(COUNT,table));
            int rowCount = 0;
            if(sqlRowSet.next()) {
                rowCount=sqlRowSet .getInt(1);
            }
            int offset = 0;
            while (rowCount>=offset){
                SqlRowSet insertList = jdbcTemplate.queryForRowSet(String.format(SQL,table,SIZE,offset));
                Map<String, JSONObject> map = getInsertMap(tablePrefix,insertList);
                RedisUtil.getInstance().getRedisTemplate().execute((RedisCallback<String>) redisConnection -> {
                    map.forEach((key, value) -> redisConnection.set(key.getBytes(), value.toJSONString().getBytes()));
                    return null;
                });
                offset+=SIZE;
            }

        });

    }

    private Map<String, JSONObject> getInsertMap(String keyPrefix, SqlRowSet insertList) {
        Map<String, JSONObject> map = Maps.newHashMap();
        while (insertList.next()){
            SqlRowSetMetaData rsMeta=insertList.getMetaData();
            int columnCount=rsMeta.getColumnCount();
            JSONObject jsonObject = new JSONObject();
            for (int i=1; i<=columnCount; i++) {
                jsonObject.put(convertName(rsMeta.getColumnLabel(i)),insertList.getString(i));
            }
            map.put(keyPrefix+jsonObject.getString("id"),jsonObject);
        }
        return map;
    }

    private String convertName(String columnLabel) {
        columnLabel = columnLabel.toLowerCase();
        Matcher matcher = linePattern.matcher(columnLabel);
        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            matcher.appendReplacement(sb, matcher.group(1).toUpperCase());
        }
        matcher.appendTail(sb);
        return sb.toString();
    }
}
