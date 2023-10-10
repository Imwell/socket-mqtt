package com.yb.socket.service.server.pipeline;

import com.yb.socket.service.SocketType;
import io.netty.channel.ChannelPipeline;

import java.util.ArrayList;
import java.util.List;

/**
 * @author xinxing
 * @date 2023/9/20 19:42
 */
public class PipLineFactory {

    private final List<PipLineInterface> lineInterface;

    public PipLineFactory() {
        List<PipLineInterface> list = new ArrayList<>();
        list.add(new WebSocketPipLine());
        list.add(new MqttPipLine());
        list.add(new MqttWsPipLine());
        this.lineInterface = list;
    }

    public static PipLineFactory newInstant() {
        return new PipLineFactory();
    }

    public void getPipLine(ChannelPipeline pipeline, SocketType socketType) {
        System.out.println(socketType);
        lineInterface.forEach(pipLineInterface -> {
            if (pipLineInterface.checkSocketType(socketType)) {
                pipLineInterface.addHandler(pipeline);
            }
        });
    }
}
