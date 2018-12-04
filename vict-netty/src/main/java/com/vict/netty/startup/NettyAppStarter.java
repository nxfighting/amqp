package com.vict.netty.startup;

import com.vict.netty.handler.NettySocketHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

public class NettyAppStarter {
    public static void main(String args[]) {
        EventLoopGroup loopGroup = new NioEventLoopGroup();
        EventLoopGroup workGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(workGroup);
            serverBootstrap.channel(NioServerSocketChannel.class);
            serverBootstrap.childHandler(new NettySocketHandler());
            System.out.println("服务端开启等待链接");
            Channel channel = serverBootstrap.bind(8888).sync().channel();
            channel.closeFuture().sync();
        } catch (Exception ex) {

        } finally {
            loopGroup.shutdownGracefully();
            workGroup.shutdownGracefully();
        }
    }
}
