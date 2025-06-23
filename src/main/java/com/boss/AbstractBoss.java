package com.boss;

import com.Inf;
import com.managers.BossManager;
import com.skill.Skill;
import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashSet;
import java.util.Set;

public abstract class AbstractBoss {
    protected final LivingEntity entity;
    protected final BossBar bossBar;
    protected double lastHpTrigger = 1.0;
    protected final Set<Skill> skills = new HashSet<>();

    public AbstractBoss(LivingEntity entity, String name, BarColor color) {
        this.entity = entity;
        this.entity.setCustomName(name);
        this.entity.setCustomNameVisible(true);
        this.entity.setPersistent(true);
        this.entity.setRemoveWhenFarAway(false);

        this.bossBar = Bukkit.createBossBar(name, color, BarStyle.SOLID);
        bossBar.setProgress(1.0);
        for (Player player : Bukkit.getOnlinePlayers()) {
            bossBar.addPlayer(player);
        }

        BossManager.setBoss(this);
        startMonitoring();
    }

    public LivingEntity getEntity() { return entity; }
    public boolean isDead() { return entity.isDead() || !entity.isValid(); }

    protected void startMonitoring() {
        new BukkitRunnable() {
            @Override
            public void run() {
                if (isDead()) {
                    bossBar.removeAll();
                    BossManager.clear();
                    cancel();
                    return;
                }
                bossBar.setProgress(entity.getHealth() / entity.getMaxHealth());
                tick();
            }
        }.runTaskTimer(Inf.getInstance(), 0L, 20L);
    }

    protected abstract void tick(); // 每秒更新逻辑
    public void registerSkill(Skill skill) {
        skills.add(skill);
    }
}
