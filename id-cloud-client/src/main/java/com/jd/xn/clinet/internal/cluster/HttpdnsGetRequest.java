package com.jd.xn.clinet.internal.cluster;

import com.jd.xn.clinet.ApiRuleException;
import com.jd.xn.clinet.BaseTFRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * @author lijizhen1@jd.com
 * @date 2018/4/24 18:22
 */
public class HttpdnsGetRequest extends BaseTFRequest<HttpdnsGetResponse> {
    @Override
    public String getApiMethodName() {
        return "jd.httpdns.get";
    }

    @Override
    public Map<String, String> getTextParams() {
        return new HashMap<String, String>();
    }

    @Override
    public Class getResponseClass() {
        return HttpdnsGetResponse.class;
    }

    @Override
    public void check() throws ApiRuleException {

    }
}
