package com.interview.annotation;

import com.interview.common.HotKeyEnum;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author hjc
 * @version 1.0
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface HotKeyCache {
    /**
     * 热点缓存 key
     * @return
     */
    String key() default "";

    /**
     * 缓存类型
     *
     * @return
     */
    HotKeyEnum type();
}
