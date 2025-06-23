package com.config;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;
import java.util.stream.Collectors;

public class BossConfig {
    private static FileConfiguration config;

    public static void load(JavaPlugin plugin) {
        plugin.saveDefaultConfig();
        config = plugin.getConfig();
    }

    public static double getLightningChance() {
        return config.getDouble("skills.lightning-chance", 0.2);
    }

    public static double getWebTrapChance() {
        return config.getDouble("skills.web-trap-chance", 0.3);
    }

    public static int getGroundPoundCooldown() {
        return config.getInt("skills.ground-pound-cooldown", 10);
    }

    public static List<EntityType> getMinionTypes() {
        return config.getStringList("skills.minion-summon.mobs")
                .stream()
                .map(String::toUpperCase)
                .map(EntityType::valueOf)
                .collect(Collectors.toList());
    }

    public static int getBaseCount() {
        return config.getInt("skills.minion-summon.base-count", 1);
    }

    public static int getMinionCountPerPlayer() {
        return config.getInt("skills.minion-summon.per-player", 1);
    }
}
