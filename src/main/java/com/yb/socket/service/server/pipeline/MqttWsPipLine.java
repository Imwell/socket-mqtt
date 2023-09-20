package com.yb.socket.service.server.pipeline;

import com.yb.socket.codec.MqttWebSocketCodec;
import com.yb.socket.service.SocketType;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.codec.mqtt.MqttDecoder;
import io.netty.handler.codec.mqtt.MqttEncoder;

/**
 * @author xinxing
 * @date 2023/9/20 19:52
 */
public class MqttWsPipLine implements PipLineInterface{

    private String webSocketPath = "/";

    private String mqttVersion = "server, mqtt, mqttv3.1, mqttv3.1.1, mqttv5.0";

    @Override
    public boolean checkSocketType(SocketType socketType) {
        return SocketType.MQTT_WS.equals(socketType);
    }

    @Override
    public void addHandler(ChannelPipeline pipeline) {
        pipeline.addLast("httpServerCodec", new HttpServerCodec());
        pipeline.addLast("httpObjectAggregator", new HttpObjectAggregator(65536));
        pipeline.addLast("mqttWebSocketCodec", new MqttWebSocketCodec());
        pipeline.addLast("mqttDecoder", new MqttDecoder());
        pipeline.addLast("mqttEncoder", MqttEncoder.INSTANCE);

        WebSocketServerProtocolHandler webSocketHandler = new WebSocketServerProtocolHandler(webSocketPath, mqttVersion);
        pipeline.addLast("webSocketHandler", webSocketHandler);
    }
}
