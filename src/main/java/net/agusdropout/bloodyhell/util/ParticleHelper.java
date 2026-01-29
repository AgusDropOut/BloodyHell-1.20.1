package net.agusdropout.bloodyhell.util;

import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

import java.util.function.Function;

public class ParticleHelper {

    private static final RandomSource random = RandomSource.create();

    /**
     *Spawns a single particle (Handles Client/Server difference automatically).
     */
    public static void spawn(Level level, ParticleOptions particle, double x, double y, double z, double vx, double vy, double vz) {
        if (level instanceof ServerLevel serverLevel) {
            // Server: count 0 allows defining exact velocity
            serverLevel.sendParticles(particle, x, y, z, 0, vx, vy, vz, 1.0);
        } else {
            // Client
            level.addParticle(particle, x, y, z, vx, vy, vz);
        }
    }

    // --- COLOR GRADIENT UTILS ---

    /**
     * Interpolates between 3 colors based on a ratio (0.0 -> 1.0).
     * @param ratio 0.0 = Start, 0.5 = Mid, 1.0 = End
     */
    public static Vector3f gradient3(float ratio, Vector3f start, Vector3f mid, Vector3f end) {
        if (ratio < 0.5f) {
            return lerpVector(start, mid, ratio * 2f);
        } else {
            return lerpVector(mid, end, (ratio - 0.5f) * 2f);
        }
    }

    private static Vector3f lerpVector(Vector3f start, Vector3f end, float t) {
        return new Vector3f(
                Mth.lerp(t, start.x(), end.x()),
                Mth.lerp(t, start.y(), end.y()),
                Mth.lerp(t, start.z(), end.z())
        );
    }

    // --- GRADIENT SHAPES ---

    /**
     * Spawns particles in a sphere with a custom factory that determines the particle based on distance from center.
     * @param factory A function that takes the 'ratio' (0.0 center to 1.0 edge) and returns the ParticleOptions.
     */
    public static void spawnSphereGradient(Level level, Vec3 center, double radius, int count, Vec3 baseMotion, Function<Float, ParticleOptions> factory) {
        if (!level.isClientSide) return;

        for (int i = 0; i < count; i++) {
            // Linear random radius = Dense Core, Sparse Edge (Exactly what you wanted)
            double rRatio = random.nextDouble();
            double r = radius * rRatio;

            double theta = random.nextDouble() * 2 * Math.PI;
            double phi = Math.acos(2 * random.nextDouble() - 1);

            double x = center.x + r * Math.sin(phi) * Math.cos(theta);
            double y = center.y + r * Math.sin(phi) * Math.sin(theta);
            double z = center.z + r * Math.cos(phi);

            // Generate the particle based on how far it is from the center (rRatio)
            ParticleOptions p = factory.apply((float) rRatio);

            spawn(level, p, x, y, z, baseMotion.x, baseMotion.y, baseMotion.z);
        }
    }

    /**
     * Spawns particles in a cylinder with gradient logic.
     */
    public static void spawnCylinderGradient(Level level, Vec3 base, double radius, double height, int count, double upSpeed, Function<Float, ParticleOptions> factory) {
        if (!level.isClientSide) return;

        for (int i = 0; i < count; i++) {
            double rRatio = random.nextDouble();
            double r = radius * rRatio;

            double angle = random.nextDouble() * 2 * Math.PI;
            double yOffset = random.nextDouble() * height;

            double x = base.x + r * Math.cos(angle);
            double z = base.z + r * Math.sin(angle);

            ParticleOptions p = factory.apply((float) rRatio);

            spawn(level, p, x, base.y + yOffset, z, 0, upSpeed, 0);
        }
    }

    /**
     * <h3>Shape: Random Sphere Volume</h3>
     * Spawns particles randomly distributed <b>inside</b> a sphere.
     * <br> Used for: <i>BloodFireMeteor (Body), BloodFireColumn (Charging)</i>
     */
    public static void spawnSphereVolume(Level level, ParticleOptions particle, Vec3 center, double radius, int count, Vec3 motion) {
        for (int i = 0; i < count; i++) {
            double r = radius * Math.cbrt(random.nextDouble()); // Uniform distribution
            double theta = random.nextDouble() * 2 * Math.PI;
            double phi = Math.acos(2 * random.nextDouble() - 1);

            double x = center.x + r * Math.sin(phi) * Math.cos(theta);
            double y = center.y + r * Math.sin(phi) * Math.sin(theta);
            double z = center.z + r * Math.cos(phi);

            spawn(level, particle, x, y, z, motion.x, motion.y, motion.z);
        }
    }

    /**
     * <h3>Shape: Hollow Sphere Surface</h3>
     * Spawns particles on the <b>surface</b> of a sphere.
     * <br> Used for: <i>Cyclops (Eye particles), VisceralProjectile (Impact)</i>
     */
    public static void spawnHollowSphere(Level level, ParticleOptions particle, Vec3 center, double radius, int count, double speed) {
        for (int i = 0; i < count; i++) {
            double theta = random.nextDouble() * 2 * Math.PI;
            double phi = Math.acos(2 * random.nextDouble() - 1);

            double dx = Math.sin(phi) * Math.cos(theta);
            double dy = Math.cos(phi);
            double dz = Math.sin(phi) * Math.sin(theta);

            spawn(level, particle,
                    center.x + dx * radius,
                    center.y + dy * radius,
                    center.z + dz * radius,
                    dx * speed, dy * speed, dz * speed);
        }
    }

    /**
     * <h3>Shape: Flat Ring</h3>
     * Spawns particles in a circle on the X/Z plane.
     * <br> Used for: <i>SanguineSacrifice, BloodFireColumn (Impact), Impaler (Impact)</i>
     * @param expansionSpeed Positive to expand outward, 0 for static.
     */
    public static void spawnRing(Level level, ParticleOptions particle, Vec3 center, double radius, int count, double expansionSpeed) {
        for (int i = 0; i < count; i++) {
            double angle = (2 * Math.PI * i) / count;
            double dx = Math.cos(angle);
            double dz = Math.sin(angle);

            spawn(level, particle,
                    center.x + dx * radius,
                    center.y,
                    center.z + dz * radius,
                    dx * expansionSpeed, 0, dz * expansionSpeed);
        }
    }

    /**
     * <h3>Shape: Cylinder / Pillar</h3>
     * Spawns particles randomly inside a vertical cylinder.
     * <br> Used for: <i>BlasphemousArm (Magic), BloodFireColumn (Main Body)</i>
     */
    public static void spawnCylinder(Level level, ParticleOptions particle, Vec3 base, double radius, double height, int count, double upwardSpeed) {
        for (int i = 0; i < count; i++) {
            double r = radius * Math.sqrt(random.nextDouble());
            double angle = random.nextDouble() * 2 * Math.PI;
            double yOffset = random.nextDouble() * height;

            double x = base.x + r * Math.cos(angle);
            double z = base.z + r * Math.sin(angle);

            spawn(level, particle, x, base.y + yOffset, z, 0, upwardSpeed, 0);
        }
    }

    /**
     * <h3>Shape: Line / Beam</h3>
     * Spawns particles along a line between two points.
     * <br> Used for: <i>Cyclops (Laser), BloodSlash (Trail), OmenGazer (Magic Line)</i>
     */
    public static void spawnLine(Level level, ParticleOptions particle, Vec3 start, Vec3 end, int count, Vec3 randomSpread) {
        for (int i = 0; i < count; i++) {
            double progress = (double) i / count;
            double x = Mth.lerp(progress, start.x, end.x);
            double y = Mth.lerp(progress, start.y, end.y);
            double z = Mth.lerp(progress, start.z, end.z);

            double sx = (random.nextDouble() - 0.5) * randomSpread.x;
            double sy = (random.nextDouble() - 0.5) * randomSpread.y;
            double sz = (random.nextDouble() - 0.5) * randomSpread.z;

            spawn(level, particle, x + sx, y + sy, z + sz, 0, 0, 0);
        }
    }

    /**
     * <h3>Shape: Random Burst / Explosion</h3>
     * Spawns particles exploding outwards from a center point.
     * <br> Used for: <i>HornedWorm (Burrow), Veinraver (Debris)</i>
     */
    public static void spawnExplosion(Level level, ParticleOptions particle, Vec3 center, int count, double speed, double spread) {
        for (int i = 0; i < count; i++) {
            double vx = (random.nextDouble() - 0.5) * spread;
            double vy = (random.nextDouble() - 0.5) * spread;
            double vz = (random.nextDouble() - 0.5) * spread;

            // Add normalized directional speed if needed, otherwise just spread
            if (speed > 0) {
                Vec3 dir = new Vec3(vx, vy, vz).normalize().scale(speed);
                vx = dir.x; vy = dir.y; vz = dir.z;
            }

            spawn(level, particle, center.x, center.y, center.z, vx, vy, vz);
        }
    }

    /**
     * <h3>Shape: Circle (Perimeter)</h3>
     * Spawns particles in a perfect static circle on the X/Z plane.
     * <br> Used for: <i>SpellBook casting circles, Magic Seals</i>
     */
    public static void spawnCircle(Level level, ParticleOptions particle, Vec3 center, double radius, int count) {
        for (int i = 0; i < count; i++) {
            double angle = (2 * Math.PI * i) / count;
            double x = center.x + Math.cos(angle) * radius;
            double z = center.z + Math.sin(angle) * radius;

            spawn(level, particle, x, center.y, z, 0, 0, 0);
        }
    }

    /**
     * <h3>Shape: Disc (Filled Circle)</h3>
     * Spawns particles randomly distributed <b>inside</b> a flat circle.
     * <br> Used for: <i>Blood puddles, Ground AoE markers</i>
     */
    public static void spawnDisc(Level level, ParticleOptions particle, Vec3 center, double radius, int count) {
        for (int i = 0; i < count; i++) {
            double r = radius * Math.sqrt(random.nextDouble()); // Sqrt ensures uniform distribution
            double angle = random.nextDouble() * 2 * Math.PI;

            double x = center.x + r * Math.cos(angle);
            double z = center.z + r * Math.sin(angle);

            spawn(level, particle, x, center.y, z, 0, 0, 0);
        }
    }
}