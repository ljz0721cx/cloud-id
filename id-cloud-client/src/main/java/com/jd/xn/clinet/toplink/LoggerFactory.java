package com.jd.xn.clinet.toplink;

/**
 * 日志工厂类
 * @author lijizhen1@jd.com
 * @date 2018/4/14 11:34
 */
public abstract interface LoggerFactory {
    /**
     *
     * @param paramString
     * @return
     */
    public abstract Logger create(String paramString);

    /**
     *
     * @param paramClass
     * @return
     */
    public abstract Logger create(Class<?> paramClass);

    /**
     *
     * @param paramObject
     * @return
     */
    public abstract Logger create(Object paramObject);
}
