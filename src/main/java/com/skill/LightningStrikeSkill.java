package com.skill;

import com.config.BossConfig;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

public class LightningStrikeSkill implements Skill {
    @Override
    public void cast(Player target, LivingEntity source) {
        if (Math.random() < BossConfig.getLightningChance()) {
            target.getWorld().strikeLightningEffect(target.getLocation());
        }
    }
}
