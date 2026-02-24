package net.agusdropout.bloodyhell.particle.custom;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.agusdropout.bloodyhell.particle.ParticleOptions.EtherealSwirlOptions;
import net.agusdropout.bloodyhell.util.visuals.ShaderUtils;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.lwjgl.opengl.GL11;

import java.util.UUID;

public class EtherealSwirlParticle extends Particle {

    private final float quadSize;
    private final float rCol;
    private final float gCol;
    private final float bCol;
    private final int targetEntityId; // Changed to int

    private static int captureTextureId = -1;

    protected EtherealSwirlParticle(ClientLevel level, double x, double y, double z, float r, float g, float b, int maxLifetime, float size, int targetEntityId) {
        super(level, x, y, z);
        this.hasPhysics = false;
        this.xd = 0;
        this.yd = 0;
        this.zd = 0;
        this.quadSize = size;
        this.targetEntityId = targetEntityId;

        if (captureTextureId == -1) {
            captureTextureId = GL11.glGenTextures();
        }

        this.lifetime = maxLifetime;
        this.alpha = 1.0f;

        this.rCol = r;
        this.gCol = g;
        this.bCol = b;
    }

    @Override
    public void tick() {
        super.tick();

        if(this.targetEntityId != -1) {
            Entity target = this.level.getEntity(this.targetEntityId);


            if (target != null) {
                this.x = target.getX();
                this.y = target.getY() ;
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

        RenderSystem.depthMask(false);
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(
                GlStateManager.SourceFactor.SRC_ALPHA,
                com.mojang.blaze3d.platform.GlStateManager.DestFactor.ONE
        );


        Vec3 cameraPos = camera.getPosition();
        float x = (float) (Mth.lerp(partialTicks, this.xo, this.x) - cameraPos.x());
        float y = (float) (Mth.lerp(partialTicks, this.yo, this.y) - cameraPos.y());
        float z = (float) (Mth.lerp(partialTicks, this.zo, this.z) - cameraPos.z());


        PoseStack poseStack = new PoseStack();
        poseStack.translate(x, y, z);
        poseStack.mulPose(new Quaternionf(camera.rotation()));

        Matrix4f pose = poseStack.last().pose();


        float time = (float) this.level.getGameTime() + partialTicks;


        ShaderUtils.renderEtherealSwirlQuad(poseStack,captureTextureId, pose, this.quadSize, this.rCol, this.gCol, this.bCol, this.alpha, time);


        RenderSystem.blendFunc(
                com.mojang.blaze3d.platform.GlStateManager.SourceFactor.SRC_ALPHA,
                com.mojang.blaze3d.platform.GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA
        );
        RenderSystem.depthMask(true);
    }

    public static class Provider implements ParticleProvider<EtherealSwirlOptions> {
        public Provider() {}

        @Nullable
        @Override
        public Particle createParticle(EtherealSwirlOptions type, ClientLevel level, double x, double y, double z, double dx, double dy, double dz) {
            // Pass the ID from the options to the particle
            return new EtherealSwirlParticle(level, x, y, z, type.getR(), type.getG(), type.getB(), type.getMaxLifetime(), type.getSize(), type.getTargetId());
        }
    }
}