package com.dkm.isaaccommunity.service.aop;

import java.lang.annotation.*;

/**
 * 需要进行Lucene索引分析的标识注解
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface IndexAnnotation {
    //标识需要进行索引分析的对象类型
    String type() default "";

    //具体操作：添加、删除、修改
    String action()  default "";
}