package com.easysql.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 表描述的注解
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Table {

    String name();                  //表名

    String onCreated() default "";  //onCreate之后执行的Sql语句，可以添加需要执行的Sql语句，例如增加索引
}