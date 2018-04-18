package com.jd.xn.clinet.utils;

import java.util.HashMap;
import java.util.Map;

/**
 * API请求参数容器。
 *
 * @author lijizhen1@jd.com
 * @date 2018/4/18 17:08
 */
public class RequestParametersHolder {
    private String requestUrl;
    private String responseBody;
    private JdHashMap protocalMustParams;
    private JdHashMap protocalOptParams;
    private JdHashMap applicationParams;

    public String getRequestUrl() {
        return this.requestUrl;
    }

    public void setRequestUrl(String requestUrl) {
        this.requestUrl = requestUrl;
    }

    public String getResponseBody() {
        return this.responseBody;
    }

    public void setResponseBody(String responseBody) {
        this.responseBody = responseBody;
    }

    public JdHashMap getProtocalMustParams() {
        return this.protocalMustParams;
    }

    public void setProtocalMustParams(JdHashMap protocalMustParams) {
        this.protocalMustParams = protocalMustParams;
    }

    public JdHashMap getProtocalOptParams() {
        return this.protocalOptParams;
    }

    public void setProtocalOptParams(JdHashMap protocalOptParams) {
        this.protocalOptParams = protocalOptParams;
    }

    public JdHashMap getApplicationParams() {
        return this.applicationParams;
    }

    public void setApplicationParams(JdHashMap applicationParams) {
        this.applicationParams = applicationParams;
    }

    public Map<String, String> getAllParams() {
        Map<String, String> params = new HashMap<String, String>();
        if (protocalMustParams != null && !protocalMustParams.isEmpty()) {
            params.putAll(protocalMustParams);
        }
        if (protocalOptParams != null && !protocalOptParams.isEmpty()) {
            params.putAll(protocalOptParams);
        }
        if (applicationParams != null && !applicationParams.isEmpty()) {
            params.putAll(applicationParams);
        }
        return params;
    }
}
