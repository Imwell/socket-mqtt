package com.yb.socket.service.server.pipeline;

import com.yb.socket.service.SocketType;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.mqtt.MqttDecoder;
import io.netty.handler.codec.mqtt.MqttEncoder;

/**
 * @author xinxing
 * @date 2023/9/20 19:51
 */
public class MqttPipLine implements PipLineInterface{
    @Override
    public boolean checkSocketType(SocketType socketType) {
        return SocketType.MQTT.equals(socketType);
    }

    @Override
    public void addHandler(ChannelPipeline pipeline) {
        pipeline.addLast("mqttDecoder", new MqttDecoder());
        pipeline.addLast("mqttEncoder", MqttEncoder.INSTANCE);
    }
}
