package com.jd.xn.clinet.internal.cluster;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Random;

/**
 * 集群管理
 *
 * @author lijizhen1@jd.com
 * @date 2018/4/19 9:36
 */
public final class ClusterManager {
    private static final Logger log = LoggerFactory.getLogger(ClusterManager.class);

    private static final Random random = new Random();
    private static final Object initLock = new Object();
    private static volatile DnsConfig dnsConfig = null;


    public static DnsConfig GetCacheDnsConfigFrom() {
        return dnsConfig;
    }


}
