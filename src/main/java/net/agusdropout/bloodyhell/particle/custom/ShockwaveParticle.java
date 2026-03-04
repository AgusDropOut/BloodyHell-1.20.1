package net.agusdropout.bloodyhell.particle.custom;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.agusdropout.bloodyhell.particle.ParticleOptions.ShockwaveParticleOptions;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class ShockwaveParticle extends TextureSheetParticle {

    private final float growthPerTick;

    protected ShockwaveParticle(ClientLevel level, double x, double y, double z, double xDir, double yDir, double zDir, SpriteSet spriteSet, ShockwaveParticleOptions options) {
        super(level, x, y, z, 0, 0, 0);

        this.xd = xDir;
        this.yd = yDir;
        this.zd = zDir;

        this.rCol = options.getColor().x();
        this.gCol = options.getColor().y();
        this.bCol = options.getColor().z();

        this.quadSize = options.getInitialSize();
        float sizeDifference = Math.max(0.1f, options.getMaxSize() - options.getInitialSize());

        // 10 ticks of lifetime per 1.0f unit of size growth
        this.lifetime = (int) Math.max(1, sizeDifference * 10.0f);
        this.growthPerTick = sizeDifference / this.lifetime;

        this.alpha = 1.0f;

        this.pickSprite(spriteSet);
        this.hasPhysics = false;
    }

    @Override
    public void tick() {
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;

        if (this.age++ >= this.lifetime) {
            this.remove();
        } else {
            this.quadSize += this.growthPerTick;
            this.alpha = 1.0f - ((float)this.age / (float)this.lifetime);
        }
    }

    @Override
    public void render(VertexConsumer buffer, Camera camera, float partialTicks) {
        Vec3 camPos = camera.getPosition();
        float x = (float) (Mth.lerp(partialTicks, this.xo, this.x) - camPos.x());
        float y = (float) (Mth.lerp(partialTicks, this.yo, this.y) - camPos.y());
        float z = (float) (Mth.lerp(partialTicks, this.zo, this.z) - camPos.z());

        Quaternionf baseRotation = new Quaternionf();

        double horizontalDist = Math.sqrt(this.xd * this.xd + this.zd * this.zd);
        float yRot = (float) (Mth.atan2(this.xd, this.zd));
        float xRot = (float) (Mth.atan2(this.yd, horizontalDist));

        baseRotation.rotateY(yRot);
        baseRotation.rotateX(-xRot + (float)(Math.PI));

        Vector3f[] vertices = new Vector3f[]{
                new Vector3f(-1.0F, -1.0F, 0.0F),
                new Vector3f(-1.0F, 1.0F, 0.0F),
                new Vector3f(1.0F, 1.0F, 0.0F),
                new Vector3f(1.0F, -1.0F, 0.0F)
        };

        float size = this.getQuadSize(partialTicks);
        float u0 = this.getU0();
        float u1 = this.getU1();
        float v0 = this.getV0();
        float v1 = this.getV1();
        int light = this.getLightColor(partialTicks);

        for (int i = 0; i < 2; i++) {

            Quaternionf currentRot = new Quaternionf(baseRotation);

            if (i == 1) {
                currentRot.rotateY((float) Math.PI);
            }

            Vector3f v_0 = transform(vertices[0], currentRot, size, x, y, z);
            buffer.vertex(v_0.x(), v_0.y(), v_0.z()).uv(u1, v1).color(this.rCol, this.gCol, this.bCol, this.alpha).uv2(light).endVertex();

            Vector3f v_1 = transform(vertices[1], currentRot, size, x, y, z);
            buffer.vertex(v_1.x(), v_1.y(), v_1.z()).uv(u1, v0).color(this.rCol, this.gCol, this.bCol, this.alpha).uv2(light).endVertex();

            Vector3f v_2 = transform(vertices[2], currentRot, size, x, y, z);
            buffer.vertex(v_2.x(), v_2.y(), v_2.z()).uv(u0, v0).color(this.rCol, this.gCol, this.bCol, this.alpha).uv2(light).endVertex();

            Vector3f v_3 = transform(vertices[3], currentRot, size, x, y, z);
            buffer.vertex(v_3.x(), v_3.y(), v_3.z()).uv(u0, v1).color(this.rCol, this.gCol, this.bCol, this.alpha).uv2(light).endVertex();
        }
    }

    private Vector3f transform(Vector3f original, Quaternionf rot, float scale, float x, float y, float z) {
        Vector3f v = new Vector3f(original);
        v.rotate(rot);
        v.mul(scale);
        v.add(x, y, z);
        return v;
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    public static class Provider implements ParticleProvider<ShockwaveParticleOptions> {
        private final SpriteSet spriteSet;
        public Provider(SpriteSet spriteSet) { this.spriteSet = spriteSet; }

        @Override
        public Particle createParticle(ShockwaveParticleOptions options, ClientLevel level, double x, double y, double z, double xd, double yd, double zd) {
            return new ShockwaveParticle(level, x, y, z, xd, yd, zd, this.spriteSet, options);
        }
    }
}