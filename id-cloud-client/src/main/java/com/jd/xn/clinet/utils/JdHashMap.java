package com.jd.xn.clinet.utils;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author lijizhen1@jd.com
 * @date 2018/4/18 17:59
 */
public class JdHashMap extends HashMap<String, String> {
    public JdHashMap() {
    }

    public JdHashMap(Map<? extends String, ? extends String> m) {
        super(m);
    }

    public String put(String key, Object value) {
        String strValue;
        //对数据进行转换
        if (value == null) {
            strValue = null;
        } else if (value instanceof String) {
            strValue = (String) value;
        } else if (value instanceof Integer) {
            strValue = ((Integer) value).toString();
        } else if (value instanceof Long) {
            strValue = ((Long) value).toString();
        } else if (value instanceof Float) {
            strValue = ((Float) value).toString();
        } else if (value instanceof Double) {
            strValue = ((Double) value).toString();
        } else if (value instanceof Boolean) {
            strValue = ((Boolean) value).toString();
        } else if (value instanceof Date) {
            strValue = StringUtils.formatDateTime((Date) value);
        } else {
            strValue = value.toString();
        }
        return put(key, strValue);
    }
    @Override
    public String put(String key, String value) {
        if (StringUtils.areNotEmpty(key, value)) {
            return super.put(key, value);
        } else {
            return null;
        }
    }
}

