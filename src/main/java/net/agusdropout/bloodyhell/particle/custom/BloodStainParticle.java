package net.agusdropout.bloodyhell.particle.custom;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.joml.Quaternionf;
import org.joml.Vector3f;

@OnlyIn(Dist.CLIENT)
public class BloodStainParticle extends TextureSheetParticle {

    private final float rotAngle;

    protected BloodStainParticle(ClientLevel level, double x, double y, double z) {
        super(level, x, y, z);
        this.gravity = 0;
        this.hasPhysics = false;
        this.lifetime = 200 + random.nextInt(100);
        this.quadSize = 0.05F + random.nextFloat() * 0.1F;
        this.rotAngle = random.nextFloat() * Mth.TWO_PI;

        this.rCol = 0.5f;
        this.gCol = 0.0f;
        this.bCol = 0.0f;
        this.alpha = 0.9f;
    }

    @Override
    public void tick() {
        if (this.age++ >= this.lifetime) {
            this.remove();
        } else {

            if (this.age > this.lifetime - 20) {
                this.alpha -= 0.045f;
            }
        }
    }

    @Override
    public void render(VertexConsumer buffer, Camera camera, float partialTicks) {
        Vec3 camPos = camera.getPosition();
        float x = (float) (Mth.lerp(partialTicks, this.xo, this.x) - camPos.x());
        float y = (float) (Mth.lerp(partialTicks, this.yo, this.y) - camPos.y());
        float z = (float) (Mth.lerp(partialTicks, this.zo, this.z) - camPos.z());



        float size = this.getQuadSize(partialTicks);
        float u0 = this.getU0();
        float u1 = this.getU1();
        float v0 = this.getV0();
        float v1 = this.getV1();
        int light = this.getLightColor(partialTicks);


        float cos = Mth.cos(this.rotAngle);
        float sin = Mth.sin(this.rotAngle);


        Vector3f[] vertices = new Vector3f[]{
                new Vector3f(-1.0F * size, 0, -1.0F * size),
                new Vector3f(-1.0F * size, 0,  1.0F * size),
                new Vector3f( 1.0F * size, 0,  1.0F * size),
                new Vector3f( 1.0F * size, 0, -1.0F * size)
        };


        for (Vector3f vertex : vertices) {

            float vx = vertex.x();
            float vz = vertex.z();
            vertex.set(
                    vx * cos - vz * sin,
                    0,
                    vx * sin + vz * cos
            );

            vertex.add(x, y, z);
        }


        // Vertex 0
        buffer.vertex(vertices[0].x(), vertices[0].y(), vertices[0].z()).uv(u1, v1).color(this.rCol, this.gCol, this.bCol, this.alpha).uv2(light).endVertex();
        // Vertex 1
        buffer.vertex(vertices[1].x(), vertices[1].y(), vertices[1].z()).uv(u1, v0).color(this.rCol, this.gCol, this.bCol, this.alpha).uv2(light).endVertex();
        // Vertex 2
        buffer.vertex(vertices[2].x(), vertices[2].y(), vertices[2].z()).uv(u0, v0).color(this.rCol, this.gCol, this.bCol, this.alpha).uv2(light).endVertex();
        // Vertex 3
        buffer.vertex(vertices[3].x(), vertices[3].y(), vertices[3].z()).uv(u0, v1).color(this.rCol, this.gCol, this.bCol, this.alpha).uv2(light).endVertex();
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    public static class Provider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet spriteSet;
        public Provider(SpriteSet spriteSet) { this.spriteSet = spriteSet; }
        @Override
        public Particle createParticle(SimpleParticleType type, ClientLevel level, double x, double y, double z, double dx, double dy, double dz) {
            BloodStainParticle p = new BloodStainParticle(level, x, y, z);
            p.pickSprite(this.spriteSet);
            return p;
        }
    }
}