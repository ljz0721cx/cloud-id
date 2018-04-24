package com.jd.xn.clinet.internal.cluster;

import com.jd.xn.clinet.ApiException;
import com.jd.xn.clinet.DefaultClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

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
    private static volatile Thread refreshThread = null;
    private static Set<String> terminationCodeSet = new HashSet<String>();

    static {
        terminationCodeSet.add("21");
        terminationCodeSet.add("22");
        terminationCodeSet.add("25");
        terminationCodeSet.add("28");
        terminationCodeSet.add("29");
    }

    public static DnsConfig GetCacheDnsConfigFrom() {
        return dnsConfig;
    }


    public static void initRefreshThread(String appKey, String appSecret) {
        initRefreshThread(new DefaultClient("http://gw.api.taobao.com/top/router/rest", appKey, appSecret));
    }

    public static void initRefreshThread(final DefaultClient client) {
        if (refreshThread == null) {
            synchronized (initLock) {
                if (refreshThread == null) {
                    try {
                        dnsConfig = getDnsConfigFromTop(client);
                    } catch (ApiException apiException) {
                        if (apiException.getErrCode() != null &&
                                terminationCodeSet.contains(apiException.getErrCode())) {
                            log.error("http dns server termination,errCode:" + apiException.getErrCode() + "," + apiException.getErrMsg());
                            return; // 如果HTTP DNS服务不存在，则退出守护线程
                        }
                    } catch (Exception e) {
                        log.error("get http dns config from top fail", e);
                    }
                    refreshThread = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            while (true) {
                                try {
                                    int refreshInterval = dnsConfig == null ? 1 : dnsConfig.getRefreshInterval();
                                    sleep(refreshInterval * 60 * 1000L);
                                    dnsConfig = getDnsConfigFromTop(client);
                                } catch (ApiException apiException) {
                                    if (apiException.getErrCode() != null && terminationCodeSet.contains(apiException.getErrCode())) {
                                        log.error("http dns server termination,errCode:" + apiException.getErrCode() + "," + apiException.getErrMsg());
                                        return; // 如果HTTP DNS服务不存在，则退出守护线程
                                    }
                                    log.error("get http dns config from top fail," + apiException.getErrCode() + "," + apiException.getErrMsg());
                                } catch (Exception e) {
                                    log.error("refresh http dns config from top fail," + e.getMessage(), e);
                                    sleep(3 * 1000L); // 出错则过3秒重试
                                }
                            }
                        }
                    });
                }
            }
        }
    }


    private static DnsConfig getDnsConfigFromTop(DefaultClient client) throws ApiException {
        HttpdnsGetRequest req = new HttpdnsGetRequest();
        HttpdnsGetResponse rsp = client.execute(req);
        if (rsp.isSuccess()) {
            return DnsConfig.parse(rsp.getResult());
        } else {
            throw new ApiException(rsp.getErrorCode(), rsp.getMsg());
        }
    }

    private static void sleep(long time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

}
