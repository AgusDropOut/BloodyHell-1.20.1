package net.agusdropout.bloodyhell.particle.custom;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.agusdropout.bloodyhell.particle.ParticleOptions.EtherealSwirlOptions;
import net.agusdropout.bloodyhell.client.render.effect.Effects;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.Nullable;

public class EtherealSwirlParticle extends Particle {

    public final float quadSize;
    public final float rCol;
    public final float gCol;
    public final float bCol;
    private final int targetEntityId;

    protected EtherealSwirlParticle(ClientLevel level, double x, double y, double z, float r, float g, float b, int maxLifetime, float size, int targetEntityId) {
        super(level, x, y, z);
        this.hasPhysics = false;
        this.xd = 0;
        this.yd = 0;
        this.zd = 0;
        this.quadSize = size;
        this.targetEntityId = targetEntityId;
        this.lifetime = maxLifetime;
        this.alpha = 1.0f;
        this.rCol = r;
        this.gCol = g;
        this.bCol = b;
    }

    @Override
    public void tick() {
        super.tick();

        if (this.targetEntityId != -1) {
            Entity target = this.level.getEntity(this.targetEntityId);
            if (target != null) {
                this.x = target.getX();
                this.y = target.getY();
                this.z = target.getZ();
            }
        }

        float lifeRatio = (float) this.age / (float) this.lifetime;
        if (lifeRatio > 0.7F) {
            this.alpha = 1.0F - ((lifeRatio - 0.7F) / 0.3F);
        }
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.CUSTOM;
    }

    @Override
    public void render(VertexConsumer buffer, Camera camera, float partialTicks) {

        net.minecraft.world.phys.Vec3 cameraPos = camera.getPosition();
        float renderX = (float) (Mth.lerp(partialTicks, this.xo, this.x) - cameraPos.x());
        float renderY = (float) (Mth.lerp(partialTicks, this.yo, this.y) - cameraPos.y());
        float renderZ = (float) (Mth.lerp(partialTicks, this.zo, this.z) - cameraPos.z());

        PoseStack poseStack = new PoseStack();
        poseStack.translate(renderX, renderY, renderZ);

        poseStack.mulPose(camera.rotation());

        float time = (float) this.level.getGameTime() + partialTicks;

        Effects.SWIRL.submit(poseStack.last().pose(), this.quadSize, this.rCol, this.gCol, this.bCol, this.alpha, time);
    }

    public static class Provider implements ParticleProvider<EtherealSwirlOptions> {
        public Provider() {}

        @Nullable
        @Override
        public Particle createParticle(EtherealSwirlOptions type, ClientLevel level, double x, double y, double z, double dx, double dy, double dz) {
            return new EtherealSwirlParticle(level, x, y, z, type.getR(), type.getG(), type.getB(), type.getMaxLifetime(), type.getSize(), type.getTargetId());
        }
    }
}
