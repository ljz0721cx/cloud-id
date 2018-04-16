package com.jd.xn.clinet.toplink.channel.netty;

import com.jd.xn.clinet.toplink.Logger;
import com.jd.xn.clinet.toplink.Text;
import com.jd.xn.clinet.toplink.channel.ChannelException;
import com.jd.xn.clinet.toplink.channel.X509AlwaysTrustManager;
import org.jboss.netty.bootstrap.ClientBootstrap;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandler;
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
    //创建受信任的管理器
    private static TrustManager[] trustAllCerts = {new X509AlwaysTrustManager()};
    private static NioClientSocketChannelFactory nioClientSocketChannelFactory =
            new NioClientSocketChannelFactory(
                    Executors.newCachedThreadPool(),
                    Executors.newCachedThreadPool());

    /**
     * 解析地址
     *
     * @param uri
     * @return
     */
    public static InetSocketAddress parse(URI uri) {
        return new InetSocketAddress(uri.getHost(), uri.getPort() > 0 ? uri.getPort() : 80);
    }

    /**
     * 预定义和建立连接
     *
     * @param logger
     * @param uri
     * @param pipeline
     * @param handler
     * @param ssl
     * @param connectTimeoutMillis
     * @return
     * @throws ChannelException
     */
    protected static Channel prepareAndConnect(Logger logger,
                                               URI uri,
                                               ChannelPipeline pipeline,
                                               ChannelHandler handler,
                                               boolean ssl,
                                               int connectTimeoutMillis)
            throws ChannelException {
        SslHandler sslHandler = ssl ? createSslHandler(uri) : null;
        ClientBootstrap bootstrap = prepareBootstrap(logger,
                pipeline,
                handler,
                sslHandler,
                connectTimeoutMillis);
        return doConnect(uri, bootstrap, sslHandler);
    }

    /**
     * 建立连接
     *
     * @param uri
     * @param bootstrap
     * @param sslHandler
     * @return
     * @throws ChannelException
     */
    private static Channel doConnect(URI uri,
                                     ClientBootstrap bootstrap,
                                     SslHandler sslHandler) throws ChannelException {
        try {
            Channel channel = bootstrap
                    .connect(parse(uri))
                    //TODO 需要知道该处是否使用正常
                    .awaitUninterruptibly()
                    .getChannel();
            if (sslHandler != null) {
                sslHandler
                        .handshake()
                        .awaitUninterruptibly();
            }
            return channel;
        } catch (Exception e) {
            throw new ChannelException(Text.CONNECT_ERROR, e);
        }
    }

    /**
     * 客户端脚手架
     *
     * @param logger
     * @param pipeline
     * @param handler
     * @param sslHandler
     * @param connectTimeoutMillis
     * @return
     */
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
            @Override
            public ChannelPipeline getPipeline() throws Exception {
                return pipeline;
            }
        });
        return bootstrap;
    }

    /**
     * 建立握手机制
     *
     * @param uri
     * @return
     */
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
