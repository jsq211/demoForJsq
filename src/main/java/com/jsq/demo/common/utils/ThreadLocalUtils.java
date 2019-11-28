package com.jsq.demo.common.utils;

import com.jsq.demo.ThreadPoolTracing;
import com.jsq.demo.pojo.dto.TraceDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 调用链等threadLocalUtils
 * @author jsq
 */
public class ThreadLocalUtils {

    private static final Logger logger = LoggerFactory.getLogger(ThreadPoolTracing.class);

    public static InheritableThreadLocal<TraceDTO>  threadLocalTraceId = new InheritableThreadLocal<>();


}
