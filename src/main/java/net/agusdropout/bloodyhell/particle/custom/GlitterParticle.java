package net.agusdropout.bloodyhell.particle.custom;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.agusdropout.bloodyhell.particle.ParticleOptions.GlitterParticleOptions;
import net.agusdropout.bloodyhell.particle.ParticleOptions.MagicParticleOptions;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.Nullable;

import java.awt.*;

public class GlitterParticle extends TextureSheetParticle {

    private final SpriteSet spriteSet;
    private final boolean jitter;
    private final boolean hasWhiteCore;

    // HSB Colors
    private final float baseHue;
    private final float baseSat;
    private final float baseBri;

    private final float initialQuadSize;

    protected GlitterParticle(ClientLevel level, double x, double y, double z, double vx, double vy, double vz, GlitterParticleOptions options, SpriteSet spriteSet) {
        super(level, x, y, z, vx, vy, vz);

        this.spriteSet = spriteSet;
        this.jitter = options.shouldJitter();
        this.lifetime = options.getLifetime();
        this.hasWhiteCore = options.hasWhiteCore();
        // Convert RGB to HSB
        float[] hsb = Color.RGBtoHSB(
                (int)(options.getColor().x() * 255),
                (int)(options.getColor().y() * 255),
                (int)(options.getColor().z() * 255),
                null
        );
        this.baseHue = hsb[0];
        this.baseSat = hsb[1];
        this.baseBri = hsb[2];

        this.rCol = options.getColor().x();
        this.gCol = options.getColor().y();
        this.bCol = options.getColor().z();

        // Tiny Start Size (Sharper look for magic)
        this.quadSize = options.getSize() * 0.5F;
        this.initialQuadSize = this.quadSize;

        this.lifetime = 20 + this.random.nextInt(10);
        this.gravity = 0.0F;
        this.hasPhysics = false;
        this.xd = vx;
        this.yd = vy;
        this.zd = vz;

        this.setSpriteFromAge(spriteSet);
    }

    @Override
    public void tick() {
        super.tick();
        this.setSpriteFromAge(this.spriteSet);


        float lifeRatio = (float)this.age / (float)this.lifetime;
        this.quadSize = this.initialQuadSize * (1.0F - lifeRatio);


        if (this.jitter) {
            float hueShift = Mth.sin(this.age * 0.4F) * 0.15F;
            float newHue = this.baseHue + hueShift;

            int rgb = Color.HSBtoRGB(newHue, this.baseSat, this.baseBri);
            this.rCol = ((rgb >> 16) & 0xFF) / 255.0F;
            this.gCol = ((rgb >> 8) & 0xFF) / 255.0F;
            this.bCol = (rgb & 0xFF) / 255.0F;
        }


        if (lifeRatio > 0.7F) {
            this.alpha = 0.6F - ((lifeRatio - 0.7F) * 2.0F);
        } else {
            this.alpha = 0.6F;
        }
    }


    @Override
    public ParticleRenderType getRenderType() {

        float brightness = (this.rCol * 0.299f) + (this.gCol * 0.587f) + (this.bCol * 0.114f);

        if (brightness < 0.3f) {
            return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
        } else {
            return PARTICLE_SHEET_ADDITIVE;
        }
    }

    @Override
    public void render(VertexConsumer buffer, Camera camera, float partialTicks) {

        float originalR = this.rCol;
        float originalG = this.gCol;
        float originalB = this.bCol;
        float originalSize = this.quadSize;

        super.render(buffer, camera, partialTicks);

        if(this.hasWhiteCore ) {
            this.rCol = 1.0F;
            this.gCol = 1.0F;
            this.bCol = 1.0F;
            this.quadSize = originalSize * 0.4F;
            super.render(buffer, camera, partialTicks);
        }


        this.rCol = originalR;
        this.gCol = originalG;
        this.bCol = originalB;
        this.quadSize = originalSize;
    }

    // Force full brightness (Emissive)
    @Override
    protected int getLightColor(float partialTick) {
        return 240;
    }


    public static final ParticleRenderType PARTICLE_SHEET_ADDITIVE = new ParticleRenderType() {
        @Override
        public void begin(BufferBuilder buffer, TextureManager textureManager) {
            RenderSystem.depthMask(false);
            RenderSystem.setShaderTexture(0, TextureAtlas.LOCATION_PARTICLES);

            RenderSystem.enableBlend();

            RenderSystem.blendFunc(
                    com.mojang.blaze3d.platform.GlStateManager.SourceFactor.SRC_ALPHA,
                    com.mojang.blaze3d.platform.GlStateManager.DestFactor.ONE
            );

            RenderSystem.setShader(GameRenderer::getParticleShader);
            buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.PARTICLE);
        }

        @Override
        public void end(Tesselator tesselator) {
            tesselator.end();
            RenderSystem.blendFunc(
                    com.mojang.blaze3d.platform.GlStateManager.SourceFactor.SRC_ALPHA,
                    com.mojang.blaze3d.platform.GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA
            );
        }

        @Override
        public String toString() {
            return "PARTICLE_SHEET_ADDITIVE";
        }
    };

    public static class Provider implements ParticleProvider<GlitterParticleOptions> {
        private final SpriteSet spriteSet;
        public Provider(SpriteSet spriteSet) { this.spriteSet = spriteSet; }

        @Nullable
        @Override
        public Particle createParticle(GlitterParticleOptions type, ClientLevel level, double x, double y, double z, double dx, double dy, double dz) {
            return new GlitterParticle(level, x, y, z, dx, dy, dz, type, this.spriteSet);
        }
    }
}