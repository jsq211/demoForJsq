package com.jsq.demo.common.utils;


import com.jsq.demo.manager.TransactionManager;
import org.springframework.transaction.TransactionStatus;

/**
 * 手动事物方法
 * @author jsq
 */
public class TransactionUtils {
    /**
     *  全局接受事务状态
     */
    private TransactionStatus transactionStatus;
    /**
     * 获取当前事物信息 class  method  args
     */
    private TransactionManager transactionManager;


}
