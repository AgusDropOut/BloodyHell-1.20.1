package net.agusdropout.bloodyhell.particle.custom;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Axis; // Import Axis for rotation
import net.agusdropout.bloodyhell.particle.ParticleOptions.HollowRectangleOptions;
import net.agusdropout.bloodyhell.util.visuals.RenderHelper;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;

public class HollowRectangleParticle extends TextureSheetParticle {

    private final float width; // Changed to float for smoother rotation precision
    private final float height;
    private final float yaw;
    private final float jitterIntensity;
    private float rCol, gCol, bCol;

    protected HollowRectangleParticle(ClientLevel level, double x, double y, double z, HollowRectangleOptions options) {
        super(level, x, y, z);

        // --- Color Jittering Logic ---
         jitterIntensity = options.getJitter();


        this.rCol = options.getColor().x();
        this.gCol = options.getColor().y();
        this.bCol = options.getColor().z();


        this.lifetime = options.getLife();
        this.width = options.getWidth();
        this.height = options.getHeight();
        this.yaw = options.getYaw();

        this.gravity = 0;
        this.hasPhysics = false;
    }

    @Override
    public void render(VertexConsumer vertexConsumer, Camera camera, float partialTicks) {
        Vec3 camPos = camera.getPosition();
        float px = (float) (Mth.lerp(partialTicks, xo, x) - camPos.x);
        float py = (float) (Mth.lerp(partialTicks, yo, y) - camPos.y);
        float pz = (float) (Mth.lerp(partialTicks, zo, z) - camPos.z);

        Tesselator tess = Tesselator.getInstance();
        BufferBuilder buffer = tess.getBuilder();
        RenderSystem.disableCull();
        RenderSystem.enableDepthTest();
        RenderSystem.enableBlend();
        RenderSystem.depthMask(false);
        RenderSystem.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        RenderSystem.setShader(GameRenderer::getPositionColorShader);

        PoseStack poseStack = new PoseStack();
        poseStack.translate(px, py, pz);


        if (this.yaw != 0) {
            poseStack.mulPose(Axis.YP.rotationDegrees(this.yaw));
        }

        float time = age + partialTicks;



        Matrix4f pose = poseStack.last().pose();

        buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);

        float w = this.width;
        float h = this.height;
        float yOffset = (this.height / 7.0f) + (this.width / 7.0f);
        float upAlpha = (float) (Math.sin(time * 0.08) * 0.2f + 0.3f);

        RenderHelper.renderHollowRectangle(
                buffer,
                pose,
                w,
                h,
                yOffset,
                rCol,
                gCol,
                bCol,
                alpha,
                upAlpha,
                jitterIntensity
        );




        tess.end();
        RenderSystem.depthMask(true);
        RenderSystem.disableBlend();
        RenderSystem.enableCull();
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.CUSTOM;
    }


    @Override
    public AABB getBoundingBox() {
        return super.getBoundingBox().inflate(20.0);
    }

    public static class Provider implements ParticleProvider<HollowRectangleOptions> {
        @Override
        public Particle createParticle(HollowRectangleOptions opts, ClientLevel level, double x, double y, double z, double dx, double dy, double dz) {
            return new HollowRectangleParticle(level, x, y, z, opts);
        }
    }
}