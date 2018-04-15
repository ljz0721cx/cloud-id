package com.jd.xn.clinet.toplink.channel.netty;

import com.jd.xn.clinet.toplink.channel.ChannelSender;
import org.jboss.netty.channel.Channel;

import java.net.SocketAddress;
import java.util.HashMap;
import java.util.Map;

/**
 * @author lijizhen1@jd.com
 * @date 2018/4/14 11:46
 */
public abstract class NettyChannelSender implements ChannelSender {
    protected Channel channel;
    private Map<Object, Object> context;

    public NettyChannelSender(Channel channel) {
        this.channel = channel;
        this.context = new HashMap();
    }

    public Channel getChannel() {
        return this.channel;
    }

    @Override
    public SocketAddress getLocalAddress() {
        return this.channel.getLocalAddress();
    }

    @Override
    public SocketAddress getRemoteAddress() {
        return this.channel.getRemoteAddress();
    }

    @Override
    public Object getContext(Object key) {
        return this.context.get(key);
    }

    @Override
    public void setContext(Object key, Object value) {
        this.context.put(key, value);
    }
}