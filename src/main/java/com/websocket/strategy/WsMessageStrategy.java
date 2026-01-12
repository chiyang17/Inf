package com.websocket.strategy;

import org.java_websocket.WebSocket;

public interface WsMessageStrategy {

    /**
     * 处理 WebSocket 消息
     */
    void handle(WebSocket conn, String rawMessage);
}
