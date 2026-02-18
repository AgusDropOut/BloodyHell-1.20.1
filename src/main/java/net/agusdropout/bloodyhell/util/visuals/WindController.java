package net.agusdropout.bloodyhell.util.visuals;

import java.util.Random;

public class WindController {
    public static double windX = 0;
    public static double windZ = 0;
    private static double targetWindX = 0;
    private static double targetWindZ = 0;
    private static int ticksUntilChange = 0;
    private static final Random random = new Random();

    public static void tick() {
        if (--ticksUntilChange <= 0) {
            ticksUntilChange = 100 + random.nextInt(100);
            double angle = random.nextDouble() * 2 * Math.PI;
            double speed = 0.5 + random.nextDouble() * 0.1;
            targetWindX = Math.cos(angle) * speed;
            targetWindZ = Math.sin(angle) * speed;
        }

        double smoothness = 0.03 + random.nextDouble() * 0.04;
        windX += (targetWindX - windX) * smoothness;
        windZ += (targetWindZ - windZ) * smoothness;
    }
}
