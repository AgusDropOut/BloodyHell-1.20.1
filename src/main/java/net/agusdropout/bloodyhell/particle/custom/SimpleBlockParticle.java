package net.agusdropout.bloodyhell.particle.custom;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.agusdropout.bloodyhell.particle.ParticleOptions.SimpleBlockParticleOptions;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class SimpleBlockParticle extends Particle {
    private final BlockState state;
    private final BlockRenderDispatcher blockRenderer;
    private float scale;
    private final boolean hasIncrementalSize;

    private final float yaw;
    private final float pitch;
    private final float roll;

    public SimpleBlockParticle(ClientLevel level, double x, double y, double z,
                               double vx, double vy, double vz, BlockState state,
                               int initialSize, boolean hasIncrementalSize) {
        super(level, x, y, z, vx, vy, vz);
        this.state = state;
        this.blockRenderer = Minecraft.getInstance().getBlockRenderer();
        this.lifetime = 100;
        this.gravity = 0;
        this.friction = 1;
        this.hasPhysics = false;

        this.yaw = this.random.nextFloat() * 360f;
        this.pitch = this.random.nextFloat() * 360f;
        this.roll = this.random.nextFloat() * 360f;

        this.scale = initialSize;
        this.hasIncrementalSize = hasIncrementalSize;
    }
    @Override
    public void tick() {
        super.tick();

        this.yd = 0.03;

        this.xd /= 0.94;
        this.zd /= 0.94;

        if (this.hasIncrementalSize) {
            this.scale += 0.07f;
        }

        float lifeRatio = (float) this.age / (float) this.lifetime;
        this.alpha = 1.0f - lifeRatio;
    }

    @Override
    public void render(VertexConsumer ignored, Camera camera, float partialTicks) {
        PoseStack poseStack = new PoseStack();
        Vec3 camPos = camera.getPosition();

        double px = Mth.lerp(partialTicks, this.xo, this.x) - camPos.x;
        double py = Mth.lerp(partialTicks, this.yo, this.y) - camPos.y;
        double pz = Mth.lerp(partialTicks, this.zo, this.z) - camPos.z;

        poseStack.pushPose();
        poseStack.translate(px, py, pz);

        poseStack.scale(this.scale, this.scale, this.scale);

        poseStack.mulPose(Axis.YP.rotationDegrees(yaw));
        poseStack.mulPose(Axis.XP.rotationDegrees(pitch));
        poseStack.mulPose(Axis.ZP.rotationDegrees(roll));

        BakedModel model = blockRenderer.getBlockModel(state);
        MultiBufferSource.BufferSource bufferSource = Minecraft.getInstance().renderBuffers().bufferSource();
        blockRenderer.renderSingleBlock(state, poseStack, bufferSource, 15728880, 0);

        bufferSource.endBatch();
        poseStack.popPose();
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.CUSTOM;
    }
    public static class Provider implements ParticleProvider<SimpleBlockParticleOptions> {
        @Override
        public Particle createParticle(SimpleBlockParticleOptions options, ClientLevel level, double x, double y, double z,
                                       double vx, double vy, double vz) {
            return new SimpleBlockParticle(level, x, y, z, vx, vy, vz, options.getBlockState(), options.getInitialSize(), options.hasIncrementalSize());
        }
    }
}
