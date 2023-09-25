package com.yb.socket.listener;

import com.alibaba.fastjson.JSONObject;
import com.yb.socket.future.InvokeFuture;
import com.yb.socket.pojo.MqttRequest;
import com.yb.socket.pojo.Response;
import com.yb.socket.service.WrappedChannel;
import com.yb.socket.service.server.Server;
import com.yb.socket.service.server.ServerContext;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.websocketx.CloseWebSocketFrame;
import io.netty.handler.codec.http.websocketx.PongWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import io.netty.util.CharsetUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

public class WebSocketClientMessageEventListener implements MessageEventListener {

    private static final Logger logger = LoggerFactory.getLogger(WebSocketClientMessageEventListener.class);

    @Override
    public EventBehavior channelRead(ChannelHandlerContext ctx, WrappedChannel channel, Object msg) {

        if (msg instanceof FullHttpResponse) {
            FullHttpResponse response = (FullHttpResponse) msg;
            throw new IllegalStateException(
                    "Unexpected FullHttpResponse (getStatus=" + response.status() +
                            ", content=" + response.content().toString(CharsetUtil.UTF_8) + ')');
        }

        try {
            WebSocketFrame frame = (WebSocketFrame) msg;
            if (frame instanceof TextWebSocketFrame) {
                TextWebSocketFrame textFrame = (TextWebSocketFrame) frame;
                logger.info("WebSocket Client received message: {}", textFrame.text());

                // todo 测试用
//                sendMessageToMqtt(textFrame.text());

                // 后续处理
                Response response = JSONObject.parseObject(textFrame.text(), Response.class);
                processResponse(ctx, channel, response);
            } else if (frame instanceof PongWebSocketFrame) {
                System.out.println("WebSocket Client received pong");
            } else if (frame instanceof CloseWebSocketFrame) {
                System.out.println("WebSocket Client received closing");
                channel.close();
            }
        } catch (Exception exception) {
            logger.error("ERROR", exception);
        }
        return EventBehavior.CONTINUE;
    }

    private void processResponse(ChannelHandlerContext ctx, WrappedChannel channel, Response response) {
        // Future方式
        Optional.ofNullable(response.getSequence()).ifPresent(sequence -> {
            InvokeFuture future = channel.getFutures().remove(sequence);
            if (future != null) {
                if (response.getCause() != null) {
                    future.setCause(response.getCause());
                } else {
                    future.setResult(response);
                }
            }
        });
    }

    private void sendMessageToMqtt(String message) throws InterruptedException {
        Server server = ServerContext.getContext().getServer();
        MqttRequest mqttRequest = new MqttRequest((message.getBytes()));
        if (server.getChannels().size() > 0) {
            for (WrappedChannel ch : server.getChannels().values()) {
                server.send(ch, "mqtt", mqttRequest);
            }
        }
    }
}
