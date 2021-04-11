package com.jsq.component.config;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.jsq.component.util.SpringUtil;
import org.apache.commons.beanutils.PropertyUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.*;

/**
 * 配置信息
 * @author jsq
 */
@Configuration
@ConfigurationProperties(prefix="jsq.sync")
public class MybatisPlusSyncProperties {
    private static final Logger logger = LoggerFactory.getLogger(MybatisPlusSyncProperties.class);
    @SuppressWarnings("all")
    private static volatile MybatisPlusSyncProperties instance = null;
    private Boolean enabled = true;
    private Boolean logEnabled = false;
    private Map<String,Object> logicDelete;
    private List<String> tableList = Lists.newArrayList();
    private List<String> path;

    @Bean(name = "mybatisSyncTableProp")
    public Map<String,List<String>> tableProps(){
        Map<String,List<String>> propMap = new HashMap<>(tableList.size());
        if(CollectionUtils.isEmpty(tableList)){
            return new HashMap<>(0);
        }
        for (String table: tableList) {
            int index = table.indexOf(":");
            try {
                String tableName = table.substring(0,index);
                String props = table.substring(index+1);
                if (StringUtils.isEmpty(props)){
                    continue;
                }
                List<String> propList = Arrays.asList(props.split(","));
                propMap.put(tableName,propList);
            } catch (Exception e) {
                logger.warn("缓存同步表字段配置异常，请检查");
            }
        }
        return propMap;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public Boolean getLogEnabled() {
        return logEnabled;
    }

    public void setLogEnabled(Boolean logEnabled) {
        this.logEnabled = logEnabled;
    }

    public Map<String, Object> getLogicDelete() {
        return logicDelete;
    }

    public void setLogicDelete(Map<String, Object> logicDelete) {
        this.logicDelete = logicDelete;
    }

    public void setTableList(List<String> tableList) {
        this.tableList = tableList;
    }

    public List<String> getPath() {
        return path;
    }

    public void setPath(List<String> path) {
        this.path = path;
    }

    public static MybatisPlusSyncProperties getInstance(){
        if (null == instance){
            synchronized (MybatisPlusSyncProperties.class){
                if (null == instance){
                    instance = SpringUtil.getBean(MybatisPlusSyncProperties.class);
                }
            }
        }
        return instance;
    }

    public boolean isEnabled(){
        return enabled;
    }

    public boolean logEnabled(){
        return logEnabled;
    }

    public Set<String> getTableList() {
        try {
            if (CollectionUtils.isEmpty(tableList)){
                return Sets.newHashSet();
            }
            return Sets.newHashSet(tableList);
        } catch (Exception e) {
            return Sets.newHashSet();
        }
    }

    public Boolean isLogicDelete(Object obj){
        Map<String,Object> map = getDeleteFlag();
        if(CollectionUtils.isEmpty(map)){
            return false;
        }
        for (Map.Entry entry : map.entrySet()) {
            if (entry.getValue().equals(obj)){
                Object flagValue;
                try {
                    flagValue = PropertyUtils.getProperty(obj, (String) entry.getKey());
                } catch (Exception e) {
                    return false;
                }
                return flagValue.equals(entry.getValue());
            }
        }
        return false;
    }

    private Map<String,Object> getDeleteFlag() {
        if (StringUtils.isEmpty(logicDelete)){
            return new HashMap<>();
        }
        return logicDelete;
    }

    public Boolean containTable(String table) {
        return tableProps().containsKey(table);
    }

    public Set<String> getTableProps(String table){
        if (!tableProps().containsKey(table)){
            return Sets.newHashSet();
        }
        return Sets.newHashSet(tableProps().get(table));
    }
}
