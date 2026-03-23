package net.agusdropout.bloodyhell.particle.custom;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.agusdropout.bloodyhell.particle.ParticleOptions.NoiseSphereParticleOptions;
import net.agusdropout.bloodyhell.util.visuals.manager.NoiseSphereRenderManager;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

public class NoiseSphereParticle extends Particle {

    private static final float GROW_FACTOR = 3.0f;

    private final Vector3f color;
    private final float maxRadius;

    protected NoiseSphereParticle(ClientLevel level, double x, double y, double z, Vector3f color, float initialSize, int lifeTicks) {
        super(level, x, y, z);
        this.color = color;
        this.maxRadius = initialSize;
        this.lifetime = lifeTicks;
        this.xo = x;
        this.yo = y;
        this.zo = z;
        this.gravity = 0.0F;
        this.hasPhysics = false;
        this.xd = 0;
        this.yd = 0;
        this.zd = 0;
    }

    @Override
    public void tick() {
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;
        if (this.age++ >= this.lifetime) {
            this.remove();
        }
    }

    @Override
    public void render(VertexConsumer ignored, Camera camera, float partialTick) {
        float time = this.age + partialTick;
        float lifeRatio = time / (float) this.lifetime;

        float expansionFactor = 1.0f - (float) Math.pow(1.0f - lifeRatio, GROW_FACTOR);
        float currentRadius = this.maxRadius * expansionFactor;

        float alpha = Mth.clamp(1.0f - lifeRatio, 0.0f, 1.0f);
        float uOffset = time * 0.008F;
        float vOffset = time * 0.007F;

        Vec3 camPos = camera.getPosition();
        float px = (float) (Mth.lerp(partialTick, this.xo, this.x) - camPos.x());
        float py = (float) (Mth.lerp(partialTick, this.yo, this.y) - camPos.y());
        float pz = (float) (Mth.lerp(partialTick, this.zo, this.z) - camPos.z());

        NoiseSphereRenderManager.addSphere(px, py, pz, currentRadius, this.color, alpha, uOffset, vOffset);
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.CUSTOM;
    }

    public static class Provider implements ParticleProvider<NoiseSphereParticleOptions> {
        @Override
        public Particle createParticle(NoiseSphereParticleOptions options, ClientLevel level, double x, double y, double z, double dx, double dy, double dz) {
            return new NoiseSphereParticle(level, x, y, z, options.getColor(), options.getInitialSize(), options.getLifeTicks());
        }
    }
}