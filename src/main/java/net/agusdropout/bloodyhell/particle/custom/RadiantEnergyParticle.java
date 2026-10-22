package net.agusdropout.bloodyhell.particle.custom;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.agusdropout.bloodyhell.client.render.effect.RadiantEnergyRenderManager;
import net.agusdropout.bloodyhell.client.shader.ShaderTime;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;

public class RadiantEnergyParticle extends Particle {

    private final Deque<Vec3> history = new ArrayDeque<>();
    private final int maxHistory = 18;
    private final float width;

    protected RadiantEnergyParticle(ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
        super(level, x, y, z);
        this.xd = xSpeed;
        this.yd = ySpeed;
        this.zd = zSpeed;

        this.width = 0.5F + (this.random.nextFloat() * 0.2F);
        this.lifetime = 45 + this.random.nextInt(30);
        this.alpha = 1.0F;
        this.rCol = 1.0F;
        this.gCol = 1.0F;
        this.bCol = 1.0F;

        this.history.addFirst(new Vec3(this.x, this.y, this.z));
    }

    @Override
    public void tick() {
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;

        if (this.age++ >= this.lifetime) {
            this.remove();
        } else {
            this.move(this.xd, this.yd, this.zd);

            this.xd *= 0.92D;
            this.yd *= 0.92D;
            this.zd *= 0.92D;

            this.yd += 0.015D;

            this.history.addFirst(new Vec3(this.x, this.y, this.z));
            if (this.history.size() > maxHistory) {
                this.history.removeLast();
            }
        }
    }

    @Override
    public void render(VertexConsumer buffer, Camera camera, float partialTicks) {
        if (this.history.size() < 2) return;

        float time = ShaderTime.seconds();
        Vec3 cameraPos = camera.getPosition();

        float lifeFade = 1.0F - ((float)this.age / (float)this.lifetime);

        RadiantEnergyRenderManager.addTrail(
                new ArrayList<>(this.history),
                cameraPos,
                this.width,
                this.rCol,
                this.gCol,
                this.bCol,
                this.alpha * lifeFade,
                time
        );
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.CUSTOM;
    }

    public static class Provider implements ParticleProvider<SimpleParticleType> {
        @Override
        public Particle createParticle(SimpleParticleType type, ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            return new RadiantEnergyParticle(level, x, y, z, xSpeed, ySpeed, zSpeed);
        }
    }
}