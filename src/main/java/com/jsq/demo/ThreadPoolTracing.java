package com.jsq.demo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ThreadPoolTracing {
    private static final Logger logger = LoggerFactory.getLogger(ThreadPoolTracing.class);
    public static InheritableThreadLocal<String>  threadLocalTraceId = new InheritableThreadLocal<>();

    static class Task implements Runnable {

        @Override
        public void run() {
            String traceId=threadLocalTraceId.get();
            logger.info("traceId={}",traceId);
        }
    }
}
