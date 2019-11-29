package com.jsq.demo.common.utils;

import com.google.common.collect.Lists;
import com.jsq.demo.common.annotation.LoggerAnnotation;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 *
 * @author jsq
 */
public class CollectionUtil {
    /**
     *
     * list 转 map
     * 去除重复数据
     * @param collection
     * @param propertyName
     * @param <K>
     * @param <V>
     * @jdk 1.8
     * @return
     */
    @LoggerAnnotation
    public static <K,V> Map<K,V> transferMap(final Collection collection ,final String propertyName){
        if (CollectionUtils.isEmpty(collection)){
            throw new RuntimeException("collection is empty");
        }
        if (StringUtils.isEmpty(propertyName)){
            throw new RuntimeException("propertyName is empty");
        }
        Map<K,V> map = new HashMap<>(collection.size());
        collection.stream().distinct().forEach(element->{
            V value = (V) element;
            try {
                K key = (K) element.getClass().getDeclaredField(propertyName);
                if (null != key){
                    map.put(key,value);
                }
            } catch (NoSuchFieldException e) {
                throw new RuntimeException("class has no such property name :"+ propertyName);
            }
        });
        return map;
    }

    /**
     * list获取属性
     * @param collection
     * @param propertyName
     * @jdk 1.8
     * @return
     */
    public static <E> List<E> transferListValue(final Collection collection,final String propertyName){
        if (CollectionUtils.isEmpty(collection)){
            throw new RuntimeException("collection is empty");
        }
        if (StringUtils.isEmpty(propertyName)){
            throw new RuntimeException("propertyName is empty");
        }
        List list = Lists.newArrayList();
        for (Object obj :collection) {
            try {
                E ele = (E) obj.getClass().getDeclaredField(propertyName);
                if (null != ele){
                    list.add(ele);
                }
            } catch (NoSuchFieldException ex) {
                throw new RuntimeException("fieldName is empty :" + propertyName);
            }
        }
        return list;
    }


}
