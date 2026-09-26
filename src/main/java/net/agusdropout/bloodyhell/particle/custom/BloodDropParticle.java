package net.agusdropout.bloodyhell.particle.custom;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.agusdropout.bloodyhell.particle.ModParticles;
import net.agusdropout.bloodyhell.particle.ParticleOptions.BloodDropParticleOption;
import net.agusdropout.bloodyhell.util.visuals.manager.BloodDropRenderManager;
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

@OnlyIn(Dist.CLIENT)
public class BloodDropParticle extends Particle {

    private final float baseScale;

    protected BloodDropParticle(ClientLevel level, double x, double y, double z, Vector3f color) {
        super(level, x, y, z);
        this.gravity = 1.0F;
        this.friction = 0.98F;
        this.xd *= 0.1;
        this.yd *= 0.1;
        this.zd *= 0.1;
        this.baseScale = 0.25F;
        this.lifetime = 100;
        this.hasPhysics = true;

        this.rCol = color.x();
        this.gCol = color.y();
        this.bCol = color.z();
        this.alpha = 1.0f;
    }

    @Override
    public void tick() {
        super.tick();

        if (this.onGround) {
            this.remove();
            this.level.addParticle(ModParticles.BLOOD_STAIN_PARTICLE.get(), this.x, this.y + 0.01, this.z, 0, 0, 0);
        }
    }

    @Override
    public void render(VertexConsumer buffer, Camera camera, float partialTicks) {
        Vec3 cameraPos = camera.getPosition();
        float renderX = (float) (Mth.lerp(partialTicks, this.xo, this.x) - cameraPos.x());
        float renderY = (float) (Mth.lerp(partialTicks, this.yo, this.y) - cameraPos.y());
        float renderZ = (float) (Mth.lerp(partialTicks, this.zo, this.z) - cameraPos.z());

        PoseStack poseStack = new PoseStack();
        poseStack.translate(renderX, renderY, renderZ);
        poseStack.mulPose(camera.rotation());

        float stretch = (float) Math.max(1.0, Math.abs(this.yd) * 5.0);
        poseStack.scale(1.0f, stretch, 1.0f);

        float time = (this.age + partialTicks) * 0.05f;

        BloodDropRenderManager.INSTANCE.addParticle(new BloodDropRenderManager.BloodData(
                poseStack.last().pose(),
                this.baseScale,
                this.rCol, this.gCol, this.bCol,
                this.alpha,
                time
        ));
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.CUSTOM;
    }

    public static class Provider implements ParticleProvider<BloodDropParticleOption> {
        public Provider() {}

        @Override
        public Particle createParticle(BloodDropParticleOption option, ClientLevel level, double x, double y, double z, double dx, double dy, double dz) {
            return new BloodDropParticle(level, x, y, z, option.getColor());
        }
    }
}