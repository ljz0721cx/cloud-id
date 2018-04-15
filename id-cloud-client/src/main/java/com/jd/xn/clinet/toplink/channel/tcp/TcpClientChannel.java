package com.jd.xn.clinet.toplink.channel.tcp;

import com.jd.xn.clinet.toplink.ClientChannel;
import com.jd.xn.clinet.toplink.ResetableTimer;
import com.jd.xn.clinet.toplink.Text;
import com.jd.xn.clinet.toplink.channel.ChannelException;
import com.jd.xn.clinet.toplink.channel.ChannelHandler;
import com.jd.xn.clinet.toplink.channel.ChannelSender;
import com.jd.xn.clinet.toplink.channel.netty.NettyClientChannel;
import org.jboss.netty.channel.Channel;

import java.net.URI;
import java.nio.ByteBuffer;

/**
 * TCP请求的通道
 *
 * @author lijizhen1@jd.com
 * @date 2018/4/14 11:43
 */
public class TcpClientChannel extends TcpChannelSender
        implements ClientChannel, NettyClientChannel {
    private URI uri;
    private ChannelHandler channelHandler;
    private ResetableTimer timer;

    public TcpClientChannel() {
        super(null);
    }

    public TcpClientChannel(Channel channel) {
        super(channel);
    }

    @Override
    public void setChannel(Channel channel) {
        this.channel = channel;
    }

    @Override
    public void setUri(URI uri) {
        this.uri = uri;
    }

    @Override
    public URI getUri() {
        return this.uri;
    }

    @Override
    public ChannelHandler getChannelHandler() {
        delayPing();
        return this.channelHandler;
    }

    @Override
    public void setChannelHandler(ChannelHandler handler) {
        this.channelHandler = handler;
    }

    @Override
    public boolean isConnected() {
        return this.channel.isConnected();
    }

    @Override
    public void setHeartbeatTimer(ResetableTimer timer) {
        this.timer = timer;
        this.timer.setTask(() -> {
        });
        this.timer.start();
    }

    @Override
    public void send(ByteBuffer dataBuffer, ChannelSender.SendHandler sendHandler) throws ChannelException {
        checkChannel();
        super.send(dataBuffer, sendHandler);
    }

    @Override
    public void send(byte[] data, int offset, int length) throws ChannelException {
        checkChannel();
        super.send(data, offset, length);
    }

    private void checkChannel()
            throws ChannelException {
        if (!this.channel.isConnected()) {
            if (this.timer != null) {
                this.timer.stop();
            }
            throw new ChannelException(Text.CHANNEL_CLOSED);
        }
        delayPing();
    }

    private void delayPing() {
        if (this.timer != null) {
            this.timer.delay();
        }
    }
}
