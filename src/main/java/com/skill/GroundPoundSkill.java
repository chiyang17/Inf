package com.skill;

import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.List;

public class GroundPoundSkill implements Skill {

    private final long cooldownMillis;
    private long lastUse = 0;

    public GroundPoundSkill(long cooldownSeconds) {
        this.cooldownMillis = cooldownSeconds * 1000L;
    }

    @Override
    public void cast(Player ignored, LivingEntity source) {
        long now = System.currentTimeMillis();
        if (now - lastUse < cooldownMillis) return;
        lastUse = now;

        List<Player> nearby = (List<Player>) source.getWorld().getNearbyPlayers(source.getLocation(), 5);
        for (Player player : nearby) {
            player.setVelocity(new Vector(0, 1, 0));
            player.damage(4.0, source);
        }

        source.getWorld().playSound(source.getLocation(), Sound.ENTITY_IRON_GOLEM_ATTACK, 1.0f, 1.0f);
        source.getWorld().spawnParticle(Particle.EXPLOSION, source.getLocation(), 1);
    }
}
