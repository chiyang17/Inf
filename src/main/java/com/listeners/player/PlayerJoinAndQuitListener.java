package com.listeners.player;

import com.alibaba.fastjson2.JSONObject;
import com.server.MyWebSocketServer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.net.InetSocketAddress;

public class PlayerJoinAndQuitListener implements Listener {

    private final MyWebSocketServer webSocketServer;

    public PlayerJoinAndQuitListener(MyWebSocketServer webSocketServer) {
        this.webSocketServer = webSocketServer;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        JSONObject playerInfo = getPlayer(0, player);
        webSocketServer.broadcast(playerInfo.toJSONString());
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        JSONObject playerInfo = getPlayer(1, player);
        webSocketServer.broadcast(playerInfo.toJSONString());
    }


    /**
     * 获取玩家信息
     * @param type 0：玩家加入服务器 ， 1：玩家离开服务器
     * @param player 玩家
     * @return 玩家信息
     */
    JSONObject getPlayer(int type, Player player){
        // 构造玩家信息
        JSONObject playerInfo = new JSONObject();
        // 0：玩家加入服务器 ， 1：玩家离开服务器
        playerInfo.put("type", type);
        playerInfo.put("name", player.getName());
        playerInfo.put("uuid", player.getUniqueId().toString());
        playerInfo.put("time", System.currentTimeMillis());
        playerInfo.put("world", player.getWorld().getName());
        playerInfo.put("location", player.getLocation().toVector().toString());
        playerInfo.put("health", player.getHealth());
        playerInfo.put("displayName", player.getDisplayName());
        InetSocketAddress address = player.getAddress();
        playerInfo.put("address", address == null ? null : address.toString());
        playerInfo.put("ip", address == null ? null : address.getAddress().getHostAddress());
        playerInfo.put("port", address == null ? null : address.getPort());
        playerInfo.put("isOp", player.isOp());
        playerInfo.put("gameMode", player.getGameMode());
        // 玩家客户端世界 （tick）
        playerInfo.put("playerTime", player.getPlayerTime());
        playerInfo.put("scoreboard", player.getScoreboard());
        return playerInfo;
    }
}
