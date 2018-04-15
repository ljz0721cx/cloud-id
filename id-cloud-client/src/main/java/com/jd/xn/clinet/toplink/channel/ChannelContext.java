package com.jd.xn.clinet.toplink.channel;

import java.nio.ByteBuffer;

/**
 * @author lijizhen1@jd.com
 * @date 2018/4/14 10:45
 */
public class ChannelContext {

    /**
     * 具体错误
     */
    private Throwable error;
    /**
     * 请求通道
     */
    private ChannelSender sender;
    /**
     * 响应消息
     */
    private Object message;

    public Throwable getError() {
        return this.error;
    }

    public void setError(Throwable e) {
        this.error = e;
    }

    public ChannelSender getSender() {
        return this.sender;
    }

    public void setSender(ChannelSender sender) {
        this.sender = sender;
    }

    public void setMessage(Object message) {
        this.message = message;
    }

    public Object getMessage() {
        return this.message;
    }

    public void reply(byte[] data, int offset, int length) throws ChannelException {
        this.sender.send(data, offset, length);
    }

    public void reply(ByteBuffer dataBuffer) throws ChannelException {
        this.sender.send(dataBuffer, null);
    }

    public void reply(ByteBuffer dataBuffer, ChannelSender.SendHandler sendHandler) throws ChannelException {
        this.sender.send(dataBuffer, sendHandler);
    }
}
