package com.yb.socket.service.client;

import com.yb.socket.pojo.Request;
import com.yb.socket.pojo.Response;
import io.netty.channel.ChannelFuture;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;

public class WebSocketClient extends BaseClient {

//    @Override
//    public Response sendWithSync(Request message, int timeout) {
//        TextWebSocketFrame textWebSocketFrame = new TextWebSocketFrame(message.getMessage().toString());
//        ChannelFuture channelFuture = channel.writeAndFlush(textWebSocketFrame);
//        return new Response();
//    }
}
