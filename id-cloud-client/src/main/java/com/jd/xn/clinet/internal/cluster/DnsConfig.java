package com.jd.xn.clinet.internal.cluster;

import com.jd.xn.clinet.utils.JdUtils;

import java.net.URL;
import java.util.*;

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
     * 解析DNS配置
     *
     * @param json
     * @return
     */
    public static DnsConfig parse(String json) {
        DnsConfig dnsConfig = new DnsConfig();
        Map<?, ?> root = (Map<?, ?>) JdUtils.jsonToObject(json);
        for (Object configType : root.keySet()) {
            if ("config".equals(configType)) {
                dnsConfig.globalMap = new HashMap<String, String>();
                Map<?, ?> globalInfo = (Map<?, ?>) root.get(configType);
                for (Object key : globalInfo.keySet()) {
                    dnsConfig.globalMap.put(String.valueOf(key), String.valueOf(globalInfo.get(key)));
                }
            } else if ("env".equals(configType)) {
                Map<?, ?> envInfos = (Map<?, ?>) root.get(configType);
                dnsConfig.envMap = new HashMap<String, List<EnvConfig>>();
                for (Object envName : envInfos.keySet()) {
                    Map<?, ?> envInfo = (Map<?, ?>) envInfos.get(envName);
                    List<EnvConfig> envConfigs = new ArrayList<EnvConfig>();

                    for (Object domainName : envInfo.keySet()) {
                        Map<?, ?> domainInfo = (Map<?, ?>) envInfo.get(domainName);
                        EnvConfig envConfig = new EnvConfig();
                        envConfig.setDomain(String.valueOf(domainName));
                        envConfig.setProtocol(String.valueOf(domainInfo.get("proto")));
                        List<?> vipInfos = (List<?>) domainInfo.get("vip");
                        List<VipRule> vipRules = new ArrayList<VipRule>();
                        for (Object vipInfo : vipInfos) {
                            String[] vipInfoTmp = vipInfo.toString().split("\\|");
                            VipRule vipRule = new VipRule();
                            vipRule.setVip(vipInfoTmp[0]);
                            vipRule.setWeight(Double.parseDouble(vipInfoTmp[1]));
                            vipRules.add(vipRule);
                        }
                        envConfig.setVipRules(vipRules);
                        envConfigs.add(envConfig);
                    }
                    dnsConfig.envMap.put(String.valueOf(envName), envConfigs);
                }
            } else if ("api".equals(configType)) {
                dnsConfig.apiMap = new HashMap<String, ApiConfig>();
                Map<?, ?> apiInfos = (Map<?, ?>) root.get(configType);
                for (Object apiName : apiInfos.keySet()) {
                    Map<?, ?> apiInfo = (Map<?, ?>) apiInfos.get(apiName);
                    ApiConfig apiConfig = new ApiConfig();
                    apiConfig.setUser(String.valueOf(apiInfo.get("user")));
                    if (apiInfo.get("modle") != null) {
                        ApiModle apiModle = new ApiModle();
                        String modle = String.valueOf(apiInfo.get("modle"));
                        apiModle.setField(modle.split("\\|")[1]);
                        apiModle.setModle(modle.split("\\|")[0]);
                        apiConfig.setModle(apiModle);
                    }
                    List<ApiRule> apiRules = new ArrayList<ApiRule>();
                    List<?> apiRuleInfos = (List<?>) apiInfo.get("rule");
                    for (Object apiRuleInfo : apiRuleInfos) {
                        String[] apiRuleInfoTmp = apiRuleInfo.toString().split("\\|");
                        ApiRule apiRule = new ApiRule();
                        apiRule.setName(apiRuleInfoTmp[0]);
                        apiRule.setWeight(Double.parseDouble(apiRuleInfoTmp[1]));
                        apiRules.add(apiRule);
                    }
                    apiConfig.setRules(apiRules);
                    dnsConfig.apiMap.put(String.valueOf(apiName), apiConfig);
                }
            } else if ("user".equals(configType)) {
                dnsConfig.userMap = new HashMap<String, Map<String, String>>();
                Map<?, ?> userInfos = (Map<?, ?>) root.get(configType);
                for (Object routeName : userInfos.keySet()) {
                    Map<?, ?> envInfos = (Map<?, ?>) userInfos.get(routeName);
                    Map<String, String> tags = new HashMap<String, String>();
                    for (Object envName : envInfos.keySet()) {
                        List<?> tagInfos = (List<?>) envInfos.get(envName);
                        for (Object tagName : tagInfos) {
                            tags.put(String.valueOf(tagName), String.valueOf(envName));
                        }
                    }
                    dnsConfig.userMap.put(String.valueOf(routeName), tags);
                }
            } else if ("modle".equals(configType)) {
                dnsConfig.modleMap = new HashMap<String, Integer>();
                Map<?, ?> modleInfo = (Map<?, ?>) root.get(configType);
                for (Object key : modleInfo.keySet()) {
                    dnsConfig.modleMap.put(String.valueOf(key), Integer.parseInt(modleInfo.get(key).toString()));
                }
            }
        }
        return dnsConfig;
    }

    public int getRefreshInterval() {
        if (globalMap == null) {
            return 15;
        }
        String tmp = globalMap.get("interval");
        if (tmp != null) {
            return Integer.parseInt(tmp);
        } else {
            return 15;// 默认15分钟刷新一次
        }
    }
}
