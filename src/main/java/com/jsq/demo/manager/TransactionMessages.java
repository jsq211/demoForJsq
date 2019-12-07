package com.jsq.demo.manager;

import java.lang.reflect.Method;

/**
 * 事物管理
 * 用于处理子事物与父级处于同一条件下无法使用@translation来进行回滚
 * @author jsq
 */
public class TransactionMessages {

    private Class clazz;

    private Method method;

    private Object[] args;

    public Class getClazz() {
        return clazz;
    }

    public void setClazz(Class clazz) {
        this.clazz = clazz;
    }

    public Method getMethod() {
        return method;
    }

    public void setMethod(Method method) {
        this.method = method;
    }

    public Object[] getArgs() {
        return args;
    }

    public void setArgs(Object[] args) {
        this.args = args;
    }

    /**
     * 事物管理方法
     * @param clazz
     * @param method
     * @param args
     */
    public TransactionMessages(Class clazz, Method method, Object[] args) {
        this.clazz = clazz;
        this.method = method;
        this.args = args;
    }

}
