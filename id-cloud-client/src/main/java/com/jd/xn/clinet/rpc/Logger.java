package com.jd.xn.clinet.rpc;

/**
 * @author lijizhen1@jd.com
 * @date 2018/4/14 11:35
 */
public abstract interface Logger {
    public abstract boolean isDebugEnabled();

    public abstract boolean isInfoEnabled();

    public abstract boolean isWarnEnabled();

    public abstract boolean isErrorEnabled();

    public abstract boolean isFatalEnabled();

    public abstract void debug(String paramString);

    public abstract void debug(Throwable paramThrowable);

    public abstract void debug(String paramString, Throwable paramThrowable);

    public abstract void debug(String paramString, Object[] paramArrayOfObject);

    public abstract void info(String paramString);

    public abstract void info(Throwable paramThrowable);

    public abstract void info(String paramString, Throwable paramThrowable);

    public abstract void info(String paramString, Object[] paramArrayOfObject);

    public abstract void warn(String paramString);

    public abstract void warn(Throwable paramThrowable);

    public abstract void warn(String paramString, Throwable paramThrowable);

    public abstract void warn(String paramString, Object[] paramArrayOfObject);

    public abstract void error(String paramString);

    public abstract void error(Throwable paramThrowable);

    public abstract void error(String paramString, Throwable paramThrowable);

    public abstract void error(String paramString, Object[] paramArrayOfObject);

    public abstract void fatal(String paramString);

    public abstract void fatal(Throwable paramThrowable);

    public abstract void fatal(String paramString, Throwable paramThrowable);

    public abstract void fatal(String paramString, Object[] paramArrayOfObject);
}