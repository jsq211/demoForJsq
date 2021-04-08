package com.jsq.component.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author jsq
 * created on 2021/2/3
 **/
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RedisCacheInput {
//    String database();
    String table();
    String inputKey();
    String outPutKey();
}
