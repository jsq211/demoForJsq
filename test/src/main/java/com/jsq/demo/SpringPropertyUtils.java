package com.jsq.demo;

import org.springframework.beans.factory.config.PropertiesFactoryBean;
import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.util.Properties;


/**
 * @author: jsq
 * @Date: 2021/2/27 17:24
 **/
public class SpringPropertyUtils {


    /**
     * 根据传入的yml获取yml中key的属性值
     *
     * @param key
     * @param yml
     * @return
     */
    public static Object getYml(Object key, String yml) {
        Resource resource = new ClassPathResource(yml);
        Properties properties;
        try {
            YamlPropertiesFactoryBean yamlFactory = new YamlPropertiesFactoryBean();
            yamlFactory.setResources(resource);
            properties = yamlFactory.getObject();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return properties.get(key);
    }

    /**
     * 自动根据 application中激活的配置文件,选择对应的yml读取属性
     *
     * @param key
     * @return
     */
    public static Object getYml(Object key) {
        Object value = getYml(key, "application.yml");
        if (value != null) {
            return value;
        }
        return null;
    }

    public static void main(String[] args) {
        System.out.println(getYml("server.port"));
    }
}
