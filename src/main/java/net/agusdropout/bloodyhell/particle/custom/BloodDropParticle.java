package net.agusdropout.bloodyhell.particle.custom;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.agusdropout.bloodyhell.particle.ModParticles;
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
public class BloodDropParticle extends TextureSheetParticle {

    protected BloodDropParticle(ClientLevel level, double x, double y, double z) {
        super(level, x, y, z);
        this.gravity = 1.0F;
        this.friction = 0.98F;
        this.xd *= 0.1;
        this.yd *= 0.1;
        this.zd *= 0.1;
        this.quadSize = 0.1F;
        this.lifetime = 100;
        this.hasPhysics = true;

        // Color: Deep Red
        this.rCol = 0.6f;
        this.gCol = 0.0f;
        this.bCol = 0.0f;
    }

    @Override
    public void tick() {
        super.tick();


        if (this.onGround) {

            this.remove();
            this.level.addParticle(ModParticles.BLOOD_STAIN_PARTICLE.get(), this.x, this.y + 0.01, this.z, 0, 0, 0);
            for (int i = 0; i < 4; i++) {
                this.level.addParticle(ModParticles.BLOOD_PARTICLES.get(),
                        this.x, this.y, this.z,
                        (random.nextDouble() - 0.5) * 0.2,
                        random.nextDouble() * 0.2,
                        (random.nextDouble() - 0.5) * 0.2
                );
            }
        }
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_OPAQUE;
    }




    public static class Provider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet spriteSet;
        public Provider(SpriteSet spriteSet) { this.spriteSet = spriteSet; }
        @Override
        public Particle createParticle(SimpleParticleType type, ClientLevel level, double x, double y, double z, double dx, double dy, double dz) {
            BloodDropParticle p = new BloodDropParticle(level, x, y, z);
            p.pickSprite(this.spriteSet);
            return p;
        }
    }
}