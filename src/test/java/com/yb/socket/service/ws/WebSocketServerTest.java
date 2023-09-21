package com.yb.socket.service.ws;

import com.yb.socket.listener.DefaultWebSocketMessageEventListener;
import com.yb.socket.pojo.MqttRequest;
import com.yb.socket.pojo.Request;
import com.yb.socket.service.SocketType;
import com.yb.socket.service.WrappedChannel;
import com.yb.socket.service.server.WebSocketServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author xinxing
 * @date 2023/9/20 23:39
 */
public class WebSocketServerTest {

    private final static Logger logger = LoggerFactory.getLogger(WebSocketServerTest.class);

    public static void main(String[] args) throws InterruptedException {

        WebSocketServer server = new WebSocketServer();
        server.setPort(8000);
        server.setCheckHeartbeat(true);
        server.setSocketType(SocketType.WS);
        server.addEventListener(new DefaultWebSocketMessageEventListener());
        server.bind();

        //模拟推送
        String message = "this is a web socket message!";
        while (true) {
            if (server.getChannels().size() > 0) {
                logger.info("模拟推送消息");
                for (WrappedChannel channel : server.getChannels().values()) {
                    Request request = new Request();
                    request.setMessage(message);
                    server.send(channel, request);
                }
            }
            Thread.sleep(5000L);
        }
    }

}
