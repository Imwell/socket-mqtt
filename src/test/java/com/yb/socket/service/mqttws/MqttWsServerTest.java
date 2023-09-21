package com.yb.socket.service.mqttws;

import com.alibaba.fastjson.JSONObject;
import com.yb.socket.pojo.MqttRequest;
import com.yb.socket.pojo.Request;
import com.yb.socket.pojo.Response;
import com.yb.socket.service.SocketType;
import com.yb.socket.service.WrappedChannel;
import com.yb.socket.service.client.WebSocketClient;
import com.yb.socket.service.mqtt.EchoMessageEventListener;
import com.yb.socket.service.server.Server;
import com.yb.socket.service.ws.WebSocketClientTest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;

/**
 * MQTT web socket
 * @author daoshenzzg@163.com
 * @date 2018/12/30 18:41
 */
public class MqttWsServerTest {

    private static final Logger logger = LoggerFactory.getLogger(MqttWsServerTest.class);

    public static void main(String[] args) throws Exception {
        Server server = new Server();
        server.setPort(8001);
        server.setStatusPort(8002);
        server.setOpenCount(true);
        server.setCheckHeartbeat(true);
        server.setOpenStatus(true);
        server.setOpenExecutor(true);
        server.addEventListener(new EchoMessageEventListener());
        server.setSocketType(SocketType.MQTT_WS);
        server.bind();

        WebSocketClientTest webSocketClientTest = new WebSocketClientTest();
        webSocketClientTest.start();

        //模拟推送
//        String message = "this is a mqtt message!";
//        MqttRequest mqttRequest = new MqttRequest((message.getBytes()));
//        while (true) {
//            if (server.getChannels().size() > 0) {
//                logger.info("模拟推送消息");
//                for (WrappedChannel channel : server.getChannels().values()) {
//                    server.send(channel, "mqtt", mqttRequest);
//                }
//            }
//            Thread.sleep(5000L);
//        }
    }
}
