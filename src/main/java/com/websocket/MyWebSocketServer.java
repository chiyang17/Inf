package com.websocket;

import com.Inf;
import com.websocket.dispatcher.WsStrategyDispatcher;
import com.websocket.strategy.WsMessageStrategy;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;
import org.json.JSONObject;

import java.net.InetSocketAddress;

public class MyWebSocketServer extends WebSocketServer {

    // 保存主插件的实例，以便我们访问 Bukkit API 和调度器
    private final Inf plugin;
    private final WsStrategyDispatcher dispatcher;

    public MyWebSocketServer(Inf plugin, int port, WsStrategyDispatcher dispatcher) {
        super(new InetSocketAddress(port));
        this.plugin = plugin;
        this.dispatcher = dispatcher;
    }

    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {
        // 当有新的连接建立时，在控制台打印日志
        plugin.getLogger().info("新的 WebSocket 连接已建立: " + conn.getRemoteSocketAddress());
    }

    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
        // 当连接关闭时
        plugin.getLogger().info("WebSocket 连接已关闭: " + conn.getRemoteSocketAddress());
    }

    @Override
    public void onMessage(WebSocket conn, String message) {
        // 解析 JSON
        JSONObject json = new JSONObject(message);

        // 校验 type
        if (!json.has("type")) {
            conn.send("消息缺少 type 字段");
            return;
        }

        String type = json.getString("type");

        // 获取策略
        WsMessageStrategy strategy = dispatcher.getStrategy(type);
        if (strategy == null) {
            conn.send("未知的消息类型: " + type);
            return;
        }

        // 交给策略处理
        strategy.handle(conn, message);

        // 收到来自外部的消息时
        plugin.getLogger().info("收到来自 WebSocket 的消息: " + message);
        // 这里可以处理来自外部的消息，比如执行备份等
    }

    @Override
    public void onError(WebSocket conn, Exception ex) {
        // 当发生错误时
        plugin.getLogger().severe("WebSocket 发生错误: " + ex.getMessage());
        ex.printStackTrace();
    }

    @Override
    public void onStart() {
        // 当服务器成功启动时
        plugin.getLogger().info("WebSocket 服务器已在地址 " + this.getAddress() + " 上启动！");
    }

}