package com.jd.xn.clinet.toplink.channel;

import java.net.SocketAddress;
import java.nio.ByteBuffer;

/**
 * @author lijizhen1@jd.com
 * @date 2018/4/14 10:46
 */
public abstract interface ChannelSender {
    /**
     * @param paramArrayOfByte
     * @param paramInt1
     * @param paramInt2
     * @throws ChannelException
     */
    public abstract void send(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
            throws ChannelException;

    /**
     * @param paramByteBuffer  发送参数buffer
     * @param paramSendHandler 参数发送回调
     * @throws ChannelException
     */
    public abstract void send(ByteBuffer paramByteBuffer, SendHandler paramSendHandler)
            throws ChannelException;

    /**
     * @param paramString
     */
    public abstract void close(String paramString);

    /**
     * @return
     */
    public abstract SocketAddress getLocalAddress();

    /**
     * @return
     */
    public abstract SocketAddress getRemoteAddress();

    /**
     * @param paramObject
     * @return
     */
    public abstract Object getContext(Object paramObject);

    /**
     * @param paramObject1
     * @param paramObject2
     */
    public abstract void setContext(Object paramObject1, Object paramObject2);


    public static abstract interface SendHandler {
        public abstract void onSendComplete(boolean paramBoolean);
    }
}
