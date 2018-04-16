package com.jd.xn.clinet.toplink.channel.tcp;

import com.jd.xn.clinet.toplink.ClientChannel;
import com.jd.xn.clinet.toplink.Logger;
import com.jd.xn.clinet.toplink.LoggerFactory;
import com.jd.xn.clinet.toplink.channel.ChannelException;
import com.jd.xn.clinet.toplink.channel.ConnectingChannelHandler;
import com.jd.xn.clinet.toplink.channel.netty.NettyClient;
import org.jboss.netty.channel.ChannelPipeline;

import java.net.URI;

/**
 * @author lijizhen1@jd.com
 * @date 2018/4/14 11:32
 */
public class TcpClient extends NettyClient {
    /**
     * 客户端连接通道
     *
     * @param loggerFactory
     * @param uri
     * @param connectTimeoutMillis
     * @param pipeline
     * @return
     * @throws ChannelException
     */
    public static ClientChannel connect(LoggerFactory loggerFactory, URI uri,
                                        int connectTimeoutMillis,
                                        ChannelPipeline pipeline)
            throws ChannelException {
        Logger logger = loggerFactory.create(String.format("TcpClientHandler-%s", new Object[]{uri}));

        /**
         * 创建tcp的通道
         */
        TcpClientChannel clientChannel = new TcpClientChannel();
        clientChannel.setUri(uri);

        /**
         * 设置连接通道的handler
         */
        ConnectingChannelHandler handler = new ConnectingChannelHandler();
        clientChannel.setChannelHandler(handler);
        /**
         * 客户端消息回调
         */
        TcpClientUpstreamHandler tcpHandler = new TcpClientUpstreamHandler(logger, clientChannel);
        /**
         * 预连接并且建立连接
         */
        prepareAndConnect(logger,
                uri,
                pipeline,
                tcpHandler,
                uri.getScheme().equalsIgnoreCase("ssl"),
                connectTimeoutMillis);

        return clientChannel;
    }
}
