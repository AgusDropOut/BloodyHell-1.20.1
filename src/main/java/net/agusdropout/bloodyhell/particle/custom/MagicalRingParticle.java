package net.agusdropout.bloodyhell.particle.custom;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.agusdropout.bloodyhell.particle.ParticleOptions.MagicalRingParticleOptions;
import net.agusdropout.bloodyhell.util.visuals.manager.MagicRingRenderManager;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.joml.Vector3f;

import javax.annotation.Nullable;

@OnlyIn(Dist.CLIENT)
public class MagicalRingParticle extends Particle {

    private final float radius;
    private final float ringHeight;

    protected MagicalRingParticle(ClientLevel level, double x, double y, double z, double maxLifeTime, MagicalRingParticleOptions options) {
        super(level, x, y, z);
        this.lifetime = (maxLifeTime == 0) ? 100 : (int) maxLifeTime;

        Vector3f color = options.getColor();
        this.rCol = color.x();
        this.gCol = color.y();
        this.bCol = color.z();
        this.alpha = 0.8f;

        this.radius = options.getRadius();
        this.ringHeight = options.getHeight();
    }

    @Override
    public void tick() {
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;

        if (this.age++ >= this.lifetime) {
            this.remove();
        } else {
            float lifeRatio = (float) this.age / (float) this.lifetime;
            if (lifeRatio > 0.7f) {
                // Fade out smoothly at the end of its life
                this.alpha = 0.8f * (1.0f - ((lifeRatio - 0.7f) / 0.3f));
            }
        }
    }

    @Override
    public void render(VertexConsumer ignored, Camera camera, float partialTicks) {
        Vec3 camPos = camera.getPosition();
        float px = (float) (Mth.lerp(partialTicks, xo, x) - camPos.x());
        float py = (float) (Mth.lerp(partialTicks, yo, y) - camPos.y());
        float pz = (float) (Mth.lerp(partialTicks, zo, z) - camPos.z());

        float time = (this.age + partialTicks) / 10.0f;

        MagicRingRenderManager.addRing(px, py, pz, this.radius, this.ringHeight, time, this.rCol, this.gCol, this.bCol, this.alpha);
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.CUSTOM;
    }

    @OnlyIn(Dist.CLIENT)
    public static class Provider implements ParticleProvider<MagicalRingParticleOptions> {
        @Nullable
        @Override
        public Particle createParticle(MagicalRingParticleOptions options, ClientLevel level, double x, double y, double z, double vx, double vy, double vz) {
            return new MagicalRingParticle(level, x, y, z, vx, options);
        }
    }
}