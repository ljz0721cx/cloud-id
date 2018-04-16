package com.jd.xn.clinet;

/**
 * @author lijizhen1@jd.com
 * @date 2018/4/16 13:59
 */
public abstract interface JdClient {
    /**
     * 调用执行
     * @param paramTFRequest
     * @param <T>
     * @return
     * @throws ApiException
     */
    public abstract <T extends TFResponse> T execute(TFRequest<T> paramTFRequest)
            throws ApiException;

    /**
     *调用执行
     * @param paramTFRequest
     * @param paramString
     * @param <T>
     * @return
     * @throws ApiException
     */
    public abstract <T extends TFResponse> T execute(TFRequest<T> paramTFRequest, String paramString)
            throws ApiException;
}