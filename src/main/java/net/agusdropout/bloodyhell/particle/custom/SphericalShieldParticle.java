package net.agusdropout.bloodyhell.particle.custom;

import com.mojang.blaze3d.shaders.Uniform;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.agusdropout.bloodyhell.particle.ParticleOptions.SphericalShieldParticleOptions;
import net.agusdropout.bloodyhell.util.visuals.ModShaders;
import net.agusdropout.bloodyhell.util.visuals.RenderHelper;
import net.agusdropout.bloodyhell.util.visuals.manager.SphericalShieldRenderManager;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;

public class SphericalShieldParticle extends Particle {

    private final float shieldRadius;

    protected SphericalShieldParticle(ClientLevel level, double x, double y, double z, float r, float g, float b, float radius, int lifetime) {
        super(level, x, y, z);
        this.rCol = r;
        this.gCol = g;
        this.bCol = b;
        this.shieldRadius = radius;
        this.lifetime = lifetime;
        this.hasPhysics = false;
        this.setSize(radius * 4.0F, radius * 4.0F);
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
    public void render(VertexConsumer buffer, Camera camera, float partialTicks) {
        Vec3 cameraPos = camera.getPosition();
        float renderX = (float) (Mth.lerp(partialTicks, this.xo, this.x) - cameraPos.x());
        float renderY = (float) (Mth.lerp(partialTicks, this.yo, this.y) - cameraPos.y());
        float renderZ = (float) (Mth.lerp(partialTicks, this.zo, this.z) - cameraPos.z());

        PoseStack poseStack = new PoseStack();
        poseStack.translate(renderX, renderY, renderZ);


        Matrix4f capturedPose = new Matrix4f(poseStack.last().pose());
        float currentAlpha = this.alpha;

        SphericalShieldRenderManager.queueRender(() -> {
            Tesselator tesselator = Tesselator.getInstance();
            BufferBuilder builder = tesselator.getBuilder();

            // 1. Primary Fire Sphere
            RenderSystem.setShader(() -> ModShaders.SHAPE_UNKNOWN_FIRE_SHADER);
            if (ModShaders.SHAPE_UNKNOWN_FIRE_SHADER != null) {
                Uniform timeUniform = ModShaders.SHAPE_UNKNOWN_FIRE_SHADER.getUniform("AnimTime");
                if (timeUniform != null) {
                    timeUniform.set((System.currentTimeMillis() % 100000L) / 1000.0F);
                }
            }
            builder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
            RenderHelper.renderColorSphere(builder, capturedPose, this.shieldRadius, 32, 32, this.rCol, this.gCol, this.bCol, currentAlpha - 0.3f);
            tesselator.end();

            // 2. Glitter Sphere
            RenderSystem.setShader(() -> ModShaders.SHAPE_GLITTER_SHADER);
            if (ModShaders.SHAPE_GLITTER_SHADER != null) {
                Uniform timeUniform = ModShaders.SHAPE_GLITTER_SHADER.getUniform("GlitterTime");
                if (timeUniform != null) {
                    timeUniform.set(this.age + partialTicks);
                }
            }
            builder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
            RenderHelper.renderColorSphere(builder, capturedPose, this.shieldRadius * 0.99f, 32, 32, this.rCol , this.gCol , this.bCol , 0.2f);
            tesselator.end();

            // 3. Rim Sphere
            RenderSystem.setShader(() -> ModShaders.SHAPE_SPHERICAL_RIM_SHADER);
            builder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
            RenderHelper.renderColorSphere(builder, capturedPose, this.shieldRadius * 1.01f, 32, 32, this.rCol * 0.05f, this.gCol * 0.05f, this.bCol * 0.05f, 1f);
            tesselator.end();
        });
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.CUSTOM;
    }

    public static class Provider implements ParticleProvider<SphericalShieldParticleOptions> {
        @Override
        public Particle createParticle(SphericalShieldParticleOptions options, ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            return new SphericalShieldParticle(level, x, y, z, options.getR(), options.getG(), options.getB(), options.getRadius(), options.getLifetime());
        }
    }
}