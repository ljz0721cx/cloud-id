package com.jd.xn.clinet.toplink.channel.tcp;

import com.jd.xn.clinet.toplink.Logger;
import com.jd.xn.clinet.toplink.channel.netty.NettyClientUpstreamHandler;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.MessageEvent;

/**
 * @author lijizhen1@jd.com
 * @date 2018/4/14 14:17
 */
public class TcpClientUpstreamHandler extends NettyClientUpstreamHandler {
    public TcpClientUpstreamHandler(Logger logger, TcpClientChannel clientChannel) {
        super(logger, clientChannel);
    }

    @Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
        if (haveHandler()) {
            getHandler().onMessage(createContext(e.getMessage()));
        }
    }
}