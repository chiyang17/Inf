package com.skill;

import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Random;

public class MinionSummonSkill implements Skill {

    private final LivingEntity source;
    private final List<EntityType> summonTypes;
    private final int baseCount;
    private final int perPlayer;

    public MinionSummonSkill(LivingEntity source, List<EntityType> summonTypes, int baseCount, int perPlayer) {
        this.source = source;
        this.summonTypes = summonTypes;
        this.baseCount = baseCount;
        this.perPlayer = perPlayer;
    }

    @Override
    public void cast(Player ignored, LivingEntity entity) {
        Location loc = source.getLocation();
        int playerCount = loc.getWorld().getNearbyPlayers(loc, 10).size();
        int total = baseCount + playerCount * perPlayer;
        Random random = new Random();

        for (int i = 0; i < total; i++) {
            EntityType type = summonTypes.get(random.nextInt(summonTypes.size()));
            loc.getWorld().spawnEntity(loc, type);
        }
    }
}
