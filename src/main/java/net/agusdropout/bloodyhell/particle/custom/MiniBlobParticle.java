package net.agusdropout.bloodyhell.particle.custom;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.agusdropout.bloodyhell.particle.ModParticles;
import net.agusdropout.bloodyhell.particle.ParticleOptions.MiniBlobParticleOptions;
import net.agusdropout.bloodyhell.util.visuals.manager.MiniBlobRenderManager;
import net.agusdropout.bloodyhell.util.visuals.types.MiniBlob;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.joml.Vector3f;

@OnlyIn(Dist.CLIENT)
public class MiniBlobParticle extends Particle {

    private final float blobSize;

    protected MiniBlobParticle(ClientLevel level, double x, double y, double z, double dx, double dy, double dz, float size, Vector3f color, boolean hasGravity) {
        super(level, x, y, z);
        this.xd = dx;
        this.yd = dy;
        this.zd = dz;
        this.blobSize = size;
        this.lifetime = 60 + this.random.nextInt(40);
        this.hasPhysics = true;

        if (hasGravity) {
            this.gravity = 1.0f;
            this.friction = 0.98f;
        } else {
            this.gravity = 0.0f;
            this.friction = 0.95f;
        }

        this.rCol = color.x();
        this.gCol = color.y();
        this.bCol = color.z();
    }

    @Override
    public void tick() {
        super.tick();
        if (this.onGround) {
            this.remove();
            this.level.addParticle(ModParticles.BLOOD_STAIN_PARTICLE.get(), this.x, this.y + 0.01, this.z, 0, 0, 0);
        }
    }

    @Override
    public void render(VertexConsumer buffer, Camera camera, float partialTicks) {
        double lerpX = Mth.lerp(partialTicks, this.xo, this.x);
        double lerpY = Mth.lerp(partialTicks, this.yo, this.y);
        double lerpZ = Mth.lerp(partialTicks, this.zo, this.z);

        MiniBlobRenderManager.addMiniBlob(new MiniBlob(new Vec3(lerpX, lerpY, lerpZ), this.blobSize, this.rCol, this.gCol, this.bCol));
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.CUSTOM;
    }

    public static class Provider implements ParticleProvider<MiniBlobParticleOptions> {
        public Provider() {}

        @Override
        public Particle createParticle(MiniBlobParticleOptions option, ClientLevel level, double x, double y, double z, double dx, double dy, double dz) {
            return new MiniBlobParticle(level, x, y, z, dx, dy, dz, option.getSize(), option.getColor(), option.hasGravity());
        }
    }
}