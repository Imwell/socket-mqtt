package com.yb.socket.service.client;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.yb.socket.codec.JsonDecoder;
import com.yb.socket.codec.JsonEncoder;
import com.yb.socket.exception.SocketRuntimeException;
import com.yb.socket.listener.DefaultMessageEventListener;
import com.yb.socket.pojo.Request;
import com.yb.socket.pojo.Response;
import com.yb.socket.util.AddressUtil;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.timeout.IdleStateHandler;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

/**
 * @author daoshenzzg@163.com
 * @date 2018/12/30 16:41
 */
public class Client extends BaseClient {
    private static final Logger logger = LoggerFactory.getLogger(Client.class);
    /**
     * 服务名称
     */
    protected String serviceName;
    /**
     * 注册中心地址: IP1:PORT1,IP2:PORT2
     */
    protected String centerAddr;
    private List<SocketAddress> serverList;
    /**
     * 是否发生异常
     */
    private AtomicBoolean errorFlag = new AtomicBoolean(false);
    /**
     * 是否正在断线重连
     */
    private AtomicBoolean reConnecting = new AtomicBoolean(false);

    @Override
    protected void init() {
        super.init();
        // 将一些handler放在这里初始化是为了防止多例的产生。
        if (checkHeartbeat) {
            heartbeatHandler = new ClientHeartbeatHandler();
        }
        this.addEventListener(new DefaultMessageEventListener());
    }

    public ChannelFuture connect() {
        return connect(true);
    }

    public ChannelFuture connect(boolean sync) {
        SocketAddress socketAddress;
        if (serverList == null) {
            if (StringUtils.isBlank(centerAddr)) {
                socketAddress = new InetSocketAddress(ip, port);
                serverList = new ArrayList<>();
                serverList.add(socketAddress);
            } else {
                if (serverList == null) {
                    serverList = getAddressByCenter();
                }
                if (serverList == null || serverList.size() <= 0) {
                    throw new SocketRuntimeException("can not get server list from centerAddr property.");
                }
            }
        }

        for (SocketAddress server : serverList) {
            try {
                return super.connect(server, sync, this::consumer);
            } catch (Exception ex) { // socket timeout
            }
        }
        throw new SocketRuntimeException("can not connect to server[ " + serverList + "]");

    }

    private void consumer(ChannelPipeline pipeline) {
        if (checkHeartbeat) {
            IdleStateHandler idleStateHandler = new IdleStateHandler(readerIdleTimeSeconds,
                    writerIdleTimeSeconds, allIdleTimeSeconds);
            pipeline.addLast("idleStateHandler", idleStateHandler);
            pipeline.addLast("heartbeatHandler", heartbeatHandler);
        }
        // 注册事件分发Handler
        pipeline.addLast("dispatchHandler", dispatchHandler);
    }

    @Override
    public ChannelFuture send(Object message) {
        if (errorFlag.get()) {
            reConnect();
        }
        return super.send(message);
    }

    @Override
    public Response sendWithSync(Request message) {
        if (errorFlag.get()) {
            reConnect();
        }
        return super.sendWithSync(message);
    }

    @Override
    public Response sendWithSync(Request message, int timeout) {
        if (errorFlag.get()) {
            reConnect();
        }
        return super.sendWithSync(message, timeout);
    }

    private List<SocketAddress> getAddressByCenter() {
        BaseClient baseClient = new BaseClient();
        try {
            SocketAddress[] addresses = AddressUtil.parseAddress(centerAddr);
            if (addresses == null || addresses.length <= 0) {
                return null;
            }

            for (int i = 0; i < addresses.length; i++) {
                try {
                    baseClient.setCheckHeartbeat(false);
                    baseClient.addChannelHandler("decoder", JsonDecoder::new);
                    baseClient.addChannelHandler("encoder", JsonEncoder::new);
                    //连接注册中心
                    ChannelFuture future = baseClient.connect(addresses[i], true, this::consumer);
                    future.await();
                    if (future.isSuccess() && future.cause() == null) {
                        //获取server列表
                        Request request = new Request();
                        request.setSequence(0);
                        JSONObject json = new JSONObject();
                        json.put("action", "getServerInfo");
                        json.put("service", this.getServiceName());
                        request.setMessage(json.toString());
                        Response response = baseClient.sendWithSync(request);
                        // 解析server列表
                        if (response != null && response.getCode() == Response.SUCCESS) {
                            JSONArray jsonArray = JSON.parseObject(response.getResult().toString()).getJSONArray("server_list");

                            List<SocketAddress> serverList = new ArrayList<>(jsonArray.size());
                            for (int index = 0, size = jsonArray.size(); index < size; index++) {
                                JSONObject row = jsonArray.getJSONObject(index);
                                SocketAddress server = new InetSocketAddress(row.getString("ip"), row.getIntValue("port"));
                                serverList.add(server);
                            }

                            return serverList;
                        }
                    }
                } catch (Exception ex) {
                    logger.error("Failed to connect to server '{}'.", addresses[i], ex);
                }
            }

            return null;
        } catch (Exception ex) {
            throw new SocketRuntimeException("Failed to get server address from center.", ex);
        } finally {
            baseClient.shutdown();
        }
    }

    /**
     * 断线重连
     */
    private void reConnect() {
        if (!errorFlag.get()) {
            return;
        }

        try {
            if (!reConnecting.get()) {
                reConnecting.set(true);

                //重连三次当前server
                for (int i = 1; i <= 3; i++) {
                    try {
                        ChannelFuture future = this.doConnect(curServer, true);
                        future.await();
                        if (future.isSuccess() && future.cause() == null) {
                            logger.info("尝试第 {} 次重连到 '{}' 成功!", i, curServer);
                            errorFlag.set(false);
                            return;
                        } else {
                            throw new Exception(future.cause());
                        }
                    } catch (Exception ex) {
                        logger.error("尝试第 {} 次重连到 '{}' 失败!", i, curServer, ex);
                    }
                }

                //重连不上当前server，则尝试连接到serverList里的其它server
                for (SocketAddress server : serverList) {
                    if (server.equals(curServer)) {
                        continue;
                    }
                    try {
                        ChannelFuture future = this.doConnect(server, true);
                        future.await();
                        if (future.isSuccess() && future.cause() == null) {
                            logger.info("尝试重连到 '{}' 成功!", server);

                            errorFlag.set(false);
                            return;
                        } else {
                            throw new Exception(future.cause());
                        }
                    } catch (Exception ex) {
                        logger.error("尝试重连到 '{}' 失败!", server);
                    }
                }
            }
        } finally {
            reConnecting.set(false);
        }
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getCenterAddr() {
        return centerAddr;
    }

    public void setCenterAddr(String centerAddr) {
        this.centerAddr = centerAddr;
    }

    public List<SocketAddress> getServerList() {
        return serverList;
    }

    public void setServerList(List<SocketAddress> serverList) {
        this.serverList = serverList;
    }

    public AtomicBoolean getErrorFlag() {
        return errorFlag;
    }

    public void setErrorFlag(AtomicBoolean errorFlag) {
        this.errorFlag = errorFlag;
    }

    public AtomicBoolean getReConnecting() {
        return reConnecting;
    }

    public void setReConnecting(AtomicBoolean reConnecting) {
        this.reConnecting = reConnecting;
    }
}