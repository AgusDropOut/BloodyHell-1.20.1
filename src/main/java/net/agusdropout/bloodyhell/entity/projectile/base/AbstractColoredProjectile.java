package net.agusdropout.bloodyhell.entity.projectile.base;


import net.agusdropout.bloodyhell.entity.client.base.SimpleColorProjectile;
import net.agusdropout.bloodyhell.util.visuals.ColorHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkHooks;
import org.joml.Vector3f;

public abstract class AbstractColoredProjectile extends Projectile implements SimpleColorProjectile {

    private static final EntityDataAccessor<Integer> BASE_COLOR = SynchedEntityData.defineId(AbstractColoredProjectile.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> HIGHLIGHT_COLOR = SynchedEntityData.defineId(AbstractColoredProjectile.class, EntityDataSerializers.INT);

    protected float damage = 4.0f;
    protected int lifeTicks = 0;
    protected int maxLifeTicks = 60;

    protected AbstractColoredProjectile(EntityType<? extends Projectile> type, Level level) {
        super(type, level);
    }

    @Override
    protected void defineSynchedData() {
        this.entityData.define(BASE_COLOR, 0xFFFFFF);
        this.entityData.define(HIGHLIGHT_COLOR, 0xFFFFFF);
    }

    public void setProjectileColors(int baseColorHex, int highlightColorHex) {
        this.entityData.set(BASE_COLOR, baseColorHex);
        this.entityData.set(HIGHLIGHT_COLOR, highlightColorHex);
    }

    public void setDamage(float damage) {
        this.damage = damage;
    }

    @Override
    public Vector3f getBaseColor() {
        return ColorHelper.hexToVector3f(this.entityData.get(BASE_COLOR));
    }

    @Override
    public Vector3f getHighlightColor() {
        return ColorHelper.hexToVector3f(this.entityData.get(HIGHLIGHT_COLOR));
    }

    @Override
    public void tick() {
        super.tick();
        this.lifeTicks++;

        Vec3 movement = this.getDeltaMovement();
        HitResult hitresult = ProjectileUtil.getHitResultOnMoveVector(this, this::canHitEntity);

        if (hitresult.getType() != HitResult.Type.MISS && !net.minecraftforge.event.ForgeEventFactory.onProjectileImpact(this, hitresult)) {
            this.onHit(hitresult);
        }

        this.setPos(this.getX() + movement.x, this.getY() + movement.y, this.getZ() + movement.z);

        if (this.level().isClientSide) {
            handleClientEffects();
        }

        this.setDeltaMovement(movement.scale(0.99f));

        if (this.tickCount > this.maxLifeTicks) {
            this.discard();
        }
    }

    protected abstract void handleClientEffects();

    @Override
    protected void onHitEntity(EntityHitResult result) {
        super.onHitEntity(result);
        if (!this.level().isClientSide && result.getEntity() instanceof LivingEntity target) {
            target.hurt(this.damageSources().magic(), this.damage);
            target.invulnerableTime = 0;
            this.discard();
        }
    }

    @Override
    protected void onHit(HitResult result) {
        super.onHit(result);
        if (!this.level().isClientSide) {
            this.discard();
        }
    }

    public int getLifeTicks() {
        return this.lifeTicks;
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        tag.putInt("BaseColor", this.entityData.get(BASE_COLOR));
        tag.putInt("HighlightColor", this.entityData.get(HIGHLIGHT_COLOR));
        tag.putFloat("Damage", this.damage);
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        if (tag.contains("BaseColor")) this.entityData.set(BASE_COLOR, tag.getInt("BaseColor"));
        if (tag.contains("HighlightColor")) this.entityData.set(HIGHLIGHT_COLOR, tag.getInt("HighlightColor"));
        if (tag.contains("Damage")) this.damage = tag.getFloat("Damage");
    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }
}