package com;

public class Demo {

    private final Inf plugin;

    public Demo(Inf plugin) {
        this.plugin = plugin;
    }

    public void run() {
        // 构造器调用
        plugin.getLogger().info("Demo");
        // 直接调用
        Inf.getInstance().getLogger().info("Demo");

        // 调用修改配置文件方法
        plugin.setDefaultConfig("key", "value", true);
        Inf.getInstance().setDefaultConfig("key", "value", true);

        // 调用默认配置文件方法
        plugin.getConfig().getString("key");
        Inf.getInstance().getConfig().getString("key");
    }

}
