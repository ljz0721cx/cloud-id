package com.jd.xn.clinet;

import com.jd.xn.clinet.internal.parser.xml.ObjectXmlParser;

/**
 * 默认的client端
 * 只支持xml和json的传输方式
 *
 * @author lijizhen1@jd.com
 * @date 2018/4/16 14:45
 */
public class DefaultClient implements JdClient {
    protected String serverUrl;
    protected String appKey;
    protected String appSecret;
    protected String format = "json";
    protected String signMethod = "hmac";
    protected int connectTimeout = 15000;
    protected int readTimeout = 30000;
    protected boolean needCheckRequest = true;
    protected boolean needEnableParser = true;
    protected boolean useSimplifyJson = false;
    protected boolean useGzipEncoding = true;
    private boolean isHttpDnsEnabled = false;
    private String originalHttpHost = null;

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
            String session) {
        long start = System.currentTimeMillis();
        JdParser parser = null;
        if (this.needEnableParser) {
            if ("xml".equals(this.format)) {
                parser = new ObjectXmlParser(request.getResponseClass());
            } else {
               /* parser = new ObjectJsonParser(request.getResponseClass(),
                        this.useSimplifyJson);*/
            }
        }
        return null;
    }

}
