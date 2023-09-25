package com.yb.socket.listener;

import com.alibaba.fastjson.JSONObject;
import com.yb.socket.pojo.Request;
import com.yb.socket.pojo.Response;
import com.yb.socket.service.WrappedChannel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WebSocketServerMessageEventListener implements MessageEventListener{

    private static final Logger logger = LoggerFactory.getLogger(WebSocketServerMessageEventListener.class);

    @Override
    public EventBehavior channelRead(ChannelHandlerContext ctx, WrappedChannel channel, Object frame) {

        if (frame instanceof TextWebSocketFrame) {
            // Send the uppercase string back.
            String msg = ((TextWebSocketFrame) frame).text();
            logger.info("收到消息: {}", msg);
            Response response = new Response();
            try {
                Request request = JSONObject.parseObject(msg, Request.class);
                response.setResult("服务器发送：" + request.getMessage());
                response.setSequence(request.getSequence());
                response.setCode(200);
            } catch (Exception e) {
                logger.error("ParseMessageError", e);
                response.setResult(msg);
            }

            ctx.channel().writeAndFlush(new TextWebSocketFrame(JSONObject.toJSONString(response)));
        }
        return EventBehavior.CONTINUE;
    }

}
