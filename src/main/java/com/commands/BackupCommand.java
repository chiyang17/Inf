package com.commands;

import com.Inf;
import com.managers.BackupManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Arrays;

public class BackupCommand implements CommandExecutor {

    private final Inf plugin;

    public BackupCommand(Inf plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        sender.sendMessage("§e正在异步执行备份，请稍候...");
        
        plugin.getServer().getScheduler().runTask(plugin, task -> {
            try {
                BackupManager backupManager = new BackupManager(
                    Arrays.asList(
                        Paths.get("world"),
                        Paths.get("world_nether"),
                        Paths.get("world_the_end")
                    ),
                    Paths.get("backups")
                );
                backupManager.backup();
                sender.sendMessage("§a备份成功！");
            } catch (IOException e) {
                sender.sendMessage("§c备份失败: " + e.getMessage());
                e.printStackTrace();
            }
        });

        return true;
    }
}
