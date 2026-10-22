package net.agusdropout.bloodyhell.particle.custom;

import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionf;
import org.joml.Vector3f;

@OnlyIn(Dist.CLIENT)
public class BlasphemousMagicCircleParticle extends TextureSheetParticle {
    private final SpriteSet sprites;

    public BlasphemousMagicCircleParticle(ClientLevel clientLevel, double d, double e, double f,int maxLifeTime, SpriteSet sprites) {
        super(clientLevel, d, e, f);
        this.alpha = 0.8F;
        this.quadSize = 1.3F;
        this.lifetime = maxLifeTime;
        if (maxLifeTime == 0) {
            this.lifetime = 100;
        }
        this.sprites = sprites;
        this.rCol = 1.0F;
        this.gCol = 1.0F;
        this.bCol = 1.0F;
        this.setSpriteFromAge(sprites);
    }



    @Override
    public void tick() {
        if (age++ >= lifetime) {
            remove();
        }

        this.alpha = 0.75f + 0.25f * (float) Math.sin(age * 0.2f);

        setSpriteFromAge(sprites);
    }

    @Override
    public void render(VertexConsumer consumer, Camera camera, float delta) {

        float rotation = (age + delta) * 0.05f;
        renderParticle(consumer, camera, delta, Axis.YP.rotation(rotation));
    }

    private void renderParticle(VertexConsumer consumer, Camera camera, float delta, Quaternionf quaternion) {
        Vec3 camPos = camera.getPosition();


        Vector3f[] corners = new Vector3f[]{
                new Vector3f(-1, 0, -1),
                new Vector3f(-1, 0,  1),
                new Vector3f( 1, 0,  1),
                new Vector3f( 1, 0, -1)
        };

        for (int i = 0; i < 4; ++i) {
            Vector3f vec = corners[i];
            quaternion.transform(vec);
            vec.mul(getQuadSize(delta));
            float x = (float)(Mth.lerp(delta, xo, this.x) - camPos.x());
            float y = (float)(Mth.lerp(delta, yo, this.y) - camPos.y());
            float z = (float)(Mth.lerp(delta, zo, this.z) - camPos.z());
            vec.add(x, y, z);
        }

        float u0 = getU0();
        float u1 = getU1();
        float v0 = getV0();
        float v1 = getV1();
        int light = getLightColor(delta);

        consumer.vertex(corners[0].x(), corners[0].y(), corners[0].z()).uv(u1, v1).color(rCol, gCol, bCol, alpha).uv2(light).endVertex();
        consumer.vertex(corners[1].x(), corners[1].y(), corners[1].z()).uv(u1, v0).color(rCol, gCol, bCol, alpha).uv2(light).endVertex();
        consumer.vertex(corners[2].x(), corners[2].y(), corners[2].z()).uv(u0, v0).color(rCol, gCol, bCol, alpha).uv2(light).endVertex();
        consumer.vertex(corners[3].x(), corners[3].y(), corners[3].z()).uv(u0, v1).color(rCol, gCol, bCol, alpha).uv2(light).endVertex();
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    @Override
    protected int getLightColor(float tint) {
        int baseLight = super.getLightColor(tint);
        int fullBright = 0xF000F0;
        return Math.max(baseLight, fullBright);
    }

    @OnlyIn(Dist.CLIENT)
    public static class Provider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprites;

        public Provider(SpriteSet sprites) {
            this.sprites = sprites;
        }

        @Nullable
        @Override
        public Particle createParticle(SimpleParticleType particleOptions, @NotNull ClientLevel clientLevel, double d, double e, double f, double g, double h, double i) {
            return new BlasphemousMagicCircleParticle(clientLevel, d, e, f,(int) g, this.sprites);
        }
    }

}