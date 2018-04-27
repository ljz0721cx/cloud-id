package com.jd.xn.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.sctp.nio.NioSctpServerChannel;
import io.netty.channel.socket.SocketChannel;

import java.net.InetSocketAddress;

/**
 * @author lijizhen1@jd.com
 * @date 2018/4/27 8:50
 */
public class Server {

    /**
     * 绑定端口
     *
     * @param port
     */
    private void bind(int port) throws Exception {
        //配置服务端的NIO线程组
        //实际上EventLoopGroup就是Reactor线程组
        //两个Reactor一个用于服务端接收客户端的连接，另一个用于进行SocketChannel的网络读写
        EventLoopGroup bossGroup = new NioEventLoopGroup();//服务端线程
        EventLoopGroup workerGroup = new NioEventLoopGroup();//工作组线程

        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workerGroup)
                    //指定 NioServerSocketChannel为信道类型。
                    .channel(NioSctpServerChannel.class)
                    //设置本地地址是 InetSocketAddress 与所选择的端口
                    .localAddress(new InetSocketAddress(port))
                    //配置NioServerSocketChannel的TCP参数
                    .option(ChannelOption.SO_LINGER, 1024)
                    //绑定I/O事件的处理类ChildChannelHandler,作用类似于Reactor模式中的Handler类
                    //主要用于处理网络I/O事件，例如记录日志，对消息进行编解码等
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            socketChannel.pipeline().addLast(new ServerHandler());
                        }
                    });
            //绑定监听端口，调用sync同步阻塞方法等待绑定操作完成，完成后返回ChannelFuture类似于JDK中Future
            ChannelFuture f = bootstrap.bind(port).sync();
            //使用sync方法进行阻塞，等待服务端链路关闭之后Main函数才退出
            f.channel().closeFuture().sync();
        } finally {
            //优雅退出，释放线程资源
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }

    }

    private class ServerHandler extends ChannelInboundHandlerAdapter {
        //每个信息入站都会调用
        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            ByteBuf buf = (ByteBuf) msg;
//			byte[] req = new byte[buf.readableBytes()];
//			buf.readBytes(req);
//			String body = new String(req, "UTF-8");
            System.out.println("The time server receive order :" + buf.toString());
            ctx.write(buf);
        }

        //通知处理器最后的channelread()是当前批处理中的最后一条消息时调用
        @Override
        public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
            ctx.flush();
        }

        //读操作时捕获到异常时调用
        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
            ctx.close();
        }
    }

    public static void main(String[] args) throws Exception {
        int port = 8080;
        if (args != null && args.length > 0) {
            try {
                //第一个参数作为端口号
                port = Integer.valueOf(args[0]);
            } catch (NumberFormatException e) {
                //采用默认值
            }
        }
        new Server().bind(port);
    }


}
