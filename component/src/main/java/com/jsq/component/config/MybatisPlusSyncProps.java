package com.jsq.component.config;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.jsq.component.util.SpringUtil;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 配置信息
 * @author jsq
 */
@Configuration
@ConfigurationProperties(prefix="jsq")
public class MybatisPlusSyncProps {
    @SuppressWarnings("all")
    private static volatile MybatisPlusSyncProps instance = null;
    private Map<String,String> sync;

    private static final String ENABLED = "enabled";
    private static final String TABLE_LIST = "tableList";
    private static final String PREFIX = "prefix";
    public static MybatisPlusSyncProps getInstance(){
        if (null == instance){
            synchronized (MybatisPlusSyncProps.class){
                if (null == instance){
                    instance = SpringUtil.getBean(MybatisPlusSyncProps.class);
                }
            }
        }
        return instance;
    }
    public Map<String, String> getSync() {
        return sync;
    }

    public void setSync(Map<String, String> sync) {
        this.sync = sync;
    }

    public boolean isEnabled(){
        return sync.containsKey(ENABLED) && Boolean.parseBoolean(sync.get(ENABLED));
    }

    public Set<String> getTableList() {
        try {
            String tableNameArray = sync.get(TABLE_LIST);
            if (StringUtils.isEmpty(tableNameArray)){
                return Sets.newHashSet();
            }
            return Sets.newHashSet(Arrays.asList(tableNameArray.split(",")));
        } catch (Exception e) {
            return Sets.newHashSet();
        }
    }

    public String getPrefix() {

        try {
            return sync.get(PREFIX);
        } catch (Exception e) {
            return "";
        }
    }
}
