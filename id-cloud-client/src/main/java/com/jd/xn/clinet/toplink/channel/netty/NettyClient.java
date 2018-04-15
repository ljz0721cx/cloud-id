package com.jd.xn.clinet.toplink.channel.netty;

import com.jd.xn.clinet.toplink.Logger;
import com.jd.xn.clinet.toplink.Text;
import com.jd.xn.clinet.toplink.channel.ChannelException;
import com.jd.xn.clinet.toplink.channel.ChannelHandler;
import org.jboss.netty.bootstrap.ClientBootstrap;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.socket.nio.NioClientSocketChannelFactory;
import org.jboss.netty.handler.ssl.SslHandler;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.TrustManager;
import java.net.InetSocketAddress;
import java.net.URI;
import java.util.concurrent.Executors;

/**
 * @author lijizhen1@jd.com
 * @date 2018/4/14 14:21
 */
public abstract class NettyClient {
    private static TrustManager[] trustAllCerts = { new X509AlwaysTrustManager() };
    private static NioClientSocketChannelFactory nioClientSocketChannelFactory = new NioClientSocketChannelFactory(Executors.newCachedThreadPool(), Executors.newCachedThreadPool());

    public static InetSocketAddress parse(URI uri) {
        return new InetSocketAddress(uri.getHost(), uri.getPort() > 0 ? uri.getPort() : 80);
    }

    protected static Channel prepareAndConnect(Logger logger,
                                               URI uri,
                                               ChannelPipeline pipeline,
                                               ChannelHandler handler,
                                               boolean ssl,
                                               int connectTimeoutMillis)
            throws ChannelException {
        SslHandler sslHandler = ssl ? createSslHandler(uri) : null;
        ClientBootstrap bootstrap = prepareBootstrap(logger, pipeline, handler, sslHandler, connectTimeoutMillis);
        return doConnect(uri, bootstrap, sslHandler);
    }


    private static Channel doConnect(URI uri,
                                     ClientBootstrap bootstrap,
                                     SslHandler sslHandler) throws ChannelException {
        try {
            Channel channel = bootstrap.connect(parse(uri)).syncUninterruptibly().getChannel();
            if (sslHandler != null)
                sslHandler.handshake().syncUninterruptibly();
            return channel;
        } catch (Exception e) {
            throw new ChannelException(Text.CONNECT_ERROR, e);
        }
    }

    private static ClientBootstrap prepareBootstrap(Logger logger,
                                                    ChannelPipeline pipeline,
                                                    ChannelHandler handler,
                                                    SslHandler sslHandler,
                                                    int connectTimeoutMillis) {
        ClientBootstrap bootstrap = new ClientBootstrap(nioClientSocketChannelFactory);
        bootstrap.setOption("tcpNoDelay", Boolean.valueOf(true));
        bootstrap.setOption("reuseAddress", Boolean.valueOf(true));
        bootstrap.setOption("connectTimeoutMillis", Integer.valueOf(connectTimeoutMillis));
        bootstrap.setOption("writeBufferHighWaterMark", Integer.valueOf(10485760));

        if (sslHandler != null) {
            pipeline.addFirst("ssl", sslHandler);
        }
        if (handler != null) {
            pipeline.addLast("handler", handler);
        }
        bootstrap.setPipelineFactory(new ChannelPipelineFactory() {
            public ChannelPipeline getPipeline() throws Exception {
                return this.val$pipeline;
            }
        });
        return bootstrap;
    }

    private static SslHandler createSslHandler(URI uri) {
        try {
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, trustAllCerts, null);
            SSLEngine sslEngine = sslContext.createSSLEngine();
            sslEngine.setUseClientMode(true);
            return new SslHandler(sslEngine);
        } catch (Exception e) {
        }
        return null;
    }

}
