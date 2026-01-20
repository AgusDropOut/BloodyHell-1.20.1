package net.agusdropout.bloodyhell.particle.custom;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.agusdropout.bloodyhell.particle.ParticleOptions.ImpactParticleOptions;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.awt.Color;

public class ImpactParticle extends TextureSheetParticle {
    private final SpriteSet sprites;

    private final float expansionSpeed;
    private final boolean jitter;
    private final float targetSize;

    // Jitter Bases
    private final float baseHue;
    private final float baseSat;
    private final float baseBri;

    public ImpactParticle(ClientLevel level, double x, double y, double z, ImpactParticleOptions options, SpriteSet sprites) {
        super(level, x, y, z);
        this.sprites = sprites;

        // 1. Apply Options (Individual RGB)
        this.rCol = options.getR();
        this.gCol = options.getG();
        this.bCol = options.getB();

        this.targetSize = options.getSize();
        this.lifetime = options.getLifetime();
        this.jitter = options.shouldJitter();
        this.expansionSpeed = options.getExpansionSpeed();

        // 2. Physics
        this.gravity = 0.0F;
        this.quadSize = 0.25F;

        // 3. Jitter Calculation
        if (this.jitter) {
            float[] hsb = Color.RGBtoHSB((int)(rCol*255), (int)(gCol*255), (int)(bCol*255), null);
            this.baseHue = hsb[0];
            this.baseSat = hsb[1];
            this.baseBri = hsb[2];
        } else {
            this.baseHue = 0; this.baseSat = 0; this.baseBri = 0;
        }

        this.setSpriteFromAge(sprites);
    }

    @Override
    public void tick() {
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;

        // Expansion
        this.quadSize = Mth.lerp(this.expansionSpeed, this.quadSize, this.targetSize);

        if (this.age++ >= this.lifetime) {
            this.remove();
        } else {
            // Fade
            float lifeRatio = (float)this.age / (float)this.lifetime;
            if (lifeRatio > 0.7f) {
                this.alpha = 1.0f - ((lifeRatio - 0.7f) * 3.3f);
            }
        }

        // Jitter
        if (this.jitter) {
            float hueShift = Mth.sin(this.age * 0.2f) * 0.05f;
            int rgb = Color.HSBtoRGB(this.baseHue + hueShift, this.baseSat, this.baseBri);
            this.rCol = ((rgb >> 16) & 0xFF) / 255.0F;
            this.gCol = ((rgb >> 8) & 0xFF) / 255.0F;
            this.bCol = (rgb & 0xFF) / 255.0F;
        }

        if (this.alpha <= 0.01F) this.remove();
        this.setSpriteFromAge(this.sprites);
    }

    @Override
    public void render(VertexConsumer consumer, Camera camera, float partialTick) {
        Vec3 camPos = camera.getPosition();
        float x = (float) (Mth.lerp(partialTick, this.xo, this.x) - camPos.x());
        float y = (float) (Mth.lerp(partialTick, this.yo, this.y) - camPos.y());
        float z = (float) (Mth.lerp(partialTick, this.zo, this.z) - camPos.z());

        float rotation = (this.age + partialTick) * 0.1f;
        Quaternionf quaternion = new Quaternionf().rotateY(rotation);

        Vector3f[] vertices = new Vector3f[]{
                new Vector3f(-1.0F, 0.0F, -1.0F),
                new Vector3f(-1.0F, 0.0F, 1.0F),
                new Vector3f(1.0F, 0.0F, 1.0F),
                new Vector3f(1.0F, 0.0F, -1.0F)
        };

        float scale = this.getQuadSize(partialTick);

        for(int i = 0; i < 4; ++i) {
            Vector3f vertex = vertices[i];
            vertex.rotate(quaternion);
            vertex.mul(scale);
            vertex.add(x, y, z);
        }

        float u0 = this.getU0(); float u1 = this.getU1();
        float v0 = this.getV0(); float v1 = this.getV1();
        int light = this.getLightColor(partialTick);

        consumer.vertex(vertices[0].x(), vertices[0].y(), vertices[0].z()).uv(u1, v1).color(this.rCol, this.gCol, this.bCol, this.alpha).uv2(light).endVertex();
        consumer.vertex(vertices[1].x(), vertices[1].y(), vertices[1].z()).uv(u1, v0).color(this.rCol, this.gCol, this.bCol, this.alpha).uv2(light).endVertex();
        consumer.vertex(vertices[2].x(), vertices[2].y(), vertices[2].z()).uv(u0, v0).color(this.rCol, this.gCol, this.bCol, this.alpha).uv2(light).endVertex();
        consumer.vertex(vertices[3].x(), vertices[3].y(), vertices[3].z()).uv(u0, v1).color(this.rCol, this.gCol, this.bCol, this.alpha).uv2(light).endVertex();
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    @Override
    protected int getLightColor(float partialTick) {
        return 240;
    }

    public static class Provider implements ParticleProvider<ImpactParticleOptions> {
        private final SpriteSet sprites;

        public Provider(SpriteSet sprites) {
            this.sprites = sprites;
        }

        @Nullable
        @Override
        public Particle createParticle(ImpactParticleOptions options, @NotNull ClientLevel level, double x, double y, double z, double dx, double dy, double dz) {
            return new ImpactParticle(level, x, y, z, options, this.sprites);
        }
    }
}