package net.agusdropout.bloodyhell.entity.custom;

import net.agusdropout.bloodyhell.item.custom.BlasphemousTwinDaggerItem;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.Optional;
import java.util.UUID;

public class BlasphemousTwinDaggersCloneEntity extends PathfinderMob implements GeoEntity {

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);


    private static final EntityDataAccessor<Optional<UUID>> OWNER_UUID =
            SynchedEntityData.defineId(BlasphemousTwinDaggersCloneEntity.class, EntityDataSerializers.OPTIONAL_UUID);


    private boolean isAttacking = false;
    private int attackTimer = 0;

    public BlasphemousTwinDaggersCloneEntity(EntityType<? extends PathfinderMob> type, Level level) {
        super(type, level);
        this.setNoGravity(true);
        this.noPhysics = true;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return PathfinderMob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 100.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.0D);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(OWNER_UUID, Optional.empty());
    }


    public void setOwner(Player player) {
        this.entityData.set(OWNER_UUID, Optional.of(player.getUUID()));
        this.setPos(player.getX(), player.getY(), player.getZ());
    }

    public Player getOwner() {
        Optional<UUID> uuid = this.entityData.get(OWNER_UUID);
        return uuid.map(value -> this.level().getPlayerByUUID(value)).orElse(null);
    }


    @Override
    public void tick() {
        super.tick();

        Player owner = getOwner();


        if (!this.level().isClientSide) {
            if (owner == null || !owner.isAlive() || !(owner.getMainHandItem().getItem() instanceof BlasphemousTwinDaggerItem)) {
                this.discard();
                return;
            }


            if (isAttacking) {
                attackTimer--;
                if (attackTimer <= 0) {
                    isAttacking = false;
                }
            }
        }


        if (owner != null) {

            this.setPos(owner.getX(), owner.getY(), owner.getZ());

            // Copiar rotaciones (Cuerpo y Cabeza)
            this.setYRot(owner.getYRot());
            this.setXRot(owner.getXRot());
            this.yBodyRot = owner.yBodyRot;
            this.yHeadRot = owner.yHeadRot;
            this.yHeadRotO = owner.yHeadRotO;
            this.yBodyRotO = owner.yBodyRotO;
        }
    }


    public void triggerAttack(String animName, int durationTicks) {
        if (!this.level().isClientSide) {
            this.isAttacking = true;
            this.attackTimer = durationTicks;

            this.triggerAnim("controller", animName);
        }
    }


    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "controller", 5, state -> {


                    if (this.isAttacking) {
                        return PlayState.CONTINUE;
                    }

                    // B. MOVIMIENTO (WALK / RUN / IDLE)
                    Player owner = getOwner();
                    if (owner != null) {

                        double dx = owner.getX() - owner.xo;
                        double dz = owner.getZ() - owner.zo;
                        double speed = Math.sqrt(dx * dx + dz * dz);


                        if (speed > 0.005) {



                            state.getController().setAnimationSpeed((float)(speed * 3.5));


                            if (speed > 0.22) {
                                return state.setAndContinue(RawAnimation.begin().thenLoop("run"));
                            } else {
                                return state.setAndContinue(RawAnimation.begin().thenLoop("walk"));
                            }
                        }
                    }


                    state.getController().setAnimationSpeed(1.0f);
                    return state.setAndContinue(RawAnimation.begin().thenLoop("idle"));
                })

                        .triggerableAnim("attack", RawAnimation.begin().thenPlay("attack"))
                        .triggerableAnim("special_attack", RawAnimation.begin().thenPlay("special_attack"))
        );
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        if (this.entityData.get(OWNER_UUID).isPresent()) {
            tag.putUUID("Owner", this.entityData.get(OWNER_UUID).get());
        }
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        if (tag.hasUUID("Owner")) {
            this.entityData.set(OWNER_UUID, Optional.of(tag.getUUID("Owner")));
        }
    }
}