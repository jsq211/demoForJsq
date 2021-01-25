package com.jsq.component.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * 数据库配置
 * @author jsq
 */
@Configuration
@ConfigurationProperties(prefix = "spring.datasource")
@Data
public class DatabaseConfig {
    private String url;
    private String userName;

    private static String databaseName = null;

    public String getDatabaseName(){
        if (null == databaseName){
            synchronized (DatabaseConfig.class){
                if (null == databaseName){
                    databaseName = url.substring(url.lastIndexOf("/")+1,url.indexOf("?"));
                }
            }
        }
        return databaseName;
    }
}
