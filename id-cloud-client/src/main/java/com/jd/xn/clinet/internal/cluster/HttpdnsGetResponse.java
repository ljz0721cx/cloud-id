package com.jd.xn.clinet.internal.cluster;

import com.jd.xn.clinet.TFResponse;
import com.jd.xn.clinet.internal.mapping.ApiField;

/**
 * @author lijizhen1@jd.com
 * @date 2018/4/24 18:21
 */
public class HttpdnsGetResponse extends TFResponse {
    @ApiField("result")
    private String result;

    public String getResult() {
        return this.result;
    }

    public void setResult(String result) {
        this.result = result;
    }
}
