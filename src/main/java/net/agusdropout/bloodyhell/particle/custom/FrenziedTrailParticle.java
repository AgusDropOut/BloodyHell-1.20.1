package net.agusdropout.bloodyhell.particle.custom;

import com.mojang.blaze3d.vertex.VertexConsumer;
import dev.kosmx.playerAnim.core.util.Vec3f;
import net.agusdropout.bloodyhell.particle.ParticleOptions.TinyBloomParticleOptions;
import net.agusdropout.bloodyhell.util.visuals.ParticleHelper;
import net.agusdropout.bloodyhell.client.render.effect.FrenziedTrailRenderManager;
import net.agusdropout.bloodyhell.client.shader.ShaderTime;
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

public class FrenziedTrailParticle extends Particle {

    private final Deque<Vec3> history = new ArrayDeque<>();
    private final int maxHistory = 15;
    private final float width;
    private float frequencyOffset;

    protected FrenziedTrailParticle(ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
        super(level, x, y, z);
        this.xd = xSpeed;
        this.yd = ySpeed;
        this.zd = zSpeed;
        this.width = 0.4F;
        this.lifetime = 60 + this.random.nextInt(20);
        this.alpha = 1.0F;
        this.rCol = 1.0F;
        this.gCol = 1.0F;
        this.bCol = 1.0F;

        this.frequencyOffset = this.random.nextFloat() * ((float)Math.PI * 2.0F);

        this.history.addFirst(new Vec3(this.x, this.y, this.z));
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

            this.history.addFirst(new Vec3(this.x, this.y, this.z));
            if (this.history.size() > maxHistory) {
                this.history.removeLast();
            }

            if (this.level.random.nextInt(3) == 0) {
                double sparkVelX = this.xd + (this.level.random.nextDouble() - 0.5D) * 0.15D;
                double sparkVelY = this.yd + (this.level.random.nextDouble() * 0.15D);
                double sparkVelZ = this.zd + (this.level.random.nextDouble() - 0.5D) * 0.15D;

                float size = 0.03F + this.level.random.nextFloat() * 0.05F;

                Vector3f color = ParticleHelper.gradient3(random.nextFloat(), new Vector3f(0.8F, 0.3F, 0.05F), new Vector3f(0.8F, 0.5F, 0.05F), new Vector3f(0.7F, 0.6F, 0.02F));

                TinyBloomParticleOptions bloodSparkOption = new TinyBloomParticleOptions(color, size);

                this.level.addParticle(bloodSparkOption,
                        this.x, this.y, this.z,
                        sparkVelX, sparkVelY, sparkVelZ);
            }
        }
    }

    @Override
    public void render(VertexConsumer buffer, Camera camera, float partialTicks) {
        if (this.history.size() < 2) return;

        float time = ShaderTime.seconds();
        Vec3 cameraPos = camera.getPosition();

        FrenziedTrailRenderManager.addTrail(
                new ArrayList<>(this.history),
                cameraPos,
                this.width,
                this.rCol,
                this.gCol,
                this.bCol,
                this.alpha,
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
            return new FrenziedTrailParticle(level, x, y, z, xSpeed, ySpeed, zSpeed);
        }
    }
}