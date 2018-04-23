package com.jd.xn.clinet.utils;

import com.jd.xn.clinet.Constants;
import com.jd.xn.clinet.utils.files.FileItem;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 * 网络工具类
 *
 * @author lijizhen1@jd.com
 * @date 2018/4/23 9:25
 */
public abstract class WebUtils {
    private static final String DEFAULT_CHARSET = Constants.CHARSET_UTF8;
    private static boolean ignoreSSLCheck = true; // 忽略SSL检查
    private static boolean ignoreHostCheck = true; // 忽略HOST检查


    public static class TrustAllTrustManager implements X509TrustManager {

        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return null;
        }

        @Override
        public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        }

        @Override
        public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        }

    }

    private WebUtils() {
    }

    public static void setIgnoreSSLCheck(boolean ignoreSSLCheck) {
        WebUtils.ignoreSSLCheck = ignoreSSLCheck;
    }

    public static void setIgnoreHostCheck(boolean ignoreHostCheck) {
        WebUtils.ignoreHostCheck = ignoreHostCheck;
    }

    /**
     * 执行HTTP POST请求。
     *
     * @param url    请求地址
     * @param params 请求参数
     * @return 响应字符串
     */
    public static String doPost(String url,
                                Map<String, String> params,
                                int connectTimeout,
                                int readTimeout) throws IOException {
        return doPost(url, params, DEFAULT_CHARSET, connectTimeout, readTimeout);
    }

    /**
     * 执行HTTP POST请求。
     *
     * @param url     请求地址
     * @param params  请求参数
     * @param charset 字符集，如UTF-8, GBK, GB2312
     * @return 响应字符串
     */
    public static String doPost(String url,
                                Map<String, String> params,
                                String charset,
                                int connectTimeout,
                                int readTimeout) throws IOException {
        return doPost(url, params, charset, connectTimeout, readTimeout, null);
    }

    public static String doPost(String url,
                                Map<String, String> params,
                                String charset,
                                int connectTimeout,
                                int readTimeout,
                                Map<String, String> headerMap) throws IOException {
        String ctype = "application/x-www-form-urlencoded;charset=" + charset;
        String query = buildQuery(params, charset);
        byte[] content = {};
        if (query != null) {
            content = query.getBytes(charset);
        }
        return _doPost(url, ctype, content, connectTimeout, readTimeout, headerMap);
    }

    public static String doPost(String url,
                                String apiBody,
                                String charset,
                                int connectTimeout,
                                int readTimeout,
                                Map<String, String> headerMap) throws IOException {
        String ctype = "text/plain;charset=" + charset;
        byte[] content = apiBody.getBytes(charset);
        return _doPost(url, ctype, content, connectTimeout, readTimeout, headerMap);
    }

    /**
     * 执行HTTP POST请求。
     *
     * @param url     请求地址
     * @param ctype   请求类型
     * @param content 请求字节数组
     * @return 响应字符串
     */
    public static String doPost(String url,
                                String ctype,
                                byte[] content,
                                int connectTimeout,
                                int readTimeout) throws IOException {
        return _doPost(url, ctype, content, connectTimeout, readTimeout, null);
    }

    /**
     * 执行HTTP POST请求。
     *
     * @param url       请求地址
     * @param ctype     请求类型
     * @param content   请求字节数组
     * @param headerMap 请求头部参数
     * @return 响应字符串
     */
    public static String doPost(String url,
                                String ctype,
                                byte[] content,
                                int connectTimeout,
                                int readTimeout,
                                Map<String, String> headerMap) throws IOException {
        return _doPost(url, ctype, content, connectTimeout, readTimeout, headerMap);
    }


    private static String _doPost(String url,
                                  String ctype,
                                  byte[] content,
                                  int connectTimeout,
                                  int readTimeout,
                                  Map<String, String> headerMap) throws IOException {
        HttpURLConnection conn = null;
        OutputStream out = null;
        String rsp = null;
        try {
            conn = getConnection(new URL(url), Constants.METHOD_POST, ctype, headerMap);
            conn.setConnectTimeout(connectTimeout);
            conn.setReadTimeout(readTimeout);
            out = conn.getOutputStream();
            out.write(content);
            rsp = getResponseAsString(conn);
        } finally {
            if (out != null) {
                out.close();
            }
            if (conn != null) {
                conn.disconnect();
            }
        }
        return rsp;
    }

    /**
     * @param conn
     * @return
     */
    private static String getResponseAsString(HttpURLConnection conn) {
        String charset = getResponseCharset(conn.getContentType());
        //TODO 进行处理
        return null;
    }

    public static String getResponseCharset(String ctype) {
        String charset = DEFAULT_CHARSET;
        if (!StringUtils.isEmpty(ctype)) {
            String[] params = ctype.split(";");
            for (String param : params) {
                param = param.trim();
                if (param.startsWith("charset")) {
                    String[] pair = param.split("=", 2);
                    if (pair.length == 2) {
                        if (!StringUtils.isEmpty(pair[1])) {
                            charset = pair[1].trim();
                        }
                    }
                    break;
                }
            }
        }
        return charset;
    }


    /**
     * 获得请求连接
     *
     * @param url
     * @param method
     * @param ctype
     * @param headerMap
     * @return
     */
    private static HttpURLConnection getConnection(URL url,
                                                   String method,
                                                   String ctype,
                                                   Map<String, String> headerMap) throws IOException {
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        if (conn instanceof HttpsURLConnection) {
            HttpsURLConnection connHttps = (HttpsURLConnection) conn;
            if (ignoreSSLCheck) {
                try {
                    //获得TLS的容器
                    SSLContext ctx = SSLContext.getInstance("TLS");
                    ctx.init(null, new TrustManager[]{new TrustAllTrustManager()}, new SecureRandom());
                    connHttps.setSSLSocketFactory(ctx.getSocketFactory());
                    connHttps.setHostnameVerifier((hostname, session) -> true);
                } catch (Exception e) {
                    throw new IOException(e.toString());
                }
            } else {
                if (ignoreHostCheck) {
                    connHttps.setHostnameVerifier((hostname, session) -> true);
                }
            }
            conn = connHttps;
        }
        //
        conn.setRequestMethod(method);
        conn.setDoInput(true);
        conn.setDoOutput(true);
        if (headerMap != null && headerMap.get(Constants.TOP_HTTP_DNS_HOST) != null) {
            conn.setRequestProperty("Host", headerMap.get(Constants.TOP_HTTP_DNS_HOST));
        } else {
            conn.setRequestProperty("Host", url.getHost());
        }
        conn.setRequestProperty("Accept", "text/xml,text/javascript");
        conn.setRequestProperty("User-Agent", "top-sdk-java");
        conn.setRequestProperty("Content-Type", ctype);
        if (headerMap != null) {
            for (Entry<String, String> entry : headerMap.entrySet()) {
                if (!Constants.TOP_HTTP_DNS_HOST.equals(entry.getKey())) {
                    conn.setRequestProperty(entry.getKey(), entry.getValue());
                }
            }
        }
        return conn;
    }

    /**
     * 执行带文件上传的HTTP POST请求。
     *
     * @param url        请求地址
     * @param textParams 文本请求参数
     * @param fileParams 文件请求参数
     * @return 响应字符串
     */
    public static String doPost(String url, Map<String, String> textParams, Map<String, FileItem> fileParams, int connectTimeout, int readTimeout) throws IOException {
        if (fileParams == null || fileParams.isEmpty()) {
            return doPost(url, textParams, DEFAULT_CHARSET, connectTimeout, readTimeout);
        } else {
            return doPost(url, textParams, fileParams, DEFAULT_CHARSET, connectTimeout, readTimeout);
        }
    }

    public static String doPost(String url, Map<String, String> params, Map<String, FileItem> fileParams, String charset, int connectTimeout, int readTimeout) throws IOException {
        return doPost(url, params, fileParams, charset, connectTimeout, readTimeout, null);
    }

    /**
     * 执行带文件上传的HTTP POST请求。
     *
     * @param url        请求地址
     * @param textParams 文本请求参数
     * @param fileParams 文件请求参数
     * @param charset    字符集，如UTF-8, GBK, GB2312
     * @param headerMap  需要传递的header头，可以为空
     * @return 响应字符串
     */
    public static String doPost(String url, Map<String, String> textParams, Map<String, FileItem> fileParams, String charset,
                                int connectTimeout, int readTimeout, Map<String, String> headerMap) throws IOException {
        if (fileParams == null || fileParams.isEmpty()) {
            return doPost(url, textParams, charset, connectTimeout, readTimeout, headerMap);
        } else {
            return _doPostWithFile(url, textParams, fileParams, charset, connectTimeout, readTimeout, headerMap);
        }
    }

    /**
     * 绑定请求参数
     * a=123&b=12223
     *
     * @param params
     * @param charset
     * @return
     */
    public static String buildQuery(Map<String, String> params, String charset) throws IOException {
        if (params == null || params.isEmpty()) {
            return null;
        }
        StringBuilder query = new StringBuilder();
        Set<Entry<String, String>> entries = params.entrySet();
        boolean hasParam = false;
        for (Entry<String, String> entry : entries) {
            String name = entry.getKey();
            String value = entry.getValue();
            // 忽略参数名或参数值为空的参数
            if (StringUtils.areNotEmpty(name, value)) {
                //如果有多个参数使用&进行拼接
                if (hasParam) {
                    query.append("&");
                } else {
                    hasParam = true;
                }
                query.append(name).append("=").append(URLEncoder.encode(value, charset));
            }
        }
        return query.toString();
    }

    /**
     * 构建请求
     *
     * @param url
     * @param queries
     * @return
     */
    public static String buildRequestUrl(String url, String... queries) {
        if (queries == null || queries.length == 0) {
            return url;
        }
        StringBuilder newUrl = new StringBuilder(url);
        //是否包含？
        boolean hasQuery = url.contains("?");
        //有连接符？或者&
        boolean hasPrepend = url.endsWith("?") || url.endsWith("&");
        for (String query : queries) {
            if (!StringUtils.isEmpty(query)) {
                if (!hasPrepend) {
                    if (hasQuery) {
                        //已经有参数
                        newUrl.append("&");
                    } else {
                        //新参数还没有添加
                        newUrl.append("?");
                        hasQuery = true;
                    }
                }
                newUrl.append(query);
                hasPrepend = false;
            }
        }
        return newUrl.toString();
    }


}
