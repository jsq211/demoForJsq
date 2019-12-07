package com.jsq.demo.common.annotation;


import com.jsq.demo.common.TransactionalRollbackConstants;

import java.lang.annotation.*;

/**
 * 事物注解 用于回滚当前所用方法
 * @author jsq
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface TransactionalRollback {
    int transactionDefinition() default TransactionalRollbackConstants.PROPAGATION_REQUIRED;

}
