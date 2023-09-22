package com.yb.socket.service.ws;

import com.alibaba.fastjson.JSONObject;
import com.yb.socket.listener.WebSocketClientMessageEventListener;
import com.yb.socket.pojo.Request;
import com.yb.socket.pojo.Response;
import com.yb.socket.service.SocketType;
import com.yb.socket.service.client.WebSocketClient;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;

public class WebSocketClientTest {

    private static final Logger logger = LoggerFactory.getLogger(WebSocketClientTest.class);

    public static void main(String[] args) {
        WebSocketClientTest webSocketClientTest = new WebSocketClientTest();
        webSocketClientTest.start();
    }

    public void start() {
        final String broker = "localhost";
        final int port = 8000;
        WebSocketClient webSocketClient = new WebSocketClient();
        webSocketClient.setIp(broker);
        webSocketClient.setPort(port);
        webSocketClient.setOpenExecutor(true);
        webSocketClient.setSocketType(SocketType.WS);
        webSocketClient.addEventListener(new WebSocketClientMessageEventListener());
        webSocketClient.connect(true);

        JSONObject message = new JSONObject();
        message.put("action", "echo");
        message.put("message", "hello world!");

        Request request = new Request();
        request.setSequence(0);
        request.setMessage(message);
        Response response = webSocketClient.sendWithSync(request, 5000);

        logger.info("成功接收到同步的返回: '{}'.", response);

//        sendMessage(request);
    }

    private void sendMessage(Object message) {

        message = new TextWebSocketFrame(JSONObject.toJSONString(message));
        System.out.println(message);

        TextWebSocketFrame textWebSocketFrame = new TextWebSocketFrame(JSONObject.toJSONString(message));
        System.out.println(textWebSocketFrame);
    }
}
