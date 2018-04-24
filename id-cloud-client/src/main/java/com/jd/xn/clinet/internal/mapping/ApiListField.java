package com.jd.xn.clinet.internal.mapping;

/**
 * 数据结构列表属性注解。
 *
 * @author lijizhen1@jd.com
 * @date 2018/4/24 21:06
 */
public @interface ApiListField {
    /**
     * JSON列表属性映射名称
     **/
    public String value() default "";
}
