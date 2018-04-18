package com.jd.xn.clinet.internal.mapping;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author lijizhen1@jd.com
 * @date 2018/4/18 15:05
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({java.lang.annotation.ElementType.FIELD})
public @interface ApiField
{
    public abstract String value();
}