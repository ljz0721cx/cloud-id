package com.jd.xn.clinet;

import java.util.Map;

/**
 * @author lijizhen1@jd.com
 * @date 2018/4/16 14:03
 */
public abstract interface TFRequest<T extends TFResponse> {

    /**
     * 请求api方法名称
     *
     * @return
     */
    public abstract String getApiMethodName();

    /**
     * 获得请求参数
     *
     * @return
     */
    public abstract Map<String, String> getTextParams();

    /**
     * 获得timestamp
     *
     * @return
     */
    public abstract Long getTimestamp();

    /**
     * 获取目标的appkey
     *
     * @return
     */
    public abstract String getTargetAppKey();

    /**
     * 获得响应的类类型
     *
     * @return
     */
    public abstract Class<T> getResponseClass();

    /**
     * 获得请求的头文件
     *
     * @return
     */
    public abstract Map<String, String> getHeaderMap();

    /**
     * 校验请求参数是否合法
     *
     * @throws ApiRuleException
     */
    public abstract void check()
            throws ApiRuleException;

    public abstract String getBatchApiSession();

    public abstract void setBatchApiSession(String paramString);

    public abstract int getBatchApiOrder();

    public abstract void setBatchApiOrder(int paramInt);
}
