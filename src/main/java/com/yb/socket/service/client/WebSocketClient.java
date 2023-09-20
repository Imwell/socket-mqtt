package com.yb.socket.service.client;

import com.yb.socket.exception.SocketRuntimeException;
import com.yb.socket.exception.SocketTimeoutException;
import com.yb.socket.pojo.Request;
import com.yb.socket.pojo.Response;
import com.yb.socket.service.WrappedChannel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.handler.codec.http.DefaultHttpHeaders;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketClientHandshakerFactory;
import io.netty.handler.codec.http.websocketx.WebSocketVersion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.SocketAddress;
import java.util.concurrent.TimeUnit;

public class WebSocketClient extends BaseClient {
    private static final Logger logger = LoggerFactory.getLogger(WebSocketClient.class);

    @Override
    protected ChannelFuture doConnect(SocketAddress socketAddress, boolean sync) {
        // 连接server
        curServer = socketAddress;
        try {
            ChannelFuture future = bootstrap.connect(uri.getHost(), port).sync();
            future.addListener((ChannelFutureListener) ch -> {
                ch.await();
                if (ch.isSuccess()) {
                    channel = new WrappedChannel(ch.channel());
                    if (logger.isDebugEnabled()) {
                        logger.debug("Connect to '{}' success.", socketAddress);
                    }
                } else {
                    logger.error("Failed to connect to '{}', caused by: '{}'.", socketAddress, ch.cause());
                }
                semaphore.release(Integer.MAX_VALUE - semaphore.availablePermits());
            });
            future.channel().closeFuture();
            if (sync) {
                Throwable cause = null;
                try {
                    if (!semaphore.tryAcquire(connectTimeout, TimeUnit.MILLISECONDS)) {
                        cause = new SocketTimeoutException("time out.");
                    }
                } catch (InterruptedException ex) {
                    throw new SocketTimeoutException(ex);
                }

                if (cause != null) {
                    throw new SocketRuntimeException(cause);
                }
            }
            return future;
        } catch (Exception ex) {
            throw new SocketRuntimeException(ex);
        }
    }

    @Override
    public Response sendWithSync(Request message, int timeout) {
        TextWebSocketFrame textWebSocketFrame = new TextWebSocketFrame(message.getMessage().toString());
        ChannelFuture channelFuture = channel.writeAndFlush(textWebSocketFrame);
        return new Response();
    }
}
