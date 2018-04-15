package com.jd.xn.clinet.toplink.channel.tcp;

import com.jd.xn.clinet.toplink.channel.ChannelException;
import com.jd.xn.clinet.toplink.channel.ChannelSender;
import com.jd.xn.clinet.toplink.channel.netty.NettyChannelSender;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;

import java.nio.ByteBuffer;

/**
 * @author lijizhen1@jd.com
 * @date 2018/4/14 11:45
 */
public class TcpChannelSender extends NettyChannelSender {
    public TcpChannelSender(Channel channel) {
        super(channel);
    }

    @Override
    public void send(byte[] data, int offset, int length) throws ChannelException {
        send(ChannelBuffers.wrappedBuffer(data, offset, length), null);
    }

    @Override
    public void send(ByteBuffer dataBuffer, ChannelSender.SendHandler sendHandler) throws ChannelException {
        send(ChannelBuffers.wrappedBuffer(dataBuffer), sendHandler);
    }

    @Override
    public void close(String reason) {
        this.channel.write(reason).addListener(future -> future.getChannel().close());
    }

    private void send(Object message, final ChannelSender.SendHandler sendHandler) throws ChannelException {
        if (sendHandler == null) {
            this.channel.write(message);
        } else {
            this.channel.write(message).addListener(future -> {
                if (sendHandler != null) {
                    sendHandler.onSendComplete(future.isSuccess());
                }
            });
        }
    }
}
