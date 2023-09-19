package com.yb.socket.config;

import com.yb.socket.service.SocketType;

public class BrokerProperties {

    /**
     * socket类型
     */
    private SocketType socketType = SocketType.NORMAL;
    /**
     * 绑定端口，默认为8000
     */
    private int port = 8000;
    /**
     * 多IP情况下绑定指定的IP(可以不设置)
     */
    private String ip;
    /**
     * 是否启用keepAlive
     */
    private boolean keepAlive;
    /**
     * 是否启用tcpNoDelay
     */
    private boolean tcpNoDelay;
    /**
     * 工作线程池大小
     */
    private int workerCount;
    /**
     * 是否开户业务处理线程池
     */
    private boolean openExecutor;
    /**
     * 消息事件业务处理线程池最小保持的线程数
     */
    private int corePoolSize;
    /**
     * 消息事件业务处理线程池最大线程数
     */
    private int maximumPoolSize;
    /**
     * 消息事件业务处理线程池队列最大值
     */
    private int queueCapacity;
    /**
     * 设置是否心跳检查
     */
    private boolean checkHeartbeat;
    /**
     * 心跳检查时的读空闲时间
     */
    private int readerIdleTimeSeconds;
    /**
     * 心跳检查时的写空闲时间
     */
    private int writerIdleTimeSeconds;
    /**
     * 心跳检查时的读写空闲时间
     */
    private int allIdleTimeSeconds;

    public SocketType getSocketType() {
        return socketType;
    }

    public void setSocketType(SocketType socketType) {
        this.socketType = socketType;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public boolean isKeepAlive() {
        return keepAlive;
    }

    public void setKeepAlive(boolean keepAlive) {
        this.keepAlive = keepAlive;
    }

    public boolean isTcpNoDelay() {
        return tcpNoDelay;
    }

    public void setTcpNoDelay(boolean tcpNoDelay) {
        this.tcpNoDelay = tcpNoDelay;
    }

    public int getWorkerCount() {
        return workerCount;
    }

    public void setWorkerCount(int workerCount) {
        this.workerCount = workerCount;
    }

    public boolean isOpenExecutor() {
        return openExecutor;
    }

    public void setOpenExecutor(boolean openExecutor) {
        this.openExecutor = openExecutor;
    }

    public int getCorePoolSize() {
        return corePoolSize;
    }

    public void setCorePoolSize(int corePoolSize) {
        this.corePoolSize = corePoolSize;
    }

    public int getMaximumPoolSize() {
        return maximumPoolSize;
    }

    public void setMaximumPoolSize(int maximumPoolSize) {
        this.maximumPoolSize = maximumPoolSize;
    }

    public int getQueueCapacity() {
        return queueCapacity;
    }

    public void setQueueCapacity(int queueCapacity) {
        this.queueCapacity = queueCapacity;
    }

    public boolean isCheckHeartbeat() {
        return checkHeartbeat;
    }

    public void setCheckHeartbeat(boolean checkHeartbeat) {
        this.checkHeartbeat = checkHeartbeat;
    }

    public int getReaderIdleTimeSeconds() {
        return readerIdleTimeSeconds;
    }

    public void setReaderIdleTimeSeconds(int readerIdleTimeSeconds) {
        this.readerIdleTimeSeconds = readerIdleTimeSeconds;
    }

    public int getWriterIdleTimeSeconds() {
        return writerIdleTimeSeconds;
    }

    public void setWriterIdleTimeSeconds(int writerIdleTimeSeconds) {
        this.writerIdleTimeSeconds = writerIdleTimeSeconds;
    }

    public int getAllIdleTimeSeconds() {
        return allIdleTimeSeconds;
    }

    public void setAllIdleTimeSeconds(int allIdleTimeSeconds) {
        this.allIdleTimeSeconds = allIdleTimeSeconds;
    }
}
