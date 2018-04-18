package com.jd.xn.clinet;

import com.jd.xn.clinet.internal.parser.json.ObjectJsonParser;
import com.jd.xn.clinet.internal.parser.xml.ObjectXmlParser;
import com.jd.xn.clinet.utils.JdHashMap;
import com.jd.xn.clinet.utils.RequestParametersHolder;

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
        JdHashMap hashMap = new JdHashMap(request.getTextParams());
        requestHolder.setApplicationParams(hashMap);

        //添加协议级请求参数
        JdHashMap protocalMustParams = new JdHashMap();
        protocalMustParams.put(Constants.METHOD, request.getApiMethodName());
        protocalMustParams.put(Constants.APP_KEY, appKey);
        protocalMustParams.put(Constants.VERSION, "1.0");
        Long timestamp = request.getTimestamp();
        if (null == timestamp) {
            timestamp = System.currentTimeMillis();
        }
        protocalMustParams.put(Constants.TIMESTAMP,timestamp);
        requestHolder.setProtocalOptParams(protocalMustParams);

        JdHashMap protocalOptParams =new JdHashMap();
        protocalOptParams.put(Constants.FORMAT, format);
        protocalOptParams.put(Constants.SIGN_METHOD, signMethod);
        protocalOptParams.put(Constants.SESSION, session);
        protocalOptParams.put(Constants.PARTNER_ID, getSdkVersion());
        protocalOptParams.put(Constants.TARGET_APP_KEY, request.getTargetAppKey());

        //添加签名参数
        protocalMustParams.put(Constants.SIGN, TaobaoUtils.signTopRequest(requestHolder, appSecret, signMethod));



        return null;
    }


    protected String getSdkVersion() {
        if(isHttpDnsEnabled){
            return Constants.SDK_VERSION_HTTPDNS;
        }
        return Constants.SDK_VERSION;
    }
}
