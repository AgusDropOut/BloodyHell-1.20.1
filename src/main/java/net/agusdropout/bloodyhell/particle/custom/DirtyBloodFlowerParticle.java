package net.agusdropout.bloodyhell.particle.custom;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SimpleAnimatedParticle;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;

public class DirtyBloodFlowerParticle extends SimpleAnimatedParticle {
    private double preVX;
    private double preVY;
    private double preVZ;
    private double nextVX;
    private double nextVY;
    private double nextVZ;

    protected DirtyBloodFlowerParticle(ClientLevel world, double x, double y, double z, SpriteSet sprites, double xSpeed, double ySpeed, double zSpeed) {
        super(world, x, y, z, sprites, 0);
        setSprite(sprites.get(random));
        this.lifetime = (int) ((Math.random()*500)+200);
        this.quadSize = (float) ((Math.random()*0.2)+0.05);
        this.setFadeColor(15916745);
        this.setSpriteFromAge(sprites);
        this.setAlpha(0);

        this.friction = 1.0F;
        this.xd =0.0D;
        this.yd=0.0D;
        this.zd = 0.0D;
        this.gravity = 0.0001F;
        this.hasPhysics = true;
    }

    @Override
    public void tick() {

        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;
        if (this.age++ >= this.lifetime) {
            this.remove();
        }

        if (!this.removed) {
            this.yd -= this.gravity;
            if (!this.onGround) {
                this.move(this.xd, this.yd, this.zd);
            } else {
                this.age = Math.max(this.lifetime - 10, this.age);
            }

            if (this.age > this.lifetime - 10) {
                this.scale(Mth.abs(this.age - this.lifetime) * 0.1F);
            }
        }
        super.tick();
    }

    public static class Provider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprites;

        public Provider(SpriteSet sprites) {
            this.sprites = sprites;
        }

        @Override
        public Particle createParticle(SimpleParticleType type, ClientLevel world, double x, double y, double z, double vX, double vY, double vZ) {
            return new DirtyBloodFlowerParticle(world, x, y, z, sprites, 1, 1, 1);
        }
    }
}
