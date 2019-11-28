package com.jsq.demo.pojo.dto;

import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

/**
 * 线程池参数
 * @author jsq
 */
public class ThreadPoolParamDTO {
    /**
     * 核心线程
     */
    private Integer corePoolSize;
    /**
     * 最大线程数
     */
    private Integer maximumPoolSize;
    /**
     * 持续时间
     */
    private Long keepAliveTime;
    /**
     * 单位
     */
    private TimeUnit unit;
    /**
     * 线程队列信息
     */
    private BlockingQueue<Runnable> workQueue;
    /**
     * 线程工厂类 用于区别线程类型
     */
    private ThreadFactory threadFactory;
    /**
     * 拒绝策略
     */
    private RejectedExecutionHandler handler;
    /**
     * 线程调用参数
     */
    private LinkedList<ThreadParam> paramList;

    public LinkedList<ThreadParam> getParamList() {
        return paramList;
    }

    public void setParamList(LinkedList<ThreadParam> paramList) {
        this.paramList = paramList;
    }

    public Integer getCorePoolSize() {
        return corePoolSize;
    }

    public void setCorePoolSize(Integer corePoolSize) {
        this.corePoolSize = corePoolSize;
    }

    public Integer getMaximumPoolSize() {
        return maximumPoolSize;
    }

    public void setMaximumPoolSize(Integer maximumPoolSize) {
        this.maximumPoolSize = maximumPoolSize;
    }

    public Long getKeepAliveTime() {
        return keepAliveTime;
    }

    public void setKeepAliveTime(Long keepAliveTime) {
        this.keepAliveTime = keepAliveTime;
    }

    public TimeUnit getUnit() {
        return unit;
    }

    public void setUnit(TimeUnit unit) {
        this.unit = unit;
    }

    public BlockingQueue<Runnable> getWorkQueue() {
        return workQueue;
    }

    public void setWorkQueue(BlockingQueue<Runnable> workQueue) {
        this.workQueue = workQueue;
    }

    public ThreadFactory getThreadFactory() {
        return threadFactory;
    }

    public void setThreadFactory(ThreadFactory threadFactory) {
        this.threadFactory = threadFactory;
    }

    public RejectedExecutionHandler getHandler() {
        return handler;
    }

    public void setHandler(RejectedExecutionHandler handler) {
        this.handler = handler;
    }

    @Override
    public String toString() {
        return "ThreadPoolParamDTO{" +
                "corePoolSize=" + corePoolSize +
                ", maximumPoolSize=" + maximumPoolSize +
                ", keepAliveTime=" + keepAliveTime +
                ", unit=" + unit +
                ", workQueue=" + workQueue +
                ", threadFactory=" + threadFactory +
                ", handler=" + handler +
                ", paramList=" + paramList +
                '}';
    }

    public static class ThreadParam {
        /**
         * 调用容器
         */
        private Class className;
        /**
         * 调用方法
         */
        private Method method;
        /**
         * 调用入参
         */
        private Object[] requestParam;

        public Class getClassName() {
            return className;
        }

        public void setClassName(Class className) {
            this.className = className;
        }

        public Method getMethod() {
            return method;
        }

        public void setMethod(Method method) {
            this.method = method;
        }

        public Object[] getRequestParam() {
            return requestParam;
        }

        public void setRequestParam(Object[] requestParam) {
            this.requestParam = requestParam;
        }

    }
}
