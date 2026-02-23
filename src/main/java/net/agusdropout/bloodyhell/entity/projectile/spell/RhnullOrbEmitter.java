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
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkHooks;
import org.joml.Vector3f;

import java.util.List;

public class RhnullOrbEmitter extends Projectile implements IGemSpell {

    private int maxDuration = 100; // Lasts 5 seconds by default
    private float damage = 4.0f;   // Passed down to the droplets
    private float spread = 0.25f;  // How wide the cone is
    private float fireRate = 3;
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


        // 1. Check Lifetime
        if (this.tickCount > maxDuration) {
            if (!this.level().isClientSide) this.discard();
            return;
        }

        // 2. Server-side Firing Logic
        if (!this.level().isClientSide) {
            if (this.tickCount % fireRate == 0) {
                fireDropletInCone();
            }
        } else {

            if(this.random.nextFloat() < 0.3f) {
                Vector3f gradientColor = ParticleHelper.gradient3(random.nextFloat(), SpellPalette.RHNULL.getColor(0), SpellPalette.RHNULL.getColor(1), SpellPalette.RHNULL.getColor(2));
                ParticleHelper.spawnRisingBurst(this.level(), new GlitterParticleOptions(gradientColor, 0.5f, false, 40, true), this.position(), 1,0.5, 0.01f, 0.05);
                ParticleHelper.spawnRisingBurst(this.level(), new MagicParticleOptions(gradientColor, 0.5f, false, 40, true), this.position(), 1,0.5, 0.01f, -0.05);
            }

            if(this.lifeTicks  == 1) {

                    ParticleHelper.spawn(this.level(), new EtherealSwirlOptions(SpellPalette.RHNULL.getColor(1), this.maxDuration, 1.0f), this.position(), 0, 1, 0);

            }


            lifeTicks++;
        }
    }

    private void fireDropletInCone() {
        if (!(this.getOwner() instanceof LivingEntity owner)) return;

        // 1. Get the base direction the owner is looking
        Vec3 lookVec = owner.getLookAngle();

        // 2. Add randomized spread to create the "Cone"
        RandomSource rand = this.random;
        Vec3 randomizedDir = lookVec.add(
                (rand.nextDouble() - 0.5) * spread,
                (rand.nextDouble() - 0.5) * spread,
                (rand.nextDouble() - 0.5) * spread
        ).normalize(); // Normalize it so the droplets always travel at the exact same speed

        // 3. Spawn the Droplet (We will create this class next!)
        RhnullDropletEntity droplet = new RhnullDropletEntity(this.level(), this.getX(), this.getY(), this.getZ());
        droplet.setOwner(owner);
        droplet.setDamage(this.damage);

        // Shoot it super fast (scale of 2.0 or 3.0 blocks per tick)
        droplet.setDeltaMovement(randomizedDir.scale(2.5));

        this.level().addFreshEntity(droplet);

        // Play a rapid, light firing sound
        this.level().playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.AMETHYST_BLOCK_CHIME, SoundSource.PLAYERS, 2.0f, 1.5f + (rand.nextFloat() * 0.5f));
    }

    public int getLifeTicks() { return this.lifeTicks; }

    // --- IGemSpell ---
    @Override
    public void increaseSpellDamage(double amount) { this.damage += amount; }
    @Override
    public void increaseSpellSize(double amount) { this.spread += (amount * 0.1); } // Bigger gem = wider cone!
    @Override
    public void increaseSpellDuration(int amount) { this.maxDuration += amount; }

    // --- BOILERPLATE ---
    @Override
    protected void readAdditionalSaveData(CompoundTag tag) { }
    @Override
    protected void addAdditionalSaveData(CompoundTag tag) { }
    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() { return NetworkHooks.getEntitySpawningPacket(this); }
}