package com.jd.xn.clinet.internal.cluster;

import java.util.Map;

/**
 * @author lijizhen1@jd.com
 * @date 2018/4/19 9:42
 */
public class DnsConfig {
    private Map<String, String> globalMap;
    private Map<String, Integer> modleMap;
    private Map<String, ApiConfig> apiMap;
    private Map<String, List<EnvConfig>> envMap;
    private Map<String, Map<String, String>> userMap;


}
