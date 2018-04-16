package com.jd.xn.clinet.utils;

import java.util.TimeZone;
import java.util.regex.Pattern;

/**
 * @author lijizhen1@jd.com
 * @date 2018/4/16 19:55
 */
public abstract class StringUtils {
    public static final String[] EMPTY_STRING_ARRAY = new String[0];
    //FIXME 设置时区为中国时区 需要考虑国外的服务器
    private static final TimeZone TZ_GMT8 = TimeZone.getTimeZone("GMT+8");
    private static final Pattern PATTERN_CIDR = Pattern.compile("^(\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3})/(\\d{1,2})$");
    private static final String QUOT = "&quot;";
    private static final String AMP = "&amp;";
    private static final String APOS = "&apos;";
    private static final String GT = "&gt;";
    private static final String LT = "&lt;";


    public static boolean isEmpty(String value) {
        int strLen;
        if ((value == null) || ((strLen = value.length()) == 0)) {
            return true;
        }
        for (int i = 0; i < strLen; i++) {
            if (!Character.isWhitespace(value.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    /**
     * 判断是否为数字
     *
     * @param obj
     * @return
     */
    public static boolean isNumeric(Object obj) {
        if (obj == null) {
            return false;
        }
        char[] chars = obj.toString().toCharArray();
        int length = chars.length;
        if (length < 1) {
            return false;
        }
        int i = 0;
        if ((length > 1) && (chars[0] == '-')) {}
        for (i = 1;
             i < length; i++) {
            if (!Character.isDigit(chars[i])) {
                return false;
            }
        }
        return true;
    }
}
