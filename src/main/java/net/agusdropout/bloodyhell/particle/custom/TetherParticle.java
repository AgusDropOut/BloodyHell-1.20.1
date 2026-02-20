package net.agusdropout.bloodyhell.particle.custom;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.agusdropout.bloodyhell.particle.ParticleOptions.TetherParticleOptions;
import net.agusdropout.bloodyhell.util.visuals.RenderHelper;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.lwjgl.opengl.GL11;
import java.util.UUID;

public class TetherParticle extends Particle {
    private final UUID targetUUID;
    private final float alphaFinal;
    private Entity cachedTarget;

    protected TetherParticle(ClientLevel level, double x, double y, double z, TetherParticleOptions options) {
        super(level, x, y, z);
        this.targetUUID = options.targetUUID();
        this.rCol = options.r();
        this.gCol = options.g();
        this.bCol = options.b();
        this.alphaFinal = options.a();
        this.alpha = options.a();
        this.lifetime = options.lifetime();
        this.hasPhysics = false;
    }

    @Override
    public void tick() {
        super.tick();
        if (cachedTarget == null || !cachedTarget.isAlive()) {
            for (Entity e : level.entitiesForRendering()) {
                if (e.getUUID().equals(targetUUID)) {
                    cachedTarget = e;
                    break;
                }
            }
        }
        // Simple fade-out over time
        this.alpha = this.alphaFinal * (1.0f - ((float) this.age / this.lifetime));
    }

    @Override
    public void render(VertexConsumer vertexConsumer, Camera camera, float partialTicks) {
        Vec3 cam = camera.getPosition();
        // Start is the fixed origin point in the world (The Throne)
        Vec3 start = new Vec3(this.x - cam.x, this.y - cam.y, this.z - cam.z);
        Vec3 end;

        if (cachedTarget != null) {
            // Smoothly interpolate the entity position
            double tx = Mth.lerp(partialTicks, cachedTarget.xo, cachedTarget.getX());
            double ty = Mth.lerp(partialTicks, cachedTarget.yo, cachedTarget.getY()) + (cachedTarget.getBbHeight() / 1.5);
            double tz = Mth.lerp(partialTicks, cachedTarget.zo, cachedTarget.getZ());
            end = new Vec3(tx - cam.x, ty - cam.y, tz - cam.z);
        } else {
            end = start.add(0, 1, 0);
        }

        Tesselator tess = Tesselator.getInstance();
        BufferBuilder buffer = tess.getBuilder();

        RenderSystem.disableCull();
        RenderSystem.enableDepthTest();
        RenderSystem.enableBlend();
        RenderSystem.depthMask(false);
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        RenderSystem.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);

        RenderHelper.renderBeam(new PoseStack(), buffer, start, end, 0.08f, rCol, gCol, bCol, alpha, getLightColor(partialTicks));
        tess.end();

        RenderSystem.depthMask(true);
        RenderSystem.enableCull();
    }

    @Override
    public ParticleRenderType getRenderType() { return ParticleRenderType.CUSTOM; }

    public static class Provider implements ParticleProvider<TetherParticleOptions> {
        public Provider(SpriteSet spriteSet) {}
        @Override
        public Particle createParticle(TetherParticleOptions type, ClientLevel level, double x, double y, double z, double dx, double dy, double dz) {
            return new TetherParticle(level, x, y, z, type);
        }
    }
}