package net.agusdropout.bloodyhell.client;



public class ClientBossBarData {
    private static int health;
    private static int maxHealth;
    private static boolean dead;
    private static boolean isNear;
    private static int bossID;

    public static void setCurrentBoss(int hp, int maxHp, boolean isDead, boolean near,  int id) {
        health = hp;
        maxHealth = maxHp;
        dead = isDead;
        isNear = near;
        bossID = id;

    }

    public static int getHealth() { return health; }
    public static int getMaxHealth() { return maxHealth; }
    public static boolean isDead() { return dead; }
    public static boolean isNear() { return isNear; }
    public static int getBossID() { return bossID; }
    public static void clear() { dead = true; }
}