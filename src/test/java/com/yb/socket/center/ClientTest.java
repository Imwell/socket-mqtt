package com.yb.socket.center;

import com.alibaba.fastjson.JSONObject;
import com.yb.socket.codec.JsonDecoder;
import com.yb.socket.codec.JsonEncoder;
import com.yb.socket.pojo.Request;
import com.yb.socket.service.client.Client;

/**
 * @author daoshenzzg@163.com
 * @date 2019/1/7 22:32
 */
public class ClientTest {

    public static void main(String[] args) throws Exception {
        Client client = new Client();
        client.setCheckHeartbeat(false);
        client.setCenterAddr("127.0.0.1:9000,127.0.0.1:9010");
        client.addChannelHandler("decoder", JsonDecoder::new);
        client.addChannelHandler("encoder", JsonEncoder::new);
        client.connect();

        JSONObject message = new JSONObject();
        message.put("action", "echo");
        message.put("message", "hello");

        for (int i = 0; i < 5; i++) {
            Request request = new Request();
            request.setSequence(i);
            request.setMessage(message);
            client.send(request);
            Thread.sleep(5000L);
        }
    }
}
