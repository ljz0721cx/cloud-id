package com.jd.xn.clinet;

import com.jd.xn.clinet.internal.cluster.ClusterManager;
import com.jd.xn.clinet.internal.cluster.DnsConfig;
import com.jd.xn.clinet.internal.parser.json.ObjectJsonParser;
import com.jd.xn.clinet.internal.parser.xml.ObjectXmlParser;
import com.jd.xn.clinet.utils.*;
import com.jd.xn.clinet.utils.files.FileItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URL;
import java.util.Map;

/**
 * 默认的client端
 * 只支持xml和json的传输方式
 *
 * @author lijizhen1@jd.com
 * @date 2018/4/16 14:45
 */
public class DefaultClient implements JdClient {
    private static final Logger logger = LoggerFactory.getLogger(DefaultClient.class);
    protected String serverUrl;
    protected String appKey;
    protected String appSecret;
    protected String format = "json";
    protected String signMethod = "hmac";
    // 默认连接超时时间为15秒
    protected int connectTimeout = 15000;
    //默认响应超时时间为30秒
    protected int readTimeout = 30000;
    //是否在客户端校验请求
    protected boolean needCheckRequest = true;
    //是否对响应结果进行解释
    protected boolean needEnableParser = true;
    //是否采用精简化的JSON返回
    protected boolean useSimplifyJson = false;
    //是否启用响应GZIP压缩
    protected boolean useGzipEncoding = true;
    //是否启用的httpdns
    private boolean isHttpDnsEnabled = false;
    //原始请求host
    private String originalHttpHost = null;
    private boolean topHttpDnsHost;

    public DefaultClient(String serverUrl,
                         String appKey,
                         String appSecret) {
        this.serverUrl = serverUrl;
        this.appKey = appKey;
        this.appSecret = appSecret;
    }

    public DefaultClient(String serverUrl,
                         String appKey,
                         String appSecret,
                         String format) {
        this(serverUrl, appKey, appSecret);
        this.format = format;
    }

    public DefaultClient(String serverUrl,
                         String appKey,
                         String appSecret,
                         String format,
                         int connectTimeout,
                         int readTimeout) {
        this(serverUrl, appKey, appSecret, format);
        this.connectTimeout = connectTimeout;
        this.readTimeout = readTimeout;
    }

    public DefaultClient(String serverUrl,
                         String appKey,
                         String appSecret,
                         String format,
                         int connectTimeout,
                         int readTimeout,
                         String signMethod) {
        this(serverUrl, appKey, appSecret, format, connectTimeout, readTimeout);
        this.signMethod = signMethod;
    }

    /**
     * 执行调用
     *
     * @param request
     * @param <T>
     * @return
     * @throws ApiException
     */
    @Override
    public <T extends TFResponse> T execute(TFRequest<T> request) throws ApiException {
        return execute(request, null);
    }

    /**
     * 执行需要绑定回话的调用
     *
     * @param request
     * @param session
     * @param <T>
     * @return
     * @throws ApiException
     */
    @Override
    public <T extends TFResponse> T execute(TFRequest<T> request, String session) throws ApiException {
        return _execute(request, session);
    }

    private <T extends TFResponse> T _execute(
            TFRequest<T> request,
            String session) throws ApiException {
        long start = System.currentTimeMillis();
        //构建响应解释器
        JdParser parser = null;
        if (this.needEnableParser) {
            if ("xml".equals(this.format)) {
                parser = new ObjectXmlParser(request.getResponseClass());
            } else {
                parser = new ObjectJsonParser(request.getResponseClass(),
                        this.useSimplifyJson);
            }
        }
        //本地校验请求参数
        if (this.needCheckRequest) {
            try {
                request.check();
            } catch (ApiRuleException e) {
                T localResponse = null;
                try {
                    localResponse = request.getResponseClass().newInstance();
                } catch (Exception xe) {
                    throw new ApiException(xe);
                }
                localResponse.setErrorCode(e.getSubErrCode());
                localResponse.setMsg(e.getErrMsg());
                return localResponse;
            }
        }
        RequestParametersHolder requestHolder = new RequestParametersHolder();
        JdHashMap appParams = new JdHashMap(request.getTextParams());
        requestHolder.setApplicationParams(appParams);

        //添加协议级请求参数
        JdHashMap protocalMustParams = new JdHashMap();
        protocalMustParams.put(Constants.METHOD, request.getApiMethodName());
        protocalMustParams.put(Constants.APP_KEY, appKey);
        protocalMustParams.put(Constants.VERSION, "1.0");
        Long timestamp = request.getTimestamp();
        if (null == timestamp) {
            timestamp = System.currentTimeMillis();
        }
        protocalMustParams.put(Constants.TIMESTAMP, timestamp);
        requestHolder.setProtocalOptParams(protocalMustParams);

        JdHashMap protocalOptParams = new JdHashMap();
        protocalOptParams.put(Constants.FORMAT, format);
        protocalOptParams.put(Constants.SIGN_METHOD, signMethod);
        protocalOptParams.put(Constants.SESSION, session);
        protocalOptParams.put(Constants.PARTNER_ID, getSdkVersion());
        protocalOptParams.put(Constants.TARGET_APP_KEY, request.getTargetAppKey());
        try {
            //添加签名参数
            protocalMustParams.put(Constants.SIGN, SecurityUtil.signTopRequest(requestHolder, appSecret, signMethod));
            //获得真实的请求地址
            String realServerUrl = getServerUrl(this.serverUrl, request.getApiMethodName(), session, appParams);
            String sysMustQuery = WebUtils.buildQuery(requestHolder.getProtocalMustParams(), Constants.CHARSET_UTF8);
            String sysOptQuery = WebUtils.buildQuery(requestHolder.getProtocalOptParams(), Constants.CHARSET_UTF8);
            //构建请求全路径
            String fullUrl = WebUtils.buildRequestUrl(realServerUrl, sysMustQuery, sysOptQuery);
            String rsp = null;
            // 是否需要压缩响应
            if (this.useGzipEncoding) {
                //默认gzip
                request.getHeaderMap().put(Constants.ACCEPT_ENCODING, Constants.CONTENT_ENCODING_GZIP);
            }
            if (getTopHttpDnsHost() != null) {
                request.getHeaderMap().put(Constants.TOP_HTTP_DNS_HOST, getTopHttpDnsHost());
            }
            // 是否需要上传文件
            if (request instanceof JdUploadRequest) {
                JdUploadRequest<T> uRequest = (JdUploadRequest<T>) request;
                //清除空值
                Map<String, FileItem> fileParams = JdUtils.cleanupMap(uRequest.getFileParams());
                rsp = WebUtils.doPost(fullUrl, appParams, fileParams, Constants.CHARSET_UTF8, connectTimeout, readTimeout, request.getHeaderMap());
            } else {
                rsp = WebUtils.doPost(fullUrl, appParams, Constants.CHARSET_UTF8, connectTimeout, readTimeout, request.getHeaderMap());
            }
            requestHolder.setResponseBody(rsp);
        } catch (IOException e) {
            logger.error(appKey, request.getApiMethodName(), serverUrl, requestHolder.getAllParams(), System.currentTimeMillis() - start, e.toString());
            throw new ApiException(e);
        }
        T tRsp = null;
        if (this.needEnableParser) {
            tRsp = (T) parser.parse(requestHolder.getResponseBody());
            tRsp.setBody(requestHolder.getResponseBody());
        } else {
            try {
                tRsp = request.getResponseClass().newInstance();
                tRsp.setBody(requestHolder.getResponseBody());
            } catch (Exception e) {
                throw new ApiException(e);
            }
        }
        tRsp.setParams(appParams);
        if (!tRsp.isSuccess()) {
            logger.error(appKey, request.getApiMethodName(), serverUrl, requestHolder.getAllParams(), System.currentTimeMillis() - start, tRsp.getBody());
        }
        return tRsp;
    }

    private String getTopHttpDnsHost() {
        if (isHttpDnsEnabled) {
            return originalHttpHost;
        }
        return null;
    }


    protected String getSdkVersion() {
        if (isHttpDnsEnabled) {
            return Constants.SDK_VERSION_HTTPDNS;
        }
        return Constants.SDK_VERSION;
    }


    /**
     * 是否在客户端校验请求参数。
     */
    public void setNeedCheckRequest(boolean needCheckRequest) {
        this.needCheckRequest = needCheckRequest;
    }

    /**
     * 是否把响应字符串解释为对象。
     */
    public void setNeedEnableParser(boolean needEnableParser) {
        this.needEnableParser = needEnableParser;
    }

    /**
     * 是否采用标准化的JSON格式返回。
     */
    public void setUseSimplifyJson(boolean useSimplifyJson) {
        this.useSimplifyJson = useSimplifyJson;
    }


    /**
     * 是否忽略HTTPS证书校验。
     */
    public void setIgnoreSSLCheck(boolean ignore) {
        WebUtils.setIgnoreSSLCheck(ignore);
    }

    /**
     * 是否启用响应GZIP压缩
     */
    public void setUseGzipEncoding(boolean useGzipEncoding) {
        this.useGzipEncoding = useGzipEncoding;
    }

    /**
     * 设置API请求的连接超时时间，默认为15秒。
     */
    public void setConnectTimeout(int connectTimeout) {
        this.connectTimeout = connectTimeout;
    }

    /**
     * 设置API请求的读超时时间，默认为30秒。
     */
    public void setReadTimeout(int readTimeout) {
        this.readTimeout = readTimeout;
    }

    /**
     * 启用http dns
     */
    public void enableHttpDns() {
        WebUtils.setIgnoreHostCheck(true);
        setHttpDnsHost(serverUrl);
        ClusterManager.initRefreshThread(appKey, appSecret);
        isHttpDnsEnabled = true;
    }

    private void setHttpDnsHost(String serverUrl) {
        if (serverUrl == null || serverUrl.isEmpty()) {
            return;
        }
        try {
            URL url = new URL(serverUrl);
            originalHttpHost = url.getHost();
        } catch (Exception e) {
            throw new RuntimeException("error serverUrl:" + serverUrl, e);
        }
    }

    /**
     * 获得请求的服务URL
     *
     * @param serverUrl
     * @param apiName
     * @param session
     * @param appParams
     * @return
     */
    public String getServerUrl(String serverUrl, String apiName, String session, JdHashMap appParams) {
        if (isHttpDnsEnabled) {
            DnsConfig dnsConfig = ClusterManager.GetCacheDnsConfigFrom();
            if (dnsConfig == null) {
                return serverUrl;
            } else {
                return dnsConfig.getVipUrl(serverUrl);
            }
        }
        return serverUrl;
    }

    public String getAppKey() {
        return appKey;
    }


}
