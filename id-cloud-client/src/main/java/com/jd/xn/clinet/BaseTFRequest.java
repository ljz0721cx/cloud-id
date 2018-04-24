package com.jd.xn.clinet;

import com.jd.xn.clinet.utils.JdHashMap;

import java.util.Map;

/**
 * 基础TOP请求类，存放一些通用的请求参数。
 * 注：这个类不能随意增加get/set/is开头类的方法，否则会有可能和API业务级的参数冲突。
 *
 * @author lijizhen1@jd.com
 * @date 2018/4/24 18:33
 */
public abstract class BaseTFRequest<T extends TFResponse> implements TFRequest<T> {
    protected Map<String, String> headerMap; // HTTP请求头参数
    protected JdHashMap udfParams; // 自定义表单参数
    protected Long timestamp; // 请求时间戳
    protected String targetAppKey; // 请求目标AppKey
    protected String topMixParams; // 指定哪个入参是混淆参数
    protected String session; // 使用批量API调用时，每个API可以使用不同的授权码，普通API调用忽略此属性
    protected int order; // 标示该API在批量API调用中的顺序

    /**
     * 添加URL自定义请求参数。
     */
    public void putOtherTextParam(String key, String value) {
        if (this.udfParams == null) {
            this.udfParams = new JdHashMap();
        }
        this.udfParams.put(key, value);
    }

    @Override
    public Map<String, String> getHeaderMap() {
        if (this.headerMap == null) {
            this.headerMap = new JdHashMap();
        }
        return this.headerMap;
    }

    public void setHeaderMap(Map<String, String> headerMap) {
        this.headerMap = headerMap;
    }

    /**
     * 添加头部自定义请求参数。
     */
    public void putHeaderParam(String key, String value) {
        getHeaderMap().put(key, value);
    }

    @Override
    public Long getTimestamp() {
        return this.timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String getTargetAppKey() {
        return this.targetAppKey;
    }

    public void setTargetAppKey(String targetAppKey) {
        this.targetAppKey = targetAppKey;
    }

    public String getTopMixParams() {
        return this.topMixParams;
    }

    public void setTopMixParams(String topMixParams) {
        this.topMixParams = topMixParams;
    }

    @Override
    public String getBatchApiSession() {
        return this.session;
    }

    @Override
    public void setBatchApiSession(String session) {
        this.session = session;
    }

    @Override
    public int getBatchApiOrder() {
        return this.order;
    }

    @Override
    public void setBatchApiOrder(int order) {
        this.order = order;
    }
}
