package com.jd.xn.clinet.internal.mapping;


import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author lijizhen1@jd.com
 * @date 2018/4/16 19:53
 */
public class Converters {
    public static boolean isCheckJsonType = false;
    private static final Object emptyCache = new Object();

    //基本的属性在类加载时候初始化
    private static final Map<String, Set<String>> baseProps = new HashMap();
    private static final Map<String, Object> fieldCache = new ConcurrentHashMap();
    private static final Map<String, Object> methodCache = new ConcurrentHashMap();


    static
    {
        baseProps.put(TFResponse.class.getName(), StringUtils.getClassProperties(TaobaoResponse.class, false));
        baseProps.put(QimenResponse.class.getName(), StringUtils.getClassProperties(QimenResponse.class, false));
        baseProps.put(AbstractQimenCloudResponse.class.getName(), StringUtils.getClassProperties(AbstractQimenCloudResponse.class, false));
    }
}
