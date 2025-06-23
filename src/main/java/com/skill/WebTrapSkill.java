package com.skill;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Random;

public class WebTrapSkill implements Skill {

    private final double chance;

    public WebTrapSkill(double chance) {
        this.chance = chance;
    }

    @Override
    public void cast(Player target, LivingEntity source) {
        if (target == null || Math.random() > chance) return;

        Block block = target.getLocation().getBlock();
        if (block.getType() == Material.AIR) {
            block.setType(Material.COBWEB);

            // 5秒后移除蛛网
            new BukkitRunnable() {
                @Override
                public void run() {
                    if (block.getType() == Material.COBWEB) {
                        block.setType(Material.AIR);
                    }
                }
            }.runTaskLater(JavaPlugin.getProvidingPlugin(getClass()), 100L);
        }
    }
}
