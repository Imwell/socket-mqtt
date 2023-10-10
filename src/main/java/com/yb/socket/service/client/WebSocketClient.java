package com.yb.socket.service.client;

import com.yb.socket.exception.SocketRuntimeException;
import com.yb.socket.pojo.Request;
import com.yb.socket.pojo.Response;
import io.netty.channel.ChannelFuture;
import io.netty.handler.codec.http.DefaultHttpHeaders;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketClientHandshakerFactory;
import io.netty.handler.codec.http.websocketx.WebSocketVersion;
import io.netty.handler.codec.http.websocketx.extensions.compression.WebSocketClientCompressionHandler;
import io.netty.handler.timeout.IdleStateHandler;

import java.net.InetSocketAddress;

public class WebSocketClient extends BaseClient {

    private WebSocketClientHandler webSocketClientHandler;

    @Override
    protected void init() {
        super.init();
        webSocketClientHandler = new WebSocketClientHandler(
                WebSocketClientHandshakerFactory.newHandshaker(uri, WebSocketVersion.V13, null, true, new DefaultHttpHeaders())
                , eventDispatcher);
        if (checkHeartbeat) {
            heartbeatHandler = new WebSocketHeartbeatHandler();
        }
    }

    public ChannelFuture connect(boolean sync) {
        try {
            ChannelFuture connect = super.connect(new InetSocketAddress(ip, port), sync, (pipeline) -> {

                pipeline.addLast("idleStateHandler", new IdleStateHandler(readerIdleTimeSeconds, writerIdleTimeSeconds, allIdleTimeSeconds));
                pipeline.addLast("heartbeatHandler", heartbeatHandler);

                pipeline.addLast("httpClientCodec", new HttpClientCodec());
                pipeline.addLast("httpObjectAggregator", new HttpObjectAggregator(8192));
                pipeline.addLast("webSocketClientCompressionHandler", WebSocketClientCompressionHandler.INSTANCE);
                pipeline.addLast("webSocketClientHandler", webSocketClientHandler);

            });
            // 确认正常连接之后再进行返回
            webSocketClientHandler.handshakeFuture().sync();
            // 开启ws心跳，ws中不能加入心跳handler
            return connect;
        } catch (InterruptedException e) {
            throw new SocketRuntimeException(e);
        }
    }

//    @Override
//    public Response sendWithSync(Request message, int timeout) {
//        TextWebSocketFrame textWebSocketFrame = new TextWebSocketFrame(message.getMessage().toString());
//        ChannelFuture channelFuture = channel.writeAndFlush(textWebSocketFrame);
//        return new Response();
//    }
}
