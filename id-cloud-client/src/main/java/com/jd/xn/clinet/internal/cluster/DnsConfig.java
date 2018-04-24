package com.jd.xn.clinet.internal.cluster;

import com.jd.xn.clinet.utils.JdUtils;

import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.Random;

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


    public String getVipUrl(String serverUrl) {
        if (envMap == null || globalMap == null || globalMap.get("def_env") == null
                || serverUrl == null || serverUrl.length() == 0) {
            return serverUrl;
        }
        List<EnvConfig> envConfigs = envMap.get(globalMap.get("def_env"));
        //环境配置为空
        if (envConfigs == null || envConfigs.isEmpty()) {
            return serverUrl;
        }
        try {
            return getBalancedUrl(serverUrl, envConfigs);
        } catch (Exception e) {
            return serverUrl;
        }
    }


    private String getBalancedUrl(String serverUrl, List<EnvConfig> envConfigs) throws Exception {
        URL uri = new URL(serverUrl);
        String host = uri.getHost();
        //请求协议
        String scheme = uri.getProtocol();
        for (EnvConfig envConfig : envConfigs) {
            //验证是否是本机host
            try {
                if (host.equalsIgnoreCase(envConfig.getDomain())) {
                    List<VipRule> vipRules = envConfig.getVipRules();
                    if (null == vipRules || vipRules.isEmpty()) {
                        return serverUrl;
                    }
                    Random random = new Random();
                    String vip = vipRules.get(random.nextInt(vipRules.size())).getVip();
                    //没有获得vip规则
                    if (vip == null || vip.length() == 0) {
                        return serverUrl;
                    }
                    //构建请求
                    StringBuilder urlBuilder = new StringBuilder();
                    urlBuilder.append(scheme).append("://").append(vip);
                    //添加对应的端口好
                    if (uri.getPort() > 0) {
                        urlBuilder.append(":").append(uri.getPort());
                    }
                    if (uri.getFile() != null) {
                        urlBuilder.append(uri.getFile());
                    }
                    return urlBuilder.toString();
                }
            } catch (Exception e) {
                //无效的url忽略
                continue;
            }

        }
        return serverUrl;
    }


    /**
     * 解析
     * @param json
     * @return
     */
    public static DnsConfig parse(String json) {
        DnsConfig dnsConfig = new DnsConfig();
        Map<?, ?> root = (Map<?, ?>) JdUtils.jsonToObject(json);
        return null;
    }
}
