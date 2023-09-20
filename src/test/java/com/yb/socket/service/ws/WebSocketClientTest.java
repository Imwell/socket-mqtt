package com.yb.socket.service.ws;

import com.alibaba.fastjson.JSONObject;
import com.yb.socket.pojo.Request;
import com.yb.socket.pojo.Response;
import com.yb.socket.service.SocketType;
import com.yb.socket.service.client.WebSocketClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;

public class WebSocketClientTest {

    private static final Logger logger = LoggerFactory.getLogger(WebSocketClientTest.class);

    public static void main(String[] args) {

        final String broker = "localhost";
        final int port = 8000;
        WebSocketClient webSocketClient = new WebSocketClient();
        webSocketClient.setIp(broker);
        webSocketClient.setPort(port);
        webSocketClient.setSocketType(SocketType.WS);
        webSocketClient.connect(new InetSocketAddress(broker, port), true);

        for (int i = 0; i < 2; i++) {
            JSONObject message = new JSONObject();
            message.put("action", "echo");
            message.put("message", "hello world!");

            Request request = new Request();
            request.setSequence(i);
            request.setMessage(message);
            Response response = webSocketClient.sendWithSync(request, 3000);

            logger.info("成功接收到同步的返回: '{}'.", response);
        }
    }
}
