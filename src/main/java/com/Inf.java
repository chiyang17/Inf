package com;

import com.commands.PlayerDataCommand;
import com.listeners.player.PlayerJoinAndQuitListener;
import com.server.MyWebSocketServer;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public final class Inf extends JavaPlugin implements Listener {

    private static Inf instance;
    private MyWebSocketServer wsServer;

    @Override
    public void onLoad() {
        // 不需在加载中编写代码，留空即可
        // 无此实现可能会导致启动异常
    }

    @Override
    public void onEnable() {
        // 1. 方便其他类调用
        instance = this;
        // 2. 配置文件外放
        saveDefaultConfig();
        // 3. 保存其他文件
        saveConfig();
        // 4. 接入websocket
        startWebSocketServer();
        // 5.注册指令
        registerCommands();
        // 6. 注册监听器
        registerAListener();
    }

    @Override
    public void onDisable() {
        stopWebSocketServer();
    }


    /**
     * 注册监听器
     */
    public void registerAListener() {
        // 添加监听器
        getServer().getPluginManager().registerEvents(this, this);
        getServer().getPluginManager().registerEvents(new PlayerJoinAndQuitListener(wsServer), this);
        // 其他监听器
        // getServer().getPluginManager().registerEvents(new XXX(), this);
    }

    /**
     * 保存资源文件
     */
    public void saveConfig() {
        List<String> fileNames = new ArrayList<>();
        // 添加文件名
        // fileNames.add("xxx.yml");

        if (fileNames.isEmpty())
            return;

        for (String fileName : fileNames) {
            if (!new File(getDataFolder(), fileName).exists()) {
                saveResource(fileName,false);
            }
        }
    }

    /**
     * 注册指令
     */
    private void registerCommands() {
        // getCommand("inf").setExecutor(new 类());
        // getServer().getPluginCommand("backup").setExecutor(new BackupCommand(this));
        getServer().getPluginCommand("playerdata").setExecutor(new PlayerDataCommand(this));
    }

    // 统一设置配置文件方法
    public void setDefaultConfig(String key, Object value, boolean isReload){
        instance.getConfig().set(key, value);
        instance.saveConfig();
        if (isReload){
            instance.reloadConfig();
        }
    }

    /**
     * 获取实例
     * @return 实例本体   调用实例有两种方法，第一种直接Inf.getInstance(),  第二种 调用类使用构造方法
     */
    public static Inf getInstance() {
        return instance;
    }


    private void startWebSocketServer() {
        try {
            // 获取是否启动
            boolean websocketEnable = getConfig().getBoolean("websocket.enable" , false);
            if (websocketEnable){
                // 获取端口
                int port = getConfig().getInt("websocket.port" ,  8777);
                wsServer = new MyWebSocketServer(this, port);
                wsServer.start();
                getLogger().info("websocket启动中...");
            }
        }catch (Exception e){
            getLogger().severe("启动websocket服务器时发生异常！");
        }
    }

    private void stopWebSocketServer() {
        try {
            if (wsServer != null) {
                // 优雅地停止 WebSocket 服务器，超时设为1秒
                wsServer.stop(1000);
                getLogger().info("WebSocket 服务器已停止。");
            }
        } catch (InterruptedException e) {
            getLogger().severe("停止 WebSocket 服务器时发生中断异常！");
        }
    }
}
