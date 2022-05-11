package com.yqwl.datamiddle.realtime.enums;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target(ElementType.TYPE)
@Retention(RUNTIME)
public @interface TableName {
    /**
     * 实体对应的表名
     */
    String value() default "";
}
