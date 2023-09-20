package com.yb.socket.service.server.pipeline;

import com.yb.socket.service.SocketType;
import io.netty.channel.ChannelPipeline;

/**
 * @author xinxing
 * @date 2023/9/20 19:40
 */
public interface PipLineInterface {

    public boolean checkSocketType(SocketType socketType);

    public void addHandler(ChannelPipeline pipeline);


}
