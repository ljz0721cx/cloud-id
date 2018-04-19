package com.jd.xn.clinet.utils;

import com.jd.xn.clinet.Constants;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.Map;

/**
 * 系统安全工具类。
 *
 * @author lijizhen1@jd.com
 * @date 2018/4/19 9:11
 */
public abstract class SecurityUtil {
    private static final byte[] IV_BYTES = "0102030405060708".getBytes();
    private static final String AES = "AES";
    private static String intranetIp;
    private static final String MAC_HMAC_MD5 = "HmacMD5";

    /**
     * 给TOP请求签名。
     *
     * @param requestHolder 所有字符型的TOP请求参数
     * @param secret        签名密钥
     * @param signMethod    signMethod 签名方法，目前支持：空（老md5)、md5, hmac_md5三种
     * @return 签名
     */
    public static String signTopRequest(RequestParametersHolder requestHolder, String secret, String signMethod)
            throws IOException {
        return signTopRequest(requestHolder.getAllParams(), null, secret, signMethod);
    }

    /**
     * 给TOP请求签名。
     *
     * @param params     所有字符型的TOP请求参数
     * @param body       请求主体内容
     * @param secret     签名密钥
     * @param signMethod 签名方法，目前支持：空（老md5)、md5, hmac_md5三种
     * @return 签名
     */
    public static String signTopRequest(Map<String, String> params, String body, String secret, String signMethod)
            throws IOException {
        // 第一步：检查参数是否已经排序
        String[] keys = (String[]) params.keySet().toArray(new String[0]);
        Arrays.sort(keys);
        // 第二步：把所有参数名和参数值串在一起
        StringBuilder query = new StringBuilder();
        if ("md5".equals(signMethod)) {
            query.append(secret);
        }
        for (String key : keys) {
            String value = (String) params.get(key);
            if (StringUtils.areNotEmpty(new String[]{key, value})) {
                query.append(key).append(value);
            }
        }
        // 第三步：把请求主体拼接在参数后面
        if (body != null) {
            query.append(body);
        }
        byte[] bytes;
        // 第四步：使用MD5/HMAC加密
        if ("hmac".equals(signMethod)) {
            //使用hmac进行加密
            bytes = encryptHMAC(query.toString(), secret);
        } else {
            //不是hmad使用hmac-256
            if ("hmac-sha256".equals(signMethod)) {
                bytes = encryptHMACSHA256(query.toString(), secret);
            } else {
                query.append(secret);
                //没有签名使用MD5加密方式
                bytes = encryptMD5(query.toString());
            }
        }
        return byte2hex(bytes);
    }

    /**
     * 把字节流转换为十六进制表示方式。
     *
     * @param bytes
     * @return
     */
    private static String byte2hex(byte[] bytes) {
        StringBuilder sign = new StringBuilder();
        for (int i = 0; i < bytes.length; i++) {
            String hex = Integer.toHexString(bytes[i] & 0xFF);
            if (hex.length() == 1) {
                sign.append("0");
            }
            sign.append(hex.toUpperCase());
        }
        return sign.toString();
    }

    /**
     * 对字符串采用UTF-8编码后，用MD5进行摘要。
     */
    public static byte[] encryptMD5(String data) throws IOException {
        return encryptMD5(data.getBytes(Constants.CHARSET_UTF8));
    }

    /**
     * 对字节流进行MD5摘要。
     */
    public static byte[] encryptMD5(byte[] data) throws IOException {
        byte[] bytes = null;
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            bytes = md.digest(data);
        } catch (GeneralSecurityException gse) {
            throw new IOException(gse.toString());
        }
        return bytes;
    }

    private static byte[] encryptHMACSHA256(String data, String secret) throws IOException {
        byte[] bytes = null;
        try {
            SecretKey secretKey = new SecretKeySpec(secret.getBytes(Constants.CHARSET_UTF8), "HmacSHA256");
            Mac mac = Mac.getInstance(secretKey.getAlgorithm());
            mac.init(secretKey);
            bytes = mac.doFinal(data.getBytes(Constants.CHARSET_UTF8));
        } catch (GeneralSecurityException gse) {
            throw new IOException(gse.toString());
        }
        return bytes;
    }

    private static byte[] encryptHMAC(String data, String secret) throws IOException {
        byte[] bytes = null;
        try {
            SecretKey secretKey = new SecretKeySpec(secret.getBytes(Constants.CHARSET_UTF8), "HmacMD5");
            Mac mac = Mac.getInstance(secretKey.getAlgorithm());
            mac.init(secretKey);
            bytes = mac.doFinal(data.getBytes(Constants.CHARSET_UTF8));
        } catch (GeneralSecurityException gse) {
            throw new IOException(gse.toString());
        }
        return bytes;
    }
}
