package net.agusdropout.bloodyhell.particle.custom;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.agusdropout.bloodyhell.particle.ParticleOptions.MagicParticleOptions;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.Nullable;

import java.awt.Color;

public class MagicParticle extends TextureSheetParticle {

    private final SpriteSet spriteSet;
    private final boolean jitter;

    // HSB Colors
    private final float baseHue;
    private final float baseSat;
    private final float baseBri;

    private final float initialQuadSize;

    protected MagicParticle(ClientLevel level, double x, double y, double z, double vx, double vy, double vz, MagicParticleOptions options, SpriteSet spriteSet) {
        super(level, x, y, z, vx, vy, vz);

        this.spriteSet = spriteSet;
        this.jitter = options.shouldJitter();
        this.lifetime = options.getLifetime();
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

        // Shrink with age
        float lifeRatio = (float)this.age / (float)this.lifetime;
        this.quadSize = this.initialQuadSize * (1.0F - lifeRatio);

        // Living Color Jitter
        if (this.jitter) {
            float hueShift = Mth.sin(this.age * 0.4F) * 0.15F;
            float newHue = this.baseHue + hueShift;

            int rgb = Color.HSBtoRGB(newHue, this.baseSat, this.baseBri);
            this.rCol = ((rgb >> 16) & 0xFF) / 255.0F;
            this.gCol = ((rgb >> 8) & 0xFF) / 255.0F;
            this.bCol = (rgb & 0xFF) / 255.0F;
        }

        // --- GLOW ALPHA TWEAK ---
        // For Additive Blending, High Alpha = White Center. Low Alpha = Colored Tint.
        // We keep it around 0.6 to 0.8 to ensure the color is visible.
        // If it's too high (1.0), it will just look white.
        if (lifeRatio > 0.7F) {
            this.alpha = 0.6F - ((lifeRatio - 0.7F) * 2.0F); // Quick fade out
        } else {
            this.alpha = 0.6F; // Steady glow
        }
    }

    // --- RENDER TYPE MAGIC ---
    // We override this to use our custom Additive Render Type
    @Override
    public ParticleRenderType getRenderType() {
        return PARTICLE_SHEET_ADDITIVE;
    }

    // Force full brightness (Emissive)
    @Override
    protected int getLightColor(float partialTick) {
        return 240;
    }

    // --- THE CUSTOM RENDER TYPE ---
    // This defines HOW the particle is drawn onto the screen.
    public static final ParticleRenderType PARTICLE_SHEET_ADDITIVE = new ParticleRenderType() {
        @Override
        public void begin(BufferBuilder buffer, TextureManager textureManager) {
            RenderSystem.depthMask(false); // Do not write to depth buffer (Ghostly)
            RenderSystem.setShaderTexture(0, TextureAtlas.LOCATION_PARTICLES);

            RenderSystem.enableBlend();
            // THE SECRET SAUCE: ADDITIVE BLENDING
            // SRC_ALPHA + ONE means: Take the particle color and ADD it to the background.
            // This creates the "Light" effect.
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
            // Important: Reset blending to Normal so we don't break other particles
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

    public static class Provider implements ParticleProvider<MagicParticleOptions> {
        private final SpriteSet spriteSet;
        public Provider(SpriteSet spriteSet) { this.spriteSet = spriteSet; }

        @Nullable
        @Override
        public Particle createParticle(MagicParticleOptions type, ClientLevel level, double x, double y, double z, double dx, double dy, double dz) {
            return new MagicParticle(level, x, y, z, dx, dy, dz, type, this.spriteSet);
        }
    }
}