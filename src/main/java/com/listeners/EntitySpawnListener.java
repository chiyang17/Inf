package com.listeners;

import com.boss.EliteZombieBoss;
import com.managers.BossManager;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Zombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;

public class EntitySpawnListener implements Listener {

    @EventHandler
    public void onZombieSpawn(CreatureSpawnEvent event) {
        if (event.getEntityType() == EntityType.ZOMBIE && Math.random() < 0.5 && !BossManager.hasBoss()) {
            Zombie zombie = (Zombie) event.getEntity();
            new EliteZombieBoss(zombie);
        }
    }
}
