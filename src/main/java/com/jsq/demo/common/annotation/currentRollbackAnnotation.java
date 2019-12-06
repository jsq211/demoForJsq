package com.jsq.demo.common.annotation;

import java.lang.annotation.*;

/**
 * 事物注解 用于回滚当前所用方法
 * @author jsq
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface currentRollbackAnnotation {

}
