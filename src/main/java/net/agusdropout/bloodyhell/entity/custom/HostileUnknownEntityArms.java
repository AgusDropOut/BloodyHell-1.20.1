package net.agusdropout.bloodyhell.entity.custom;


import net.minecraft.nbt.CompoundTag;

import net.minecraft.network.syncher.EntityDataAccessor;

import net.minecraft.network.syncher.EntityDataSerializers;

import net.minecraft.network.syncher.SynchedEntityData;

import net.minecraft.world.entity.Entity;

import net.minecraft.world.entity.EntityType;

import net.minecraft.world.entity.LivingEntity;

import net.minecraft.world.entity.player.Player;

import net.minecraft.world.level.Level;

import net.minecraft.world.phys.AABB;

import software.bernie.geckolib.animatable.GeoEntity;

import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;

import software.bernie.geckolib.core.animation.AnimatableManager;

import software.bernie.geckolib.core.animation.AnimationController;

import software.bernie.geckolib.core.animation.AnimationState;

import software.bernie.geckolib.core.animation.RawAnimation;

import software.bernie.geckolib.core.object.PlayState;

import software.bernie.geckolib.util.GeckoLibUtil;


import java.util.List;


public class HostileUnknownEntityArms extends Entity implements GeoEntity {


    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);


    private static final EntityDataAccessor<Integer> STATE = SynchedEntityData.defineId(HostileUnknownEntityArms.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> TARGET_ID = SynchedEntityData.defineId(HostileUnknownEntityArms.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> HAS_GRABBED = SynchedEntityData.defineId(HostileUnknownEntityArms.class, EntityDataSerializers.BOOLEAN);


    public static final int STATE_SUMMON = 0;
    public static final int STATE_IDLE = 1;
    public static final int STATE_GRAB = 2;
    public static final int STATE_RETRACT = 3;

    private static final int DEFAULT_GRAB_DELAY = 100;



    private static final double MAX_TARGET_RANGE = 5.0D;


    private int stateTicks = 0;
    private int grabDelayTicks = DEFAULT_GRAB_DELAY;
    public float cachedGrabYaw = 0.0F;
    public float cachedGrabPitch = 0.0F;


    private float grabBoneX, grabBoneY, grabBoneZ;


    public HostileUnknownEntityArms(EntityType<?> type, Level level) {

        super(type, level);

    }


    @Override

    protected void defineSynchedData() {
        this.entityData.define(STATE, STATE_SUMMON);
        this.entityData.define(TARGET_ID, -1);
        this.entityData.define(HAS_GRABBED, false);
    }


    public void setTarget(LivingEntity target) {
        this.entityData.set(TARGET_ID, target == null ? -1 : target.getId());

    }


    public LivingEntity getTarget() {
        int id = this.entityData.get(TARGET_ID);
        if (id != -1 && this.level().getEntity(id) instanceof LivingEntity living) {
            return living;
        }
        return null;
    }


    public int getTentacleState() {
        return this.entityData.get(STATE);
    }


    public boolean hasGrabbed() {
        return this.entityData.get(HAS_GRABBED);
    }

    public void setHasGrabbed(boolean grabbed) {
        this.entityData.set(HAS_GRABBED, grabbed);
    }


    private void setTentacleState(int state) {
        this.entityData.set(STATE, state);
        this.stateTicks = 0;
    }


    public void updateGrabBonePosition(float x, float y, float z) {
        this.grabBoneX = x;
        this.grabBoneY = y;
        this.grabBoneZ = z;
    }


    @Override

    public void tick() {

        super.tick();


        if(!level().isClientSide()){
            System.out.println("Has Grabbed on the server: " + hasGrabbed() + ", Target: " + getTarget() + ", State: " + getTentacleState());
        } else {
            System.out.println("Has Grabbed on the client: " + hasGrabbed() + ", Target: " + getTarget() + ", State: " + getTentacleState());
        }


        if (this.level().isClientSide()) return;


        this.stateTicks++;
        int currentState = getTentacleState();


        if (currentState == STATE_SUMMON) {

            if (this.stateTicks >= 40) {
                setTentacleState(STATE_IDLE);
            }

        } else if (currentState == STATE_IDLE) {
            setHasGrabbed(false);

            if (this.stateTicks % 20 == 0) {
                findAndSetClosestTarget();
            }

            if (getTarget() != null) {
                grabDelayTicks--;
                if (grabDelayTicks <= 0) {
                    setTentacleState(STATE_GRAB);
                    grabDelayTicks = DEFAULT_GRAB_DELAY;
                }
            } else {
                grabDelayTicks = DEFAULT_GRAB_DELAY;
            }

        } else if (currentState == STATE_GRAB) {

            LivingEntity target = getTarget();
            boolean isValidTarget = target != null && target.isAlive() && this.distanceToSqr(target) <= (MAX_TARGET_RANGE * MAX_TARGET_RANGE) ;


            if(target instanceof Player player) {

                isValidTarget = isValidTarget && !player.isCreative() && !player.isSpectator();
                if (!isValidTarget) {
                    setTarget(null);
                    setTentacleState(STATE_IDLE);
                }

            }


            if (isValidTarget) {

                if (this.stateTicks == 30) {
                    setHasGrabbed(true);
                }


                if (this.stateTicks > 30 && this.stateTicks <= 60) {
                    target.setPos(this.grabBoneX, this.grabBoneY, this.grabBoneZ);
                    target.hurtMarked = true;
                }

            }


            if (this.stateTicks >= 60) {

                if(target!= null && target.isAlive()) {
                    target.kill();
                    setTarget(null);
                }

                setTentacleState(STATE_RETRACT);

            }

        } else if (currentState == STATE_RETRACT) {

            LivingEntity target = getTarget();

            if (target != null && target.isAlive()) {
                target.setPos(this.grabBoneX, this.grabBoneY, this.grabBoneZ);
                target.hurtMarked = true;
                setHasGrabbed(false);
            }


            if (this.stateTicks >= 40) {
                setTentacleState(STATE_IDLE);
            }

        }

    }


    private void findAndSetClosestTarget() {

        AABB searchArea = this.getBoundingBox().inflate(MAX_TARGET_RANGE);
        List<LivingEntity> potentialTargets = this.level().getEntitiesOfClass(LivingEntity.class, searchArea,

                entity -> entity.isAlive() && !entity.is(this));


        LivingEntity closestTarget = null;
        double closestDistance = Double.MAX_VALUE;


        for (LivingEntity entity : potentialTargets) {
            double distance = this.distanceToSqr(entity);
            if (distance < closestDistance) {
                closestDistance = distance;
                closestTarget = entity;
            }

        }
        setTarget(closestTarget);

    }


    @Override

    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "controller", 0, this::predicate));

    }


    private PlayState predicate(AnimationState<HostileUnknownEntityArms> event) {
        int state = this.getTentacleState();

        if (state == STATE_SUMMON) {
            event.getController().setAnimation(RawAnimation.begin().thenPlay("summon"));
        } else if (state == STATE_GRAB) {
            event.getController().setAnimation(RawAnimation.begin().thenPlay("grab"));
        } else if (state == STATE_RETRACT) {
            event.getController().setAnimation(RawAnimation.begin().thenPlay("summon"));
        } else {
            event.getController().setAnimation(RawAnimation.begin().thenLoop("idle"));
        }

        return PlayState.CONTINUE;

    }


    @Override

    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;

    }


    @Override

    protected void readAdditionalSaveData(CompoundTag tag) {
        this.setTentacleState(tag.getInt("TentacleState"));

    }


    @Override

    protected void addAdditionalSaveData(CompoundTag tag) {
        tag.putInt("TentacleState", this.getTentacleState());

    }

}