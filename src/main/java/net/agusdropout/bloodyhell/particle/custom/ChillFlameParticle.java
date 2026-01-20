package net.agusdropout.bloodyhell.particle.custom;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class ChillFlameParticle extends TextureSheetParticle {

    private final Quaternionf customRotation = new Quaternionf();

    // Variables to track rotation momentum
    private float rotSpeedX;
    private float rotSpeedY;
    private float rotSpeedZ;

    protected ChillFlameParticle(ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed, SpriteSet spriteSet) {
        super(level, x, y, z, xSpeed, ySpeed, zSpeed);

        this.friction = 0.98F;

        // --- YOUR CUSTOM VALUES (Preserved) ---
        this.gravity = 0.0F;
        this.alpha = 0.6F + this.random.nextFloat() * 0.4F;
        this.rCol = 1.0F;
        this.gCol = 1.0F;
        this.bCol = 1.0F;
        this.roll = 10.0F + this.random.nextFloat() * 20.0F;
        this.quadSize *= 1.5F;
        this.lifetime = 30 + this.random.nextInt(20);

        // --- AGGRESSIVE ROTATION SETUP ---
        // 1. Start much faster (Range: -0.25 to 0.25 radians per tick)
        // Previously this was 0.05. We increased it 5x.
        this.rotSpeedX = (this.random.nextFloat() - 0.5F) * 0.5F;
        this.rotSpeedY = (this.random.nextFloat() - 0.5F) * 0.5F;
        this.rotSpeedZ = (this.random.nextFloat() - 0.5F) * 0.5F;

        // Initial random orientation
        this.customRotation.rotationXYZ(
                this.random.nextFloat() * Mth.TWO_PI,
                this.random.nextFloat() * Mth.TWO_PI,
                this.random.nextFloat() * Mth.TWO_PI
        );

        this.pickSprite(spriteSet);
    }

    @Override
    public void tick() {
        super.tick();

        // --- GRAVITY LOGIC (Preserved) ---
        if (this.age < 10) {
            this.gravity += 0.05F;
        }

        // --- SPIN LOGIC (No Trig, Pure Rotation) ---

        // 1. Apply current speed to the rotation
        // This spins it "all the way around" continuously
        this.customRotation.rotateXYZ(rotSpeedX, rotSpeedY, rotSpeedZ);



        // --- FADE LOGIC (Preserved) ---
        float lifeRatio = (float)this.age / (float)this.lifetime;
        if (lifeRatio > 0.5F) {
            this.alpha *= 0.01F;
        }
    }

    @Override
    public int getLightColor(float partialTick) {
        return 15728880;
    }


    @Override
    public void render(VertexConsumer buffer, Camera camera, float partialTicks) {
        Vec3 vec3 = camera.getPosition();
        float x = (float)(Mth.lerp(partialTicks, this.xo, this.x) - vec3.x());
        float y = (float)(Mth.lerp(partialTicks, this.yo, this.y) - vec3.y());
        float z = (float)(Mth.lerp(partialTicks, this.zo, this.z) - vec3.z());

        Quaternionf q = new Quaternionf(this.customRotation);

        Vector3f[] avector3f = new Vector3f[]{
                new Vector3f(-1.0F, -1.0F, 0.0F),
                new Vector3f(-1.0F, 1.0F, 0.0F),
                new Vector3f(1.0F, 1.0F, 0.0F),
                new Vector3f(1.0F, -1.0F, 0.0F)
        };

        float f4 = this.getQuadSize(partialTicks);

        for(int i = 0; i < 4; ++i) {
            Vector3f vector3f = avector3f[i];
            vector3f.rotate(q);
            vector3f.mul(f4);
            vector3f.add(x, y, z);
        }

        float u0 = this.getU0(); float u1 = this.getU1();
        float v0 = this.getV0(); float v1 = this.getV1();
        int light = this.getLightColor(partialTicks);

        buffer.vertex(avector3f[0].x(), avector3f[0].y(), avector3f[0].z()).uv(u1, v1).color(this.rCol, this.gCol, this.bCol, this.alpha).uv2(light).endVertex();
        buffer.vertex(avector3f[1].x(), avector3f[1].y(), avector3f[1].z()).uv(u1, v0).color(this.rCol, this.gCol, this.bCol, this.alpha).uv2(light).endVertex();
        buffer.vertex(avector3f[2].x(), avector3f[2].y(), avector3f[2].z()).uv(u0, v0).color(this.rCol, this.gCol, this.bCol, this.alpha).uv2(light).endVertex();
        buffer.vertex(avector3f[3].x(), avector3f[3].y(), avector3f[3].z()).uv(u0, v1).color(this.rCol, this.gCol, this.bCol, this.alpha).uv2(light).endVertex();
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    public static class Provider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet spriteSet;
        public Provider(SpriteSet spriteSet) { this.spriteSet = spriteSet; }

        @Nullable
        @Override
        public Particle createParticle(SimpleParticleType type, ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            return new ChillFlameParticle(level, x, y, z, xSpeed, ySpeed, zSpeed, this.spriteSet);
        }
    }
}