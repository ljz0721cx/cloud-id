package com.jd.xn.clinet.toplink.channel;

/**
 * @author lijizhen1@jd.com
 * @date 2018/4/14 10:45
 */
public abstract interface ChannelHandler {
    /**
     * @param paramChannelContext
     * @throws Exception
     */
    public abstract void onConnect(ChannelContext paramChannelContext)
            throws Exception;

    /**
     * @param paramChannelContext
     * @throws Exception
     */
    public abstract void onMessage(ChannelContext paramChannelContext)
            throws Exception;

    /**
     * @param paramChannelContext
     * @throws Exception
     */
    public abstract void onError(ChannelContext paramChannelContext)
            throws Exception;

    /**
     * @param paramString
     */
    public abstract void onClosed(String paramString);
}
