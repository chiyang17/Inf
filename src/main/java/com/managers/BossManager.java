package com.managers;

import com.boss.AbstractBoss;

public class BossManager {
    private static AbstractBoss current;

    public static boolean hasBoss() { return current != null && !current.isDead(); }
    public static AbstractBoss getBoss() { return current; }
    public static void setBoss(AbstractBoss boss) { current = boss; }
    public static void clear() { current = null; }
}
