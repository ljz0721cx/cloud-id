package com.jd.xn.clinet.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 * @author lijizhen1@jd.com
 * @date 2018/4/18 18:25
 */
public abstract class JdUtils {
    private static final byte[] IV_BYTES = "0102030405060708".getBytes();
    private static final String AES = "AES";
    private static String intranetIp;
    private static final String MAC_HMAC_MD5 = "HmacMD5";

    private JdUtils() {
    }

    /**
     * 清除字典中值为空的项。
     *
     * @param fileParams
     * @return
     */
    public static <V> Map<String, V> cleanupMap(Map<String, V> fileParams) {
        if (fileParams == null || fileParams.isEmpty()) {
            return null;
        }

        Map<String, V> result = new HashMap<String, V>(fileParams.size());
        Set<Entry<String, V>> entries = fileParams.entrySet();

        for (Entry<String, V> entry : entries) {
            if (entry.getValue() != null) {
                result.put(entry.getKey(), entry.getValue());
            }
        }
        return result;
    }
}
