package com.jd.xn.clinet;

/**
 * @author lijizhen1@jd.com
 * @date 2018/4/16 16:41
 */
public abstract interface JdParser<T> {
    /**
     * @param paramString
     * @return
     * @throws ApiException
     */
    public abstract T parse(String paramString)
            throws ApiException;

    /**
     * @return
     * @throws ApiException
     */
    public abstract Class<T> getResponseClass()
            throws ApiException;
}