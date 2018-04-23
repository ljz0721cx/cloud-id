package com.jd.xn.clinet.internal.cluster;

/**
 * vip规则
 * @author lijizhen1@jd.com
 * @date 2018/4/23 8:58
 */
public class VipRule extends  Weightable {
    private String vip;

    public String getVip() {
        return vip;
    }

    public void setVip(String vip) {
        this.vip = vip;
    }
}
