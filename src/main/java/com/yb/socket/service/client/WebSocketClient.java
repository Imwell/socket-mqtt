package com.yb.socket.service.client;

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

import java.net.InetSocketAddress;

public class WebSocketClient extends BaseClient {

    @Override
    protected void init() {
        super.init();
        if (checkHeartbeat) {
            heartbeatHandler = new WebSocketHeartbeatHandler();
        }
    }

    public ChannelFuture connect(boolean sync) {
        return super.connect(new InetSocketAddress(ip, port), sync, (pipeline) -> {
            pipeline.addLast("httpClientCodec", new HttpClientCodec());
            pipeline.addLast("httpObjectAggregator", new HttpObjectAggregator(8192));
            pipeline.addLast("webSocketClientCompressionHandler", WebSocketClientCompressionHandler.INSTANCE);
            WebSocketClientHandler webSocketClientHandler = new WebSocketClientHandler(
                    WebSocketClientHandshakerFactory.newHandshaker(uri, WebSocketVersion.V13, null, true, new DefaultHttpHeaders())
                    , eventDispatcher);
            pipeline.addLast("webSocketClientHandler", webSocketClientHandler);
        });
    }

//    @Override
//    public Response sendWithSync(Request message, int timeout) {
//        TextWebSocketFrame textWebSocketFrame = new TextWebSocketFrame(message.getMessage().toString());
//        ChannelFuture channelFuture = channel.writeAndFlush(textWebSocketFrame);
//        return new Response();
//    }
}
