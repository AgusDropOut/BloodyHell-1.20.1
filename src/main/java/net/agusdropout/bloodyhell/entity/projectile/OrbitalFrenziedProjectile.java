package net.agusdropout.bloodyhell.entity.projectile;


import net.agusdropout.bloodyhell.entity.ModEntityTypes;
import net.agusdropout.bloodyhell.entity.effects.FrenziedFireEntity;
import net.agusdropout.bloodyhell.particle.ParticleOptions.FrenziedExplosionParticleOptions;
import net.agusdropout.bloodyhell.particle.ParticleOptions.TinyBloomParticleOptions;
import net.agusdropout.bloodyhell.util.visuals.ParticleHelper;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.ThrowableProjectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkHooks;
import org.joml.Vector3f;

import java.util.ArrayDeque;
import java.util.Deque;

public class OrbitalFrenziedProjectile extends ThrowableProjectile {

    public final Deque<Vec3> historyCore = new ArrayDeque<>();
    public final Deque<Vec3> historyOrbit1 = new ArrayDeque<>();
    public final Deque<Vec3> historyOrbit2 = new ArrayDeque<>();

    public final int maxHistory = 15;
    public final float coreWidth = 0.4F;
    public final float orbitWidth = 0.25F;

    private final double orbitRadius = 0.35D;
    private final double orbitSpeed = 0.4D;

    public OrbitalFrenziedProjectile(EntityType<? extends ThrowableProjectile> type, Level level) {
        super(type, level);
    }

    @Override
    protected void defineSynchedData() {
    }

    @Override
    public void tick() {
        super.tick();

        if (this.level().isClientSide) {
            this.historyCore.addFirst(this.position());

            double currentAngle = this.tickCount * this.orbitSpeed;
            double offsetX = Math.cos(currentAngle) * this.orbitRadius;
            double offsetZ = Math.sin(currentAngle) * this.orbitRadius;

            this.historyOrbit1.addFirst(new Vec3(this.getX() + offsetX, this.getY(), this.getZ() + offsetZ));
            this.historyOrbit2.addFirst(new Vec3(this.getX() - offsetX, this.getY(), this.getZ() - offsetZ));

            if (this.historyCore.size() > maxHistory) {
                this.historyCore.removeLast();
                this.historyOrbit1.removeLast();
                this.historyOrbit2.removeLast();
            }

            if (this.level().random.nextFloat() < 0.8F) {
                boolean firstOrbit = this.level().random.nextBoolean();
                double sign = firstOrbit ? 1.0D : -1.0D;

                double spawnX = this.getX() + (offsetX * sign);
                double spawnZ = this.getZ() + (offsetZ * sign);

                double tangVelX = -this.orbitRadius * this.orbitSpeed * Math.sin(currentAngle) * sign;
                double tangVelZ = this.orbitRadius * this.orbitSpeed * Math.cos(currentAngle) * sign;

                Vec3 vel = this.getDeltaMovement();
                double sparkVelX = vel.x + tangVelX + (this.level().random.nextDouble() - 0.5D) * 0.05D;
                double sparkVelY = vel.y + (this.level().random.nextDouble() * 0.05D);
                double sparkVelZ = vel.z + tangVelZ + (this.level().random.nextDouble() - 0.5D) * 0.05D;

                float size = 0.03F + this.level().random.nextFloat() * 0.05F;

                Vector3f color = ParticleHelper.gradient3(this.random.nextFloat(),
                        new Vector3f(0.8F, 0.3F, 0.05F),
                        new Vector3f(0.8F, 0.5F, 0.05F),
                        new Vector3f(0.7F, 0.6F, 0.02F));

                this.level().addParticle(new TinyBloomParticleOptions(color, size),
                        spawnX, this.getY(), spawnZ,
                        sparkVelX, sparkVelY, sparkVelZ);
            }
        }
    }

    @Override
    protected void onHit(HitResult result) {
        super.onHit(result);

        if (!this.level().isClientSide) {
            Vec3 impactPos = result.getLocation();

            FrenziedFireEntity fireHazard = new FrenziedFireEntity(ModEntityTypes.FRENZIED_FIRE.get(), this.level());
            fireHazard.setPos(impactPos.x, impactPos.y, impactPos.z);
            this.level().addFreshEntity(fireHazard);

            if (this.level() instanceof ServerLevel serverLevel) {
                serverLevel.sendParticles(new FrenziedExplosionParticleOptions(2.5F),
                        impactPos.x, impactPos.y, impactPos.z,
                        1, 0.0D, 0.0D, 0.0D, 0.0D);
            }

            this.discard();
        }
    }



    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }
}