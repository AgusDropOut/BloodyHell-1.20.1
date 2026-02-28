package net.agusdropout.bloodyhell.entity.projectile.spell;

import net.agusdropout.bloodyhell.entity.interfaces.IGemSpell;
import net.agusdropout.bloodyhell.item.custom.base.Gem;
import net.agusdropout.bloodyhell.particle.ParticleOptions.EtherealSwirlOptions;
import net.agusdropout.bloodyhell.particle.ParticleOptions.GlitterParticleOptions;
import net.agusdropout.bloodyhell.particle.ParticleOptions.MagicParticleOptions;
import net.agusdropout.bloodyhell.util.visuals.ParticleHelper;
import net.agusdropout.bloodyhell.util.visuals.SpellPalette;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkHooks;
import org.joml.Vector3f;

import java.util.List;

public class RhnullOrbEmitter extends Projectile implements IGemSpell {

    private int maxDuration = 100;
    private float damage = 4.0f;
    private float spread = 0.25f;


    private float shotsPerTick = 1.0f / 3.0f;
    private float shotAccumulator = 0.0f;

    private int lifeTicks;

    public RhnullOrbEmitter(EntityType<? extends Projectile> type, Level level) {
        super(type, level);
    }

    public RhnullOrbEmitter(EntityType<? extends Projectile> type, Level level, LivingEntity owner, double x, double y, double z, List<Gem> gems) {
        super(type, level);
        this.setOwner(owner);
        this.setPos(x, y, z);
        this.configureSpell(gems);
    }

    @Override
    protected void defineSynchedData() { }

    @Override
    public void tick() {
        super.tick();

        if (this.tickCount > maxDuration) {
            if (!this.level().isClientSide) this.discard();
            return;
        }

        if (!this.level().isClientSide) {
            this.shotAccumulator += this.shotsPerTick;

            /* While loop allows multiple shots per tick if rate exceeds 1.0 */
            while (this.shotAccumulator >= 1.0f) {
                fireDropletInCone();
                this.shotAccumulator -= 1.0f;
            }
            trackOwner();
        } else {
            handleClientEffects();
            lifeTicks++;
        }

        this.move(MoverType.SELF, this.getDeltaMovement());
    }

    private void trackOwner() {
        if (this.getOwner() instanceof LivingEntity owner) {
            Vec3 forward = owner.getLookAngle();
            Vec3 up = new Vec3(0, 1, 0);
            Vec3 right = forward.cross(up).normalize();

            Vec3 targetPos = owner.position()
                    .add(0, owner.getEyeHeight() + 0.2, 0)
                    .add(right.scale(1.5))
                    .add(forward.scale(0.6));

            targetPos = targetPos.add(0, Math.sin(this.tickCount * 0.1) * 0.1, 0);

            Vec3 diff = targetPos.subtract(this.position());
            this.setDeltaMovement(diff.scale(0.2));
        }
    }

    private void fireDropletInCone() {
        if (!(this.getOwner() instanceof LivingEntity owner)) return;

        Vec3 lookVec = owner.getLookAngle();
        RandomSource rand = this.random;
        Vec3 randomizedDir = lookVec.add(
                (rand.nextDouble() - 0.5) * spread,
                (rand.nextDouble() - 0.5) * spread,
                (rand.nextDouble() - 0.5) * spread
        ).normalize();

        RhnullDropletEntity droplet = new RhnullDropletEntity(this.level(), this.getX(), this.getY(), this.getZ());
        droplet.setOwner(owner);
        droplet.setDamage(this.damage);
        droplet.setDeltaMovement(randomizedDir.scale(2.5));

        this.level().addFreshEntity(droplet);
        this.level().playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.AMETHYST_BLOCK_CHIME, SoundSource.PLAYERS, 2.0f, 1.5f + (rand.nextFloat() * 0.5f));
    }

    private void handleClientEffects(){
        if(this.random.nextFloat() < 0.3f) {
            Vector3f gradientColor = ParticleHelper.gradient3(random.nextFloat(), SpellPalette.RHNULL.getColor(0), SpellPalette.RHNULL.getColor(1), SpellPalette.RHNULL.getColor(2));
            ParticleHelper.spawnRisingBurst(this.level(), new GlitterParticleOptions(gradientColor, 0.5f, false, 40, true), this.position(), 1,0.5, 0.01f, 0.05);
            ParticleHelper.spawnRisingBurst(this.level(), new MagicParticleOptions(gradientColor, 0.5f, false, 40, true), this.position(), 1,0.5, 0.01f, -0.05);
        }
        if(this.lifeTicks  == 1) {
            ParticleHelper.spawn(this.level(), new EtherealSwirlOptions(SpellPalette.RHNULL.getColor(1), this.maxDuration, 1.0f, this.getId()), this.position(), 0, 1, 0);
        }
    }

    public int getLifeTicks() { return this.lifeTicks; }

    @Override
    public void increaseSpellDamage(double amount) { this.damage += amount; }
    @Override
    public void increaseSpellSize(double amount) { this.spread += (amount * 0.1); }
    @Override
    public void increaseSpellDuration(int amount) { this.maxDuration += amount; }

    @Override
    public void increaseSpellQuantity(double amount) {
        this.shotsPerTick += (float) (amount * 0.15f);
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) { }
    @Override
    protected void addAdditionalSaveData(CompoundTag tag) { }
    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() { return NetworkHooks.getEntitySpawningPacket(this); }
}