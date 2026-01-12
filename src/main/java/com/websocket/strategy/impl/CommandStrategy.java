package com.websocket.strategy.impl;

import com.Inf;
import com.websocket.inter.WsAction;
import com.websocket.strategy.WsMessageStrategy;
import org.java_websocket.WebSocket;

@WsAction("command")
public class CommandStrategy implements WsMessageStrategy {

    private final Inf plugin;

    public CommandStrategy(Inf plugin) {
        this.plugin = plugin;
    }

    @Override
    public void handle(WebSocket conn, String rawMessage) {

    }
}
