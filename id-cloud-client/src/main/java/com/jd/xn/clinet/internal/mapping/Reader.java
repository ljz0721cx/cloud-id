package com.jd.xn.clinet.internal.mapping;

import com.jd.xn.clinet.ApiException;

import java.util.List;

/**
 * 格式转换器。
 *
 * @author lijizhen1@jd.com
 * @date 2018/4/17 9:24
 */
public abstract interface Reader {

    /**
     * 判断返回结果是否包含指定的属性。
     *
     * @param name 属性名称
     * @return true/false
     */
    public abstract boolean hasReturnField(Object name);

    /**
     * 读取单个基本对象。
     *
     * @param name 属性名称
     * @return
     */
    public abstract Object getPrimitiveObject(Object name);

    /**
     * 读取单个自定义对象。
     *
     * @param name 映射名称
     * @param type 映射类型
     * @return 映射类型的实例
     * @throws ApiException
     */
    public abstract Object getObject(Object name, Class<?> type)
            throws ApiException;

    /**
     * 读取多个对象的值。
     *
     * @param listName 列表名称
     * @param itemName 映射名称
     * @param subType  嵌套映射类型
     * @return 嵌套映射类型实例列表
     * @throws ApiException
     */
    public abstract List<?> getListObjects(Object listName, Object itemName, Class<?> subType)
            throws ApiException;
}