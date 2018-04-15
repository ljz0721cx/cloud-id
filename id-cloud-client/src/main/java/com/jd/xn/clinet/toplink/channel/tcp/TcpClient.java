package com.jd.xn.clinet.toplink.channel.tcp;

import com.jd.xn.clinet.toplink.ClientChannel;
import com.jd.xn.clinet.toplink.Logger;
import com.jd.xn.clinet.toplink.LoggerFactory;
import com.jd.xn.clinet.toplink.channel.ChannelException;
import com.jd.xn.clinet.toplink.channel.ConnectingChannelHandler;
import org.jboss.netty.channel.ChannelPipeline;

import java.net.URI;

/**
 * @author lijizhen1@jd.com
 * @date 2018/4/14 11:32
 */
public class TcpClient {
    public static ClientChannel connect(LoggerFactory loggerFactory, URI uri,
                                        int connectTimeoutMillis,
                                        ChannelPipeline pipeline)
            throws ChannelException {
        Logger logger = loggerFactory.create(String.format("TcpClientHandler-%s", new Object[]{uri}));

        TcpClientChannel clientChannel = new TcpClientChannel();
        clientChannel.setUri(uri);

        ConnectingChannelHandler handler = new ConnectingChannelHandler();
        clientChannel.setChannelHandler(handler);

        TcpClientUpstreamHandler tcpHandler = new TcpClientUpstreamHandler(logger, clientChannel);

        prepareAndConnect(logger, uri, pipeline, tcpHandler, uri.getScheme().equalsIgnoreCase("ssl"), connectTimeoutMillis);

        return clientChannel;
    }
}
