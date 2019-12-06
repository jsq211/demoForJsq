package com.jsq.demo.common.utils;

import java.util.HashMap;
import java.util.Map;

/**
 * 调用链等threadLocalUtils
 * @author jsq
 */
public class ThreadLocalUtils {

    private final static ThreadLocal<Map<String, Object>> THREAD_CONTEXT = new MapThreadLocal();

    private static class MapThreadLocal extends ThreadLocal<Map<String, Object>> {
        /**
         * 初始化threadLocalMap
         */
        @Override
        protected Map<String, Object> initialValue() {
            return new HashMap<String, Object>(8) {

                private static final long serialVersionUID = 2112112112112112112L;

                @Override
                public Object put(String key, Object value) {
                    return super.put(key, value);
                }
            };
        }
    }

    /**
     * 获取对象
     * @param key
     * @return
     */
    public static Object get(String key) {
        return getContextMap().get(key);
    }

    /**
     * 获取当前对象map实例
     * @return
     */
    private static Map<String, Object> getContextMap() {
        return THREAD_CONTEXT.get();
    }

    /**
     * 清空所有threadLocal
     */
    public static void clear() {
        getContextMap().clear();
    }

    public static void remove(String key) {
        getContextMap().remove(key);
    }

    /**
     * 存入threadL
     * @param key
     * @param value
     */
    public static void put(String key, Object value) {
        getContextMap().put(key, value);
    }

}
