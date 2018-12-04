package com.vict.netty.handler;

import com.vict.netty.config.NettyConfiguration;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http.websocketx.*;
import io.netty.util.CharsetUtil;
import org.springframework.web.socket.server.HandshakeHandler;

import java.util.Date;

public class NettySocketHandler extends SimpleChannelInboundHandler<Object> {
    private WebSocketServerHandshaker webSocketServerHandshaker;
    private static final String WEB_SOCKET_URL = "ws://localhost:8888/websocket";

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        NettyConfiguration.channels.add(ctx.channel());
        System.out.println("客户端与服务端链接开始");
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        NettyConfiguration.channels.remove(ctx.channel());
        System.out.println("客户端与服务端链接关闭");
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
    }


    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, Object o) throws Exception {
        if (o instanceof FullHttpRequest) {
            handleHttpRequest(channelHandlerContext, (FullHttpRequest) o);
        } else if (o instanceof WebSocketFrame) {
            handWebSocketFrame(channelHandlerContext, (WebSocketFrame) o);
        }
    }

    /**
     * 处理websocket 请求
     *
     * @param ctx
     * @param socketFrame
     */
    private void handWebSocketFrame(ChannelHandlerContext ctx, WebSocketFrame socketFrame) {
        if (socketFrame instanceof CloseWebSocketFrame) {
            handshakeHandler.close(ctx.channel(), (CloseWebSocketFrame) socketFrame);
        }
        if (socketFrame instanceof PingWebSocketFrame) {
            ctx.channel().write(new PongWebSocketFrame(socketFrame.content().retain()));
            return;
        }

        if (!(socketFrame instanceof TextWebSocketFrame)) {

            throw new RuntimeException("不支持的二进制消息");
        }
        //返回应答消息
        String request = ((TextWebSocketFrame) socketFrame).text();

        System.out.println("接收到的客户端消息：" + request);

        TextWebSocketFrame tws = new TextWebSocketFrame(new Date().toString() + ctx.channel().id() + "==>" + request);

        //服务端向客户端群发消息

        NettyConfiguration.channels.writeAndFlush(tws);
    }

    /**
     * 服务端向客户端发送数据
     *
     * @param channelHandlerContext
     * @param fullHttpRequest
     * @param response
     */
    private void sendHttpResponse(ChannelHandlerContext channelHandlerContext,
                                  FullHttpRequest fullHttpRequest,
                                  DefaultFullHttpResponse response) {
        if (response.status().code() != 200) {
            ByteBuf buf = Unpooled.copiedBuffer(response.status().toString(), CharsetUtil.UTF_8);
            response.content().writeBytes(buf);
            buf.release();
        }
        ChannelFuture future = channelHandlerContext.channel().writeAndFlush("--------||||-----");
        if (response.status().code() != 200) {
            future.addListener(ChannelFutureListener.CLOSE);
        }
    }

    private WebSocketServerHandshaker handshakeHandler;

    private void handleHttpRequest(ChannelHandlerContext channelHandlerContext,
                                   FullHttpRequest fullHttpRequest) {
        if (!fullHttpRequest.decoderResult().isSuccess()
                || !("websocket".equals(fullHttpRequest.headers().get("Upgrade")))) {
            this.sendHttpResponse(channelHandlerContext, fullHttpRequest,
                    new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.BAD_REQUEST));
            return;
        }

        WebSocketServerHandshakerFactory factory = new WebSocketServerHandshakerFactory(WEB_SOCKET_URL, "", false);
        handshakeHandler = factory.newHandshaker(fullHttpRequest);
        if (handshakeHandler == null) {
            WebSocketServerHandshakerFactory.sendUnsupportedVersionResponse(channelHandlerContext.channel());
        } else {
            handshakeHandler.handshake(channelHandlerContext.channel(), fullHttpRequest);
        }
    }
}
