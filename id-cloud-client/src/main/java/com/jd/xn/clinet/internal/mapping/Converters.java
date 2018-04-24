package com.jd.xn.clinet.internal.mapping;


import com.jd.xn.clinet.ApiException;
import com.jd.xn.clinet.TFResponse;
import com.jd.xn.clinet.utils.StringUtils;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Json转换工具
 *
 * @author lijizhen1@jd.com
 * @date 2018/4/16 19:53
 */
public class Converters {
    /**
     * 是否对JSON返回的数据类型进行校验，默认不校验。给内部测试JSON返回时用的开关。
     * 规则：返回的"基本"类型只有String,Long,Boolean,Date,采取严格校验方式，如果类型不匹配，报错
     */
    public static boolean isCheckJsonType = false;
    /**
     * 空缓存，避免缓存击穿后影响Field和Method的获取性能。
     */
    private static final Object emptyCache = new Object();

    //基本的属性在类加载时候初始化
    private static final Map<String, Set<String>> baseProps = new HashMap();
    private static final Map<String, Object> fieldCache = new ConcurrentHashMap();
    //类加载后本地方法缓存
    private static final Map<String, Object> methodCache = new ConcurrentHashMap();


    /**
     * 使用指定 的读取器去转换字符串为对象。
     *
     * @param clazz  领域类型
     * @param reader 读取器
     * @param <T>    领域泛型
     * @return 领域对象
     * @throws ApiException
     */
    public static <T> T convert(Class<T> clazz, Reader reader)
            throws ApiException {
        T rsp = null;
        try {
            rsp = clazz.newInstance();
            BeanInfo beanInfo = Introspector.getBeanInfo(clazz);
            PropertyDescriptor[] pds = beanInfo.getPropertyDescriptors();
            for (PropertyDescriptor pd : pds) {
                Method method = getCacheMethod(clazz, pd);
                //忽略只读方法
                if (method == null) {
                    continue;
                }
            }
        } catch (Exception e) {
            throw new ApiException(e);
        }
        return rsp;
    }


    /**
     * 获得缓存的方法
     *
     * @param clazz
     * @param pd
     * @param <T>
     * @return
     */
    private static <T> Method getCacheMethod(Class<T> clazz, PropertyDescriptor pd) {
        String key = new StringBuilder(clazz.getName()).append("_").append(pd.getName()).toString();
        Object method = methodCache.get(key);
        if (method == null) {
            method = pd.getWriteMethod();
            if (method == null) {
                //缓存只读的方法
                method = emptyCache;
            }
            //缓存方法
            methodCache.put(key, method);
        }
        return method == emptyCache ? null : (Method) method;
    }

    static {
        baseProps.put(TFResponse.class.getName(), StringUtils.getClassProperties(TFResponse.class, false));
    }


    public static Field getField(Class<?> clazz, PropertyDescriptor prop) {
        String key = new StringBuilder(clazz.getName()).append("_").append(prop.getName()).toString();
        Object field = fieldCache.get(key);
        if (field == null) {
            try {
                field = clazz.getDeclaredField(prop.getName());
            } catch (NoSuchFieldException e) {
                field = emptyCache; // cache isolated field
            }
            fieldCache.put(key, field);
        }
        return field == emptyCache ? null : (Field) field;
    }
}
