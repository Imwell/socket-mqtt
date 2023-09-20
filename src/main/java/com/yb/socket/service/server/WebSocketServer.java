package com.yb.socket.service.server;

import com.yb.socket.pojo.Request;
import com.yb.socket.service.WrappedChannel;
import io.netty.channel.ChannelFuture;

/**
 * @author xinxing
 * @date 2023/9/20 19:39
 */
public class WebSocketServer extends Server {

    public ChannelFuture send(WrappedChannel channel, Request request) throws InterruptedException {
        return channel.send(request);
    }
}
