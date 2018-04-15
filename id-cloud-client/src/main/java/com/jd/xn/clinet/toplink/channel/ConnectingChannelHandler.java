package com.jd.xn.clinet.toplink.channel;

/**
 * @author lijizhen1@jd.com
 * @date 2018/4/14 14:14
 */
public class ConnectingChannelHandler implements ChannelHandler {
    public Throwable error;
    public Object syncObject = new Object();

    @Override
    public void onConnect(ChannelContext context) {
        synchronized (this.syncObject) {
            this.syncObject.notify();
        }
    }

    @Override
    public void onMessage(ChannelContext context) {
    }

    @Override
    public void onError(ChannelContext context) {
        this.error = context.getError();
        synchronized (this.syncObject) {
            this.syncObject.notify();
        }
    }

    @Override
    public void onClosed(String reason) {
    }
}
