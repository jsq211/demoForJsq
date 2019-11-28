package com.jsq.demo.common.annotation;

import java.lang.annotation.*;

/**
 * 日志切面信息
 * 通过该注解实现调用方法的出参、入参及异常信息日志自动打印
 * @author jsq
 */

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface LoggerAnnotation {

}
