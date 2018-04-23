package com.jd.xn.clinet.internal.cluster;

import java.util.List;

/**
 * @author lijizhen1@jd.com
 * @date 2018/4/23 8:53
 */
public class ApiConfig {
    private String user;
    private ApiModle modle;
    private List<ApiRule> rules;


    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public ApiModle getModle() {
        return modle;
    }

    public void setModle(ApiModle modle) {
        this.modle = modle;
    }

    public List<ApiRule> getRules() {
        return rules;
    }

    public void setRules(List<ApiRule> rules) {
        this.rules = rules;
    }
}
