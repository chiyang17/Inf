package com.commands;

import com.Inf;
import org.bukkit.Bukkit;
import org.bukkit.command.*;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class PlayerDataCommand implements CommandExecutor {

    private final Inf plugin;

    public PlayerDataCommand(Inf plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
                             @NotNull String label, @NotNull String[] args) {

        if (args.length == 0) {
            sender.sendMessage("§c用法: /playerdata <save|load|reload> [player]");
            return true;
        }

        String subCommand = args[0].toLowerCase();
        String path = "allow-player-command.playerdata." + subCommand;

        boolean isPlayer = sender instanceof Player;
        boolean allowPlayerExecute = plugin.getConfig().getBoolean(path, false);

        // 控制台必须指定玩家名（仅对 save/load）
        if (!isPlayer && (subCommand.equals("save") || subCommand.equals("load")) && args.length < 2) {
            sender.sendMessage("§c控制台请指定玩家名：/playerdata " + subCommand + " <player>");
            return true;
        }

        // 玩家权限控制
        if (isPlayer && !allowPlayerExecute) {
            sender.sendMessage("§c你没有权限执行此子命令：" + subCommand);
            return true;
        }

        // 获取目标玩家
        Player target = null;
        if (args.length >= 2) {
            target = Bukkit.getPlayerExact(args[1]);
            if (target == null) {
                sender.sendMessage("§c未找到该玩家或玩家不在线：" + args[1]);
                return true;
            }
        } else if (isPlayer) {
            target = (Player) sender;
        }

        // 执行具体子命令
        switch (subCommand) {
            case "save" -> handleSave(sender, target);
            case "load" -> handleLoad(sender, target);
            case "reload" -> handleReload(sender);
            default -> sender.sendMessage("§c未知子命令，请使用 save/load/list/reload");
        }

        return true;
    }

    private void handleSave(CommandSender sender, Player player) {
        if (player == null) {
            sender.sendMessage("§c无法保存数据：目标玩家不存在");
            return;
        }
        player.saveData(); // 保存玩家信息
        sender.sendMessage("§a成功保存玩家数据：" + player.getName());
    }

    private void handleLoad(CommandSender sender, Player player) {
        if (player == null) {
            sender.sendMessage("§c无法加载数据：目标玩家不存在");
            return;
        }
        player.loadData(); // 加载玩家信息
        sender.sendMessage("§a成功加载玩家数据：" + player.getName());
    }

    private void handleReload(CommandSender sender) {
        plugin.reloadConfig();
        sender.sendMessage("§a配置已重新加载");
    }
}
