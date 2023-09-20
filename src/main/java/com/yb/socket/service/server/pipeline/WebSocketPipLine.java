package com.yb.socket.service.server.pipeline;

import com.yb.socket.service.SocketType;
import com.yb.socket.service.server.WebSocketFrameHandler;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.codec.http.websocketx.extensions.compression.WebSocketServerCompressionHandler;

/**
 * @author xinxing
 * @date 2023/9/20 19:53
 */
public class WebSocketPipLine implements PipLineInterface {

    private static final String WEBSOCKET_PATH = "/";

    @Override
    public boolean checkSocketType(SocketType socketType) {
        return SocketType.WS.equals(socketType);
    }

    @Override
    public void addHandler(ChannelPipeline pipeline) {

        pipeline.addLast(new HttpServerCodec());
        pipeline.addLast(new HttpObjectAggregator(65536));
        pipeline.addLast(new WebSocketServerCompressionHandler());
        pipeline.addLast(new WebSocketServerProtocolHandler(WEBSOCKET_PATH, null, true));
        pipeline.addLast(new WebSocketFrameHandler());
    }
}
