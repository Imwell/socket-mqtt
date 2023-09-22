package com.yb.socket.service.client;

import com.yb.socket.future.InvokeFuture;
import com.yb.socket.pojo.MqttRequest;
import com.yb.socket.pojo.Response;
import com.yb.socket.service.EventDispatcher;
import com.yb.socket.service.WrappedChannel;
import com.yb.socket.service.server.Server;
import com.yb.socket.service.server.ServerContext;
import io.netty.channel.*;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.websocketx.*;
import io.netty.util.CharsetUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WebSocketClientHandler extends SimpleChannelInboundHandler<Object> {

    private static final Logger logger = LoggerFactory.getLogger(WebSocketClientHandler.class);

    private final WebSocketClientHandshaker handshaker;
    private ChannelPromise handshakeFuture;
    private EventDispatcher eventDispatcher;

    public WebSocketClientHandler(WebSocketClientHandshaker handshaker, EventDispatcher eventDispatcher) {
        this.handshaker = handshaker;
        this.eventDispatcher = eventDispatcher;
    }

    public ChannelFuture handshakeFuture() {
        return handshakeFuture;
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) {
        handshakeFuture = ctx.newPromise();
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        handshaker.handshake(ctx.channel());
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        System.out.println("WebSocket Client disconnected!");
        ctx.close();
    }

    @Override
    public void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (!handshaker.isHandshakeComplete()) {
            try {
                handshaker.finishHandshake(ctx.channel(), (FullHttpResponse) msg);
                System.out.println("WebSocket Client connected!");
                handshakeFuture.setSuccess();
            } catch (WebSocketHandshakeException e) {
                System.out.println("WebSocket Client failed to connect");
                handshakeFuture.setFailure(e);
            }
            return;
        }


        if (msg instanceof FullHttpResponse) {
            FullHttpResponse response = (FullHttpResponse) msg;
            throw new IllegalStateException(
                    "Unexpected FullHttpResponse (getStatus=" + response.status() +
                            ", content=" + response.content().toString(CharsetUtil.UTF_8) + ')');
        }

        WrappedChannel channel = ((BaseClient) eventDispatcher.getService()).getChannel();
        eventDispatcher.dispatchMessageEvent(ctx, channel, msg);

//        try {
//            WebSocketFrame frame = (WebSocketFrame) msg;
//            if (frame instanceof TextWebSocketFrame) {
//                TextWebSocketFrame textFrame = (TextWebSocketFrame) frame;
//                logger.info("WebSocket Client received message: {}", textFrame.text());
//                // todo 测试用
////                Server server = ServerContext.getContext().getServer();
////                String message = textFrame.text();
////                MqttRequest mqttRequest = new MqttRequest((message.getBytes()));
////                if (server.getChannels().size() > 0) {
////                    for (WrappedChannel ch : server.getChannels().values()) {
////                        server.send(ch, "mqtt", mqttRequest);
////                    }
////                }
//            } else if (frame instanceof PongWebSocketFrame) {
//                System.out.println("WebSocket Client received pong");
//            } else if (frame instanceof CloseWebSocketFrame) {
//                System.out.println("WebSocket Client received closing");
//                ctx.channel().close();
//            }
//        } catch (Exception exception) {
//            logger.error("ERROR", exception);
//        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        if (!handshakeFuture.isDone()) {
            handshakeFuture.setFailure(cause);
        }
        ctx.close();
    }
}