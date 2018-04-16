package com.jd.xn.clinet.internal.mapping;

import com.jd.xn.clinet.ApiException;

/**
 * @author lijizhen1@jd.com
 * @date 2018/4/16 18:06
 */
public abstract interface Converter {
    /**
     * @param paramString 需要转换的参数
     * @param paramClass  待转换的目标类
     * @param <T>
     * @return
     * @throws ApiException
     */
    public abstract <T> T toResponse(String paramString, Class<T> paramClass)
            throws ApiException;
}
