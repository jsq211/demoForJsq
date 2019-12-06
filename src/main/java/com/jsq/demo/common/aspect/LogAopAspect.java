package com.jsq.demo.common.aspect;

import com.alibaba.fastjson.JSON;
import com.jsq.demo.common.annotation.LoggerAnnotation;
import com.jsq.demo.common.utils.ThreadLocalUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.framework.AopContext;
import org.springframework.stereotype.Component;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.util.Arrays;

/**
 * 日志打印注解
 * @author jsq
 */
@Aspect
@Component
public class LogAopAspect {

    private static final Logger logger = LoggerFactory.getLogger(LogAopAspect.class);

    @Around("@annotation(loggerAnnotation)")
    public Object getLogMessage(ProceedingJoinPoint joinPoint, LoggerAnnotation loggerAnnotation) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        String methodName = signature.getDeclaringTypeName() + "." + signature.getName();
        logger.info("LogAopAspect----注解获取入参信息---" + "method [" + methodName +"ms, 入参参数:" +
                JSON.toJSONString(joinPoint.getArgs()));
        long startTime = System.currentTimeMillis();
        Object obj = null;
        try {
            obj = joinPoint.proceed();
        } catch (Exception e) {
            throw e;
        }
        long endTime = System.currentTimeMillis();
        logger.info("LogAopAspect----注解获取出参信息---" + "method [" + methodName +"]，耗时时间: "+ (endTime - startTime) + "ms, 出参参数:" +
                JSON.toJSONString(obj));
        return obj;
    }

    @Around("@annotation(currentRollbackAnnotation)")
    public Object currentTranslation(ProceedingJoinPoint joinPoint, LoggerAnnotation currentRollbackAnnotation) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        String methodName = signature.getDeclaringTypeName() + "." + signature.getName();
        logger.info("currentTranslation----获取当前事物方法名---："+ methodName +"ms, 入参参数:" +
                JSON.toJSONString(joinPoint.getArgs()));
        AopContext.currentProxy();
        Object obj = null;
        try {
            obj = joinPoint.proceed();
        } catch (Exception e) {
            logger.error("事物执行失败，回滚当前事物 ，错误信息为：{}",JSON.toJSON(e));
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
        }
        return obj;
    }

    /**
     * 监控dao..*DAO包及其子包的所有public方法
     */
    @Pointcut("execution(* com.jsq.demo.dao.*DAO.*(..))")
    private void sqlPointCutMethod() {
    }

    /**
     * sql耗时日志
     * @param pjp
     * @return
     * @throws Throwable
     */
    @Around("sqlPointCutMethod()")
    public Object doAroundSql(ProceedingJoinPoint pjp) throws Throwable {
        long begin = System.currentTimeMillis();
        Object obj = pjp.proceed();
        long end = System.currentTimeMillis();

        logger.info("调用Mapper方法：{}，参数：{}，执行耗时：{}毫秒",
                JSON.toJSONString(pjp.getSignature()), Arrays.toString(pjp.getArgs()),
                (end - begin) / 1000000);
        return obj;
    }

}



