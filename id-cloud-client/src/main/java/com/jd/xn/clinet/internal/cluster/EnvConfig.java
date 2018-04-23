package com.jd.xn.clinet.internal.cluster;

import java.util.List;

/**
 * 环境配置
 * @author lijizhen1@jd.com
 * @date 2018/4/23 8:57
 */
public class EnvConfig {
    //域名
    private String domain;
    //协议schema
    private String protocol;
    private List<VipRule> vipRules;


    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public List<VipRule> getVipRules() {
        return vipRules;
    }

    public void setVipRules(List<VipRule> vipRules) {
        this.vipRules = vipRules;
    }
}
