package com.jsq.demo;

import com.alibaba.fastjson.JSON;
import com.jsq.demo.pojo.dto.ThreadPoolParamDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

/**
 * 线程方法调用
 * @author jsq
 */
public class InvokeRunnable implements Runnable {

    private static final Logger logger = LoggerFactory.getLogger(InvokeRunnable.class);

    private ThreadPoolParamDTO.ThreadParam threadParam;

    private Map<String, Object>  result;

    public InvokeRunnable(ThreadPoolParamDTO.ThreadParam threadParam,Map<String, Object> result) {
        this.threadParam = threadParam;
        this.result = result;
    }

    /**
     * 反射调用方法 返回调用结果
     * @param threadParam
     * @return
     */
    private Object invokeMethod (ThreadPoolParamDTO.ThreadParam threadParam, Map<String, Object> result) {
        Object[] o = threadParam.getRequestParam();
        try {
            logger.error("开始获取参数。。。。。。。。", threadParam.toString());
            Class param = threadParam.getClassName();
            Method method = threadParam.getMethod();
            logger.error("获取方法。。。。。。。。{}", JSON.toJSONString(param));
            Object response = method.invoke(param,o);
            if (null != response){
                result.put(method.getName(),response);
            }
            return result;
        } catch (BeansException e) {
            logger.error("获取容器对象失败，失败信息：{}", JSON.toJSONString(e.getMessage()));
        } catch (IllegalAccessException e) {
            logger.error("调用对象方法失败，失败信息：{}", JSON.toJSONString(e.getMessage()));
        } catch (InvocationTargetException e) {
            logger.error("获取对象出参失败，失败信息：{}", JSON.toJSONString(e));
        }
        return null;
    }

    @Override
    public void run() {
        invokeMethod(threadParam, result);
    }

}
