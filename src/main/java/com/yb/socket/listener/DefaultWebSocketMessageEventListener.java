package com.yb.socket.listener;

import com.yb.socket.future.InvokeFuture;
import com.yb.socket.pojo.Response;
import com.yb.socket.service.WrappedChannel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.websocketx.CloseWebSocketFrame;
import io.netty.handler.codec.http.websocketx.PongWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultWebSocketMessageEventListener implements MessageEventListener{

    private static final Logger logger = LoggerFactory.getLogger(DefaultWebSocketMessageEventListener.class);

    @Override
    public EventBehavior channelRead(ChannelHandlerContext ctx, WrappedChannel channel, Object frame) {

        if (frame instanceof TextWebSocketFrame) {
            // Send the uppercase string back.
            String request = ((TextWebSocketFrame) frame).text();
            logger.info("收到消息: {}", request);
            ctx.channel().writeAndFlush(new TextWebSocketFrame("服务器发送：" + request));
        }
        return EventBehavior.CONTINUE;
    }

}
