package com.boss;

import com.config.BossConfig;
import com.skill.GroundPoundSkill;
import com.skill.LightningStrikeSkill;
import com.skill.MinionSummonSkill;
import com.skill.WebTrapSkill;
import org.bukkit.boss.BarColor;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;

public class EliteZombieBoss extends AbstractBoss {

    public EliteZombieBoss(Zombie zombie) {
        super(zombie, "§c精英僵尸王", BarColor.RED);
        registerSkill(new LightningStrikeSkill());
        registerSkill(new WebTrapSkill(BossConfig.getWebTrapChance()));
        registerSkill(new GroundPoundSkill(BossConfig.getGroundPoundCooldown()));
        registerSkill(new MinionSummonSkill(zombie,
                BossConfig.getMinionTypes(),
                BossConfig.getBaseCount(),
                BossConfig.getMinionCountPerPlayer()));
    }

    @Override
    protected void tick() {
        double hpRatio = entity.getHealth() / entity.getMaxHealth();
        if (hpRatio <= 0.5 && hpRatio <= lastHpTrigger - 0.05) {
            lastHpTrigger = hpRatio;
            skills.stream().filter(s -> s instanceof MinionSummonSkill)
                          .forEach(skill -> skill.cast((Player) null, entity));
        }
    }
}
