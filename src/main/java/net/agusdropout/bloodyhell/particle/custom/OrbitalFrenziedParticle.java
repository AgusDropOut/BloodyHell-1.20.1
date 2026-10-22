package net.agusdropout.bloodyhell.particle.custom;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.agusdropout.bloodyhell.particle.ParticleOptions.TinyBloomParticleOptions;
import net.agusdropout.bloodyhell.util.visuals.ParticleHelper;
import net.agusdropout.bloodyhell.client.shader.ShaderTime;
import net.agusdropout.bloodyhell.client.render.effect.Effects;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;

public class OrbitalFrenziedParticle extends Particle {

    private final Deque<Vec3> historyCore = new ArrayDeque<>();
    private final Deque<Vec3> historyOrbit1 = new ArrayDeque<>();
    private final Deque<Vec3> historyOrbit2 = new ArrayDeque<>();

    private final int maxHistory = 15;
    private final float coreWidth;
    private final float orbitWidth;
    private float frequencyOffset;

    private final double orbitRadius = 0.35D;
    private final double orbitSpeed = 0.4D;

    protected OrbitalFrenziedParticle(ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
        super(level, x, y, z);
        this.xd = xSpeed;
        this.yd = ySpeed;
        this.zd = zSpeed;

        this.coreWidth = 0.4F;
        this.orbitWidth = 0.25F;

        this.lifetime = 60 + this.random.nextInt(20);
        this.alpha = 1.0F;

        this.frequencyOffset = this.random.nextFloat() * ((float)Math.PI * 2.0F);

        this.historyCore.addFirst(new Vec3(this.x, this.y, this.z));
        this.historyOrbit1.addFirst(new Vec3(this.x + orbitRadius, this.y, this.z));
        this.historyOrbit2.addFirst(new Vec3(this.x - orbitRadius, this.y, this.z));
    }

    @Override
    public void tick() {
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;

        if (this.age++ >= this.lifetime) {
            this.remove();
        } else {
            this.xd += Math.sin((this.age * 0.4F) + this.frequencyOffset) * 0.08D;
            this.zd += Math.cos((this.age * 0.4F) + this.frequencyOffset) * 0.08D;
            this.yd += 0.02D;

            this.move(this.xd, this.yd, this.zd);

            this.xd *= 0.9D;
            this.yd *= 0.9D;
            this.zd *= 0.9D;

            this.historyCore.addFirst(new Vec3(this.x, this.y, this.z));

            double currentAngle = this.age * this.orbitSpeed;
            double offsetX = Math.cos(currentAngle) * this.orbitRadius;
            double offsetZ = Math.sin(currentAngle) * this.orbitRadius;

            this.historyOrbit1.addFirst(new Vec3(this.x + offsetX, this.y, this.z + offsetZ));
            this.historyOrbit2.addFirst(new Vec3(this.x - offsetX, this.y, this.z - offsetZ));

            if (this.historyCore.size() > maxHistory) {
                this.historyCore.removeLast();
                this.historyOrbit1.removeLast();
                this.historyOrbit2.removeLast();
            }

            if (this.level.random.nextFloat() < 0.8f) {

                boolean firstOrbit = this.level.random.nextBoolean();
                double sign = firstOrbit ? 1.0D : -1.0D;

                double spawnX = this.x + (offsetX * sign);
                double spawnZ = this.z + (offsetZ * sign);

                double tangVelX = -this.orbitRadius * this.orbitSpeed * Math.sin(currentAngle) * sign;
                double tangVelZ = this.orbitRadius * this.orbitSpeed * Math.cos(currentAngle) * sign;

                double sparkVelX = this.xd + tangVelX + (this.level.random.nextDouble() - 0.5D) * 0.05D;
                double sparkVelY = this.yd + (this.level.random.nextDouble() * 0.05D);
                double sparkVelZ = this.zd + tangVelZ + (this.level.random.nextDouble() - 0.5D) * 0.05D;

                float size = 0.03F + this.level.random.nextFloat() * 0.05F;

                Vector3f color = ParticleHelper.gradient3(this.random.nextFloat(),
                        new Vector3f(0.8F, 0.3F, 0.05F),
                        new Vector3f(0.8F, 0.5F, 0.05F),
                        new Vector3f(0.7F, 0.6F, 0.02F));

                TinyBloomParticleOptions bloomOption = new TinyBloomParticleOptions(color, size);

                this.level.addParticle(bloomOption,
                        spawnX, this.y, spawnZ,
                        sparkVelX, sparkVelY, sparkVelZ);
            }
        }
    }

    @Override
    public void render(VertexConsumer buffer, Camera camera, float partialTicks) {
        if (this.historyCore.size() < 2) return;

        float time = ShaderTime.seconds();
        Vec3 cameraPos = camera.getPosition();
        float lifeFade = 1.0F - ((float)this.age / (float)this.lifetime);

        Effects.FRENZIED_TRAIL.submit(
                new ArrayList<>(this.historyCore),
                cameraPos,
                this.coreWidth,
                1.0F, 1.0F, 1.0F,
                this.alpha,
                time
        );

        Effects.RADIANT_TRAIL.submit(
                new ArrayList<>(this.historyOrbit1),
                cameraPos,
                this.orbitWidth,
                1.0F, 1.0F, 1.0F,
                this.alpha * lifeFade,
                time
        );

        Effects.RADIANT_TRAIL.submit(
                new ArrayList<>(this.historyOrbit2),
                cameraPos,
                this.orbitWidth,
                1.0F, 1.0F, 1.0F,
                this.alpha * lifeFade,
                time
        );
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.CUSTOM;
    }

    public static class Provider implements ParticleProvider<SimpleParticleType> {
        @Override
        public Particle createParticle(SimpleParticleType type, ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            return new OrbitalFrenziedParticle(level, x, y, z, xSpeed, ySpeed, zSpeed);
        }
    }
}