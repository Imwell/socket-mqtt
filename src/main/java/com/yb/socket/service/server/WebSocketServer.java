package com.yb.socket.service.server;

import com.yb.socket.pojo.Request;
import com.yb.socket.service.WrappedChannel;
import io.netty.channel.ChannelFuture;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;

/**
 * @author xinxing
 * @date 2023/9/20 19:39
 */
public class WebSocketServer extends Server {

    @Override
    protected void init() {
        super.init();
        openCount = false;
    }

    public ChannelFuture send(WrappedChannel channel, Request request) throws InterruptedException {
//        return channel.writeAndFlush(new TextWebSocketFrame("服务器发送：" + request.getMessage()));
        return channel.send(request);
    }
}
