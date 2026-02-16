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
     * Spawns a single particle (Handles Client/Server difference automatically).
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

    // --- BLOOD & LIQUID PRESETS ---

    /**
     * Spawns a spray of particles in a specific direction with a spread cone.
     *
     * @param direction The normalized direction vector to shoot towards.
     * @param speed     The velocity magnitude.
     * @param spread    How wide the cone is (0.0 = laser, 0.5 = shotgun spray).
     */
    public static void spawnDirectionalSpray(Level level, ParticleOptions particle, Vec3 origin, Vec3 direction, int count, double speed, double spread) {
        for (int i = 0; i < count; i++) {
            double vx = direction.x + (random.nextDouble() - 0.5) * spread;
            double vy = direction.y + (random.nextDouble() - 0.5) * spread;
            double vz = direction.z + (random.nextDouble() - 0.5) * spread;

            // Re-normalize slightly to keep speed consistent, or leave as is for varied speed
            Vec3 vel = new Vec3(vx, vy, vz).normalize().scale(speed * (0.8 + random.nextDouble() * 0.4));

            spawn(level, particle, origin.x, origin.y, origin.z, vel.x, vel.y, vel.z);
        }
    }

    /**
     * Spawns particles shooting upwards and falling down in an arc.
     */
    public static void spawnFountain(Level level, ParticleOptions particle, Vec3 center, int count, double height, double radius) {
        for (int i = 0; i < count; i++) {
            double vx = (random.nextDouble() - 0.5) * radius;
            double vz = (random.nextDouble() - 0.5) * radius;
            double vy = height * (0.8 + random.nextDouble() * 0.4);

            spawn(level, particle, center.x, center.y, center.z, vx, vy, vz);
        }
    }

    /**
     * Spawns particles dripping down from a horizontal area.
     */
    public static void spawnDrippingArea(Level level, ParticleOptions particle, Vec3 center, double width, double length, int count) {
        for (int i = 0; i < count; i++) {
            double x = center.x + (random.nextDouble() - 0.5) * width;
            double z = center.z + (random.nextDouble() - 0.5) * length;
            double y = center.y;

            spawn(level, particle, x, y, z, 0, -0.1, 0);
        }
    }

    /**
     * Shape: Crown Splash
     * Shoots particles upwards and outwards in a ring.
     * * @param radius The width of the splash ring at the base.
     * @param upSpeed How high the splash goes.
     * @param outSpeed How fast the ring expands.
     */
    public static void spawnCrownSplash(Level level, ParticleOptions particle, Vec3 center, int count, double radius, double upSpeed, double outSpeed) {
        for (int i = 0; i < count; i++) {
            // Angle around the circle
            double theta = (2 * Math.PI * i) / count;

            // Position on the ring
            double dx = Math.cos(theta);
            double dz = Math.sin(theta);

            // Spawn at the rim
            double x = center.x + dx * radius;
            double z = center.z + dz * radius;
            double y = center.y;

            // Velocity: Up + Outward vector
            spawn(level, particle, x, y, z, dx * outSpeed, upSpeed, dz * outSpeed);
        }
    }

    /**
     * Shape: Arterial Spurt
     * A tight, high-pressure stream that pulses slightly.
     */
    public static void spawnArterialSpurt(Level level, ParticleOptions particle, Vec3 origin, Vec3 direction, int count, double speed) {
        for (int i = 0; i < count; i++) {
            // Slight variation so it looks like a fluid stream, not a laser
            double spread = 0.1;
            double vx = direction.x + (random.nextDouble() - 0.5) * spread;
            double vy = direction.y + (random.nextDouble() - 0.5) * spread;
            double vz = direction.z + (random.nextDouble() - 0.5) * spread;

            // Varied speed simulates "pulsing" pressure
            double currentSpeed = speed * (0.8 + random.nextDouble() * 0.4);

            spawn(level, particle, origin.x, origin.y, origin.z, vx * currentSpeed, vy * currentSpeed, vz * currentSpeed);
        }
    }

    /**
     * <h3>Shape: Hemispherical Burst</h3>
     * An explosion that only goes UP (half-sphere).
     */
    public static void spawnHemisphereExplosion(Level level, ParticleOptions particle, Vec3 center, int count, double radius, double speed) {
        for (int i = 0; i < count; i++) {
            // Random direction in a sphere
            double theta = random.nextDouble() * 2 * Math.PI;
            double phi = Math.acos(2 * random.nextDouble() - 1); // 0 to PI

            // Force phi to be upper hemisphere only (0 to PI/2)
            if (phi > Math.PI / 2) phi = Math.PI - phi;

            double xDir = Math.sin(phi) * Math.cos(theta);
            double yDir = Math.cos(phi); // Always positive (Up)
            double zDir = Math.sin(phi) * Math.sin(theta);

            // Spawn offset slightly from center to avoid clipping
            spawn(level, particle,
                    center.x + xDir * 0.2,
                    center.y + yDir * 0.2,
                    center.z + zDir * 0.2,
                    xDir * speed, yDir * speed, zDir * speed);
        }
    }

    /**
     * <h3>Shape: Spiral Spray</h3>
     * Particles fly out in a rotating spiral pattern.
     */
    public static void spawnSpiralSpray(Level level, ParticleOptions particle, Vec3 center, int count, double radius, double heightSpeed, double rotSpeed) {
        for (int i = 0; i < count; i++) {
            double progress = (double) i / count; // 0.0 to 1.0
            double angle = progress * (Math.PI * 4); // 2 full circles

            double dx = Math.cos(angle) * radius;
            double dz = Math.sin(angle) * radius;

            // Velocity is tangent to the circle (perpendicular to radius)
            double vx = -Math.sin(angle) * rotSpeed;
            double vz = Math.cos(angle) * rotSpeed;

            spawn(level, particle,
                    center.x + dx,
                    center.y + (progress * 0.5), // Slightly rising spawn point
                    center.z + dz,
                    vx, heightSpeed, vz);
        }
    }

    // --- GEOMETRIC SHAPES ---

    /**
     * Spawns particles in a sphere with a custom factory for density gradients.
     */
    public static void spawnSphereGradient(Level level, Vec3 center, double radius, int count, Vec3 baseMotion, Function<Float, ParticleOptions> factory) {
        if (!level.isClientSide) return;

        for (int i = 0; i < count; i++) {
            double rRatio = random.nextDouble();
            double r = radius * rRatio;

            double theta = random.nextDouble() * 2 * Math.PI;
            double phi = Math.acos(2 * random.nextDouble() - 1);

            double x = center.x + r * Math.sin(phi) * Math.cos(theta);
            double y = center.y + r * Math.sin(phi) * Math.sin(theta);
            double z = center.z + r * Math.cos(phi);

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
     * Spawns particles randomly distributed inside a sphere.
     */
    public static void spawnSphereVolume(Level level, ParticleOptions particle, Vec3 center, double radius, int count, Vec3 motion) {
        for (int i = 0; i < count; i++) {
            double r = radius * Math.cbrt(random.nextDouble());
            double theta = random.nextDouble() * 2 * Math.PI;
            double phi = Math.acos(2 * random.nextDouble() - 1);

            double x = center.x + r * Math.sin(phi) * Math.cos(theta);
            double y = center.y + r * Math.sin(phi) * Math.sin(theta);
            double z = center.z + r * Math.cos(phi);

            spawn(level, particle, x, y, z, motion.x, motion.y, motion.z);
        }
    }

    /**
     * Spawns particles on the surface of a sphere.
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
     * Spawns particles in a flat ring on the X/Z plane.
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
     * Spawns particles randomly inside a vertical cylinder.
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
     * Spawns particles along a line between two points.
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
     * Spawns particles exploding outwards from a center point.
     */
    public static void spawnExplosion(Level level, ParticleOptions particle, Vec3 center, int count, double speed, double spread) {
        for (int i = 0; i < count; i++) {
            double vx = (random.nextDouble() - 0.5) * spread;
            double vy = (random.nextDouble() - 0.5) * spread;
            double vz = (random.nextDouble() - 0.5) * spread;

            if (speed > 0) {
                Vec3 dir = new Vec3(vx, vy, vz).normalize().scale(speed);
                vx = dir.x; vy = dir.y; vz = dir.z;
            }

            spawn(level, particle, center.x, center.y, center.z, vx, vy, vz);
        }
    }

    /**
     * Spawns particles in a perfect static circle perimeter.
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
     * Spawns particles randomly distributed inside a flat circle.
     */
    public static void spawnDisc(Level level, ParticleOptions particle, Vec3 center, double radius, int count) {
        for (int i = 0; i < count; i++) {
            double r = radius * Math.sqrt(random.nextDouble());
            double angle = random.nextDouble() * 2 * Math.PI;

            double x = center.x + r * Math.cos(angle);
            double z = center.z + r * Math.sin(angle);

            spawn(level, particle, x, center.y, z, 0, 0, 0);
        }
    }

    /**
     * Spawns a burst of particles radiating outward.
     */
    public static void spawnBurst(Level level, ParticleOptions particle, Vec3 center, int quantity, double intensity) {
        for (int i = 0; i < quantity; i++) {
            double vx = (random.nextDouble() - 0.5) * 2.0;
            double vy = (random.nextDouble() - 0.5) * 2.0;
            double vz = (random.nextDouble() - 0.5) * 2.0;

            double dist = Math.sqrt(vx * vx + vy * vy + vz * vz);
            if (dist > 0.0001) {
                vx = (vx / dist) * intensity;
                vy = (vy / dist) * intensity;
                vz = (vz / dist) * intensity;
            }

            double randomSpeed = 0.5 + (random.nextDouble() * 0.5);
            spawn(level, particle, center.x, center.y, center.z, vx * randomSpeed, vy * randomSpeed, vz * randomSpeed);
        }
    }
}