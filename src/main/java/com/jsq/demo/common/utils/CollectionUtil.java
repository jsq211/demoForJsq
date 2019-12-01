package com.jsq.demo.common.utils;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.jsq.demo.common.annotation.LoggerAnnotation;
import org.apache.commons.beanutils.PropertyUtils;
import org.springframework.lang.Nullable;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.lang.reflect.Field;
import java.util.*;

/**
 *
 * @author jsq
 */
public class CollectionUtil {
    /**
     *
     * list 转 map
     * 去除重复数据
     * @param collection 集合
     * @param propertyName 属性名
     * @param translate 当Key重复时操作状态 True 覆盖 False 跳过,默认为true
     * @param <K>
     * @param <V>
     * @jdk 1.8
     * @return
     */
    @LoggerAnnotation
    public static <K,V> Map<K,V> transferMap(Collection collection ,String propertyName,Boolean translate){
        checkInputParam(collection,propertyName);
        Map<K,V> map = new HashMap<>(collection.size());
        Boolean trans = null != translate?translate : true;
        for (Object obj : collection) {
            V value = (V) obj;
            try {
                Field  field = obj.getClass().getDeclaredField(propertyName);
                if (null == field){
                    continue;
                }
                field.setAccessible(true);
                K key = (K) field.get(obj);
                if (null != key && trans){
                    map.put(key,value);
                }
            } catch (NoSuchFieldException e) {
                throw new RuntimeException("class has no such property name :"+ propertyName);
            } catch (IllegalAccessException e) {
                throw new RuntimeException("class has no such property value :" + propertyName);
            }
        }
        return map;
    }

    /**
     * 集合入参校验值 不能为空
     * @param collection
     * @param propertyName
     */
    private static void checkInputParam(Collection collection, String propertyName) {
        if (CollectionUtils.isEmpty(collection)){
            throw new RuntimeException("collection is empty");
        }
        if (StringUtils.isEmpty(propertyName)){
            throw new RuntimeException("propertyName is empty");
        }
    }

    /**
     * list获取属性 默认过滤null值
     * @param collection
     * @param propertyName
     * @jdk 1.8
     * @return
     */
    @LoggerAnnotation
    public static <E> List<E> transferListValue(Collection collection, String propertyName){
        checkInputParam(collection,propertyName);
        List list = Lists.newArrayList();
        for (Object obj :collection) {
            try {
                Field  field  = obj.getClass().getDeclaredField(propertyName);
                if (null != field){
                    field.setAccessible(true);
                    E ele  = (E) field.get(obj);
                    list.add(ele);
                }
            } catch (NoSuchFieldException ex) {
                throw new RuntimeException("fieldName is empty :" + propertyName);
            } catch (IllegalAccessException e) {
                throw new RuntimeException("class has no such property value :" + propertyName);
            }
        }
        return list;
    }
    public static <K,V extends Map<K, V>>Map<K,List> transferMapGroup(Collection collection, String propertyName){
        checkInputParam(collection,propertyName);
        Map<K,List> ans = new HashMap<>(collection.size()>8?8:collection.size());
        List list = new ArrayList();
        for (Object obj :collection){
            try {
                Field field = obj.getClass().getDeclaredField(propertyName);
                if (null == field){
                    continue;
                }
                field.setAccessible(true);
                Object object = field.get(obj);
                if (ans.containsKey(object)){
                    list = Lists.newArrayList(ans.get(object));
                    list.add(obj);
                    ans.put((K) object,list);
                }else {
                    ans.put((K) object,Lists.newArrayList(obj));
                }
            } catch (NoSuchFieldException ex) {
                throw new RuntimeException("fieldName is empty :" + propertyName);
            } catch (IllegalAccessException e) {
                throw new RuntimeException("class has no such property value :" + propertyName);
            }
        }
        return ans;
    }
    public static Boolean isEmpty(@Nullable Collection collection){
        return (collection == null || collection.isEmpty());
    }

    public static Boolean isNotEmpty(@Nullable Collection collection){
        return (collection != null && !collection.isEmpty());
    }

    public static boolean isEmpty(@Nullable Map<?, ?> map) {
        return map == null || map.isEmpty();
    }

    public static boolean isNotEmpty(@Nullable Map<?, ?> map) {
        return map != null && !map.isEmpty();
    }


}
