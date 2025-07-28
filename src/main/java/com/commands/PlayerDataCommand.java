package com.commands;

import com.Inf;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.*;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

public class PlayerDataCommand implements CommandExecutor {

    private static final Logger log = LoggerFactory.getLogger(PlayerDataCommand.class);
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

        // 控制台必须指定玩家名（仅对 save/load/list）
        if (!isPlayer && (subCommand.equals("save") || subCommand.equals("load") || subCommand.equals("list")) && args.length < 2) {
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
            case "load" -> {
                if (args.length < 3) {
                    sender.sendMessage("§c请指定备份文件名：/playerdata load <玩家> <文件名>");
                } else {
                    handleLoad(sender, target, args[2]);
                }
            }
            case "list" -> handleList(sender, args);
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

        player.saveData(); // 主动保存

        File source = new File(Bukkit.getWorlds().getFirst().getWorldFolder(), "playerdata/" + player.getUniqueId() + ".dat");

        String timestamp = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(new Date());
        File target = new File(plugin.getDataFolder(), "backups/" + player.getUniqueId() + "/" + timestamp + ".dat");


        try {
            copyFile(source , target);
            sender.sendMessage("§a成功保存玩家数据：" + player.getName());
        } catch (IOException e){
            sender.sendMessage("§c保存玩家数据失败：" + e.getMessage());
            log.error("保存玩家数据失败：{}", e.getMessage(), e);
        }


    }

    private void handleLoad(CommandSender sender, Player player, String filename) {
        if (player == null) {
            sender.sendMessage("§c无法加载数据：目标玩家不存在");
            return;
        }

        UUID uuid = player.getUniqueId();
        File source = new File(plugin.getDataFolder(), "backups/" + uuid + "/" + filename);
        File target = new File(Bukkit.getWorlds().getFirst().getWorldFolder(), "playerdata/" + uuid + ".dat");

        if (!source.exists()) {
            sender.sendMessage("§c未找到该备份文件：" + filename);
            return;
        }

        // 踢出玩家（防止写入冲突）
        player.kick(Component.text("§e正在恢复您的数据，请稍后重新登录"));

        try {
            // 等待玩家退出后替换文件并 reload
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                try {
                    copyFile(source, target);
                    sender.sendMessage("§a成功恢复玩家数据：" + player.getName());
                } catch (IOException e) {
                    sender.sendMessage("§c恢复失败：" + e.getMessage());
                    log.error("恢复失败：{}", e.getMessage(), e);
                }
            }, 40L); // 延迟 2 秒（40 tick）

        } catch (Exception e) {
            sender.sendMessage("§c异常：" + e.getMessage());
            log.error("异常：{}", e.getMessage(), e);
        }
    }

    private void handleList(CommandSender sender, String[] args) {
        File backupRoot = new File(plugin.getDataFolder(), "backups");

        if (!backupRoot.exists() || !backupRoot.isDirectory()) {
            sender.sendMessage("§e尚无任何备份记录");
            return;
        }

        boolean isPlayer = sender instanceof Player;
        boolean isAdmin = !isPlayer || sender.isOp();
        String targetName = null;
        UUID targetUUID = null;

        // 管理员指定玩家名
        if (args.length >= 2) {
            targetName = args[1];
            Player player = Bukkit.getPlayerExact(targetName);
            if (player != null) {
                targetUUID = player.getUniqueId();
            } else {
                try {
                    // 文件夹名就是UUID（离线玩家）
                    File playerDir = new File(backupRoot, targetName);
                    if (playerDir.exists()) {
                        targetUUID = UUID.fromString(targetName);
                    } else {
                        sender.sendMessage("§c未找到该玩家的任何记录");
                        return;
                    }
                } catch (IllegalArgumentException e) {
                    sender.sendMessage("§c未知玩家或UUID格式不正确");
                    return;
                }
            }
        } else if (isPlayer) {
            // 玩家只能查看自己的
            Player p = (Player) sender;
            targetUUID = p.getUniqueId();
            targetName = p.getName();
        }

        if (targetUUID != null) {
            File playerFolder = new File(backupRoot, targetUUID.toString());
            if (!playerFolder.exists()) {
                sender.sendMessage("§e未找到该玩家的备份记录：" + targetName);
                return;
            }

            File[] files = playerFolder.listFiles((dir, name) -> name.endsWith(".dat"));
            if (files == null || files.length == 0) {
                sender.sendMessage("§e无备份记录：" + targetName);
                return;
            }

            sender.sendMessage("§a" + targetName + " 的保存记录：");
            for (File f : files) {
                String filename = f.getName().replace(".dat", "");
                sender.sendMessage(" §7- " + filename);
            }

        } else if (isAdmin) {
            // 管理员查看所有人
            sender.sendMessage("§a所有玩家的保存记录：");
            File[] playerDirs = backupRoot.listFiles(File::isDirectory);
            if (playerDirs == null || playerDirs.length == 0) {
                sender.sendMessage("§e无任何玩家备份记录");
                return;
            }

            for (File dir : playerDirs) {
                String uuidStr = dir.getName();
                UUID uuid;
                try {
                    uuid = UUID.fromString(uuidStr);
                } catch (IllegalArgumentException e) {
                    continue;
                }

                String name = Bukkit.getOfflinePlayer(uuid).getName();
                File[] files = dir.listFiles((d, n) -> n.endsWith(".dat"));
                if (files == null || files.length == 0) continue;

                sender.sendMessage("§b" + (name != null ? name : uuidStr) + "：");
                for (File f : files) {
                    String time = f.getName().replace(".dat", "");
                    sender.sendMessage(" §7- " + time);
                }
            }
        } else {
            sender.sendMessage("§c你没有权限查看其他玩家的记录");
        }
    }



    private void handleReload(CommandSender sender) {
        plugin.reloadConfig();
        sender.sendMessage("§a配置已重新加载");
    }

    private void copyFile(File source, File target) throws IOException {
        Files.createDirectories(target.getParentFile().toPath()); // 确保父目录存在
        Files.copy(source.toPath(), target.toPath(), StandardCopyOption.REPLACE_EXISTING);
    }

}
