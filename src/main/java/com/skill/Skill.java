package com.skill;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

public interface Skill {
    void cast(Player target, LivingEntity source);
}
