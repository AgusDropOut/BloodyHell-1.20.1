package net.agusdropout.bloodyhell.entity.soul;

import net.agusdropout.bloodyhell.particle.ParticleOptions.MagicParticleOptions;
import net.agusdropout.bloodyhell.util.visuals.ParticleHelper;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

public enum BloodSoulType {
    BLOOD(
            new Vector3f(1.0f, 0.0f, 0.0f),
            new Vector3f(1.0f, 0.6f, 0.8f)
    ),
    CORRUPTED(
            new Vector3f(0.2f, 0.0f, 0.0f),
            new Vector3f(0.6f, 0.0f, 0.0f)
    ),
    INFECTED(
            new Vector3f(0.1f, 0.8f, 0.2f),
            new Vector3f(0.6f, 0.8f, 0.1f)
    );

    private final Vector3f startColor;
    private final Vector3f endColor;

    BloodSoulType(Vector3f start, Vector3f end) {
        this.startColor = start;
        this.endColor = end;
    }

    public void spawnParticles(Level level, Vec3 pos, RandomSource random, float scaleMultiplier) {
        float ratio = random.nextFloat();

        Vector3f color = new Vector3f(
                startColor.x + (endColor.x - startColor.x) * ratio,
                startColor.y + (endColor.y - startColor.y) * ratio,
                startColor.z + (endColor.z - startColor.z) * ratio
        );

        float particleSize = 0.5F * scaleMultiplier;

        double spread = 0.3 * scaleMultiplier;

        ParticleHelper.spawn(level, new MagicParticleOptions(color, particleSize, false, 20),
                pos.x + (random.nextDouble() - 0.5) * spread,
                pos.y + (random.nextDouble() - 0.5) * spread,
                pos.z + (random.nextDouble() - 0.5) * spread,
                0, 0, 0);
    }
}