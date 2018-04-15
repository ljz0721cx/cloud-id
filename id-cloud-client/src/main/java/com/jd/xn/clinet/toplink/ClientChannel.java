package com.jd.xn.clinet.toplink;

import com.jd.xn.clinet.toplink.channel.ChannelHandler;
import com.jd.xn.clinet.toplink.channel.ChannelSender;

import java.net.URI;

/**
 * @author lijizhen1@jd.com
 * @date 2018/4/14 11:38
 */
public abstract interface ClientChannel extends ChannelSender {
    public abstract boolean isConnected();

    public abstract ChannelHandler getChannelHandler();

    public abstract void setChannelHandler(ChannelHandler paramChannelHandler);

    /**
     * 设置请求uri
     * @param paramURI
     */
    public abstract void setUri(URI paramURI);

    /**
     * 获得连接url
     *
     * @return
     */
    public abstract URI getUri();

    /**
     * 设置心跳检查Timer
     *
     * @param paramResetableTimer
     */
    public abstract void setHeartbeatTimer(ResetableTimer paramResetableTimer);
}