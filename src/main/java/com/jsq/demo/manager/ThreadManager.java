package com.jsq.demo.manager;

import com.alibaba.fastjson.JSON;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.jsq.demo.InvokeRunnable;
import com.jsq.demo.common.annotation.LoggerAnnotation;
import com.jsq.demo.pojo.dto.ThreadPoolParamDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.*;

/**
 * 线程池调用类
 * 默认线程池构造参数为 coreSize = 1 maxSize = 2  线程持续时间180s  默认线程执行顺序为链表
 * 需要传入进入线程池中的具体方法参数 包括 调用类 className  调用方法 method  调用入参数据 Object...
 * @author jsq
 */
@Component
public class ThreadManager {

    private static final Logger logger = LoggerFactory.getLogger(ThreadManager.class);

    /**
     * 调用线程池参数
     * @param threadPoolParam
     * @return
     */
    @LoggerAnnotation
    public Map<String, Object> getThreadResult(ThreadPoolParamDTO threadPoolParam) {
        Map<String, Object> result = new ConcurrentHashMap<>(threadPoolParam.getParamList().size());
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(
                null == threadPoolParam.getCorePoolSize()? 1:threadPoolParam.getCorePoolSize(),
                null == threadPoolParam.getMaximumPoolSize()?2:threadPoolParam.getMaximumPoolSize(),
                null == threadPoolParam.getKeepAliveTime()? 180L:threadPoolParam.getKeepAliveTime(),
                null == threadPoolParam.getUnit()? TimeUnit.SECONDS:threadPoolParam.getUnit(),
                null == threadPoolParam.getWorkQueue()? new LinkedBlockingQueue() :threadPoolParam.getWorkQueue(),
                null == threadPoolParam.getThreadFactory()?
                        new ThreadFactoryBuilder().setNameFormat("default-thread-%d").build():threadPoolParam.getThreadFactory());
        if (CollectionUtils.isEmpty(threadPoolParam.getParamList())){
            return result;
        }
        try {
            Iterator<ThreadPoolParamDTO.ThreadParam> iterator = threadPoolParam.getParamList().iterator();
            CountDownLatch countDownLatch =new CountDownLatch(threadPoolParam.getParamList().size() - 1);
            while (iterator.hasNext()) {
                ThreadPoolParamDTO.ThreadParam threadParam = iterator.next();
                threadPoolExecutor.execute(new InvokeRunnable(countDownLatch,threadParam, result));

            }
        }catch (Exception e ){
            logger.error("线程池调用失败：{}", JSON.toJSONString(e.getMessage()));
        }

        threadPoolExecutor.shutdown();
        return result;
    }

}
