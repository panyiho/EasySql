package com.easysql.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 描述列的注解
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Column {

    String name();                   //数据库列保存的名字

    String property() default "";

    boolean isId() default false;    //是否为主键

    boolean autoGen() default true;  //是否为自增
}
