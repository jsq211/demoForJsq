package com.jsq.component.config;

import com.jsq.component.util.SpringUtil;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

/**
 * 配置信息
 * @author jsq
 */
@Configuration
@ConfigurationProperties(prefix="jsq")
public class MybatisPlusSyncProps {
    private static volatile MybatisPlusSyncProps instance = null;
    private Map<String,String> sync;

    private static final String ENABLED = "enabled";

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

}
