package net.agusdropout.bloodyhell.entity.custom;

import net.agusdropout.bloodyhell.block.entity.custom.mechanism.UnknownPortalBlockEntity;
import net.agusdropout.bloodyhell.entity.effects.EntityCameraShake;
import net.agusdropout.bloodyhell.fluid.ModFluids;
import net.agusdropout.bloodyhell.networking.ModMessages;
import net.agusdropout.bloodyhell.networking.packet.SyncGrabBonePacket;
import net.agusdropout.bloodyhell.particle.ParticleOptions.ChillFallingParticleOptions;
import net.agusdropout.bloodyhell.sound.ModSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import org.joml.Vector3f;
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
    private static final double MAX_TARGET_RANGE = 4.0D;

    private int stateTicks = 0;
    private int grabDelayTicks = DEFAULT_GRAB_DELAY;
    public float cachedGrabYaw = 0.0F;
    public float cachedGrabPitch = 0.0F;

    private float grabBoneX, grabBoneY, grabBoneZ;
    private double initialGrabX;
    private double initialGrabY;
    private double initialGrabZ;
    private int lastProcessedPacketTick = 0;

    private BlockPos portalPos = null;

    private static final byte EVENT_SACRIFICE_KILL = 60;
    private static final byte EVENT_SUMMON = 66;

    public HostileUnknownEntityArms(EntityType<?> type, Level level) {
        super(type, level);
    }

    public void setPortalPos(BlockPos pos) {
        this.portalPos = pos;
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

    public int getTentacleState() { return this.entityData.get(STATE); }
    public boolean hasGrabbed() { return this.entityData.get(HAS_GRABBED); }
    public void setHasGrabbed(boolean grabbed) { this.entityData.set(HAS_GRABBED, grabbed); }

    private void setTentacleState(int state) {
        this.entityData.set(STATE, state);
        this.stateTicks = 0;
    }

    public void updateGrabBonePosition(float x, float y, float z, int currentTick) {
        if (this.lastProcessedPacketTick == currentTick) return;
        this.lastProcessedPacketTick = currentTick;
        this.grabBoneX = x;
        this.grabBoneY = y;
        this.grabBoneZ = z;
    }

    @Override
    public void tick() {
        super.tick();

        hadleClientTickEffects();

        if (this.level().isClientSide()) return;

        if (this.tickCount % 20 == 0) {
            if (this.portalPos == null || !(this.level().getBlockEntity(this.portalPos) instanceof UnknownPortalBlockEntity portal) || portal.portalProgress < 100.0f) {
                this.discard();
                return;
            }
        }

        this.stateTicks++;
        int currentState = getTentacleState();

        if (currentState == STATE_SUMMON) {
            if(this.stateTicks == 1) {
                this.level().broadcastEntityEvent(this, EVENT_SUMMON);
            }

            if (this.stateTicks == 15) {
                EntityCameraShake.cameraShake(
                        this.level(), this.position(), 20.0F, 0.3F, 25, 10
                );
            }
            if (this.stateTicks >= 40) {
                setTentacleState(STATE_IDLE);
            }
        } else if (currentState == STATE_IDLE) {
            setHasGrabbed(false);

            if (this.stateTicks % 20 == 0 && this.getTarget() == null) {
                findAndSetRandomTarget();
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


            boolean isValidTarget = target != null && target.isAlive() && this.distanceToSqr(target) <= (MAX_TARGET_RANGE * MAX_TARGET_RANGE);

            if (isValidTarget && target instanceof Player player) {
                isValidTarget = !player.isCreative() && !player.isSpectator();
            }

            if (!isValidTarget) {
                setTarget(null);
                setTentacleState(STATE_RETRACT);
                return;
            }

            if (this.stateTicks == 30) {
                setHasGrabbed(true);
            }

            calculateBonePos(target);

            if (this.stateTicks > 30 && this.stateTicks < 60) {
                target.setPos(this.grabBoneX, this.grabBoneY, this.grabBoneZ);
                target.setDeltaMovement(0, 0, 0); // Prevent gravity from pulling them down
                target.fallDistance = 0.0F; // Reset fall distance so they don't take damage mid-air
                target.hurtMarked = true;
            }

            if (this.stateTicks >= 60) {
                this.level().broadcastEntityEvent(this, EVENT_SACRIFICE_KILL);

                target.discard();

                if (this.portalPos != null && this.level().getBlockEntity(this.portalPos) instanceof UnknownPortalBlockEntity portal) {
                    portal.outputTank.fill(new FluidStack(ModFluids.VISCOUS_BLASPHEMY_SOURCE.get(), 250), IFluidHandler.FluidAction.EXECUTE);
                }

                setTarget(null);
                setTentacleState(STATE_RETRACT);
            }

        } else if (currentState == STATE_RETRACT) {
            LivingEntity target = getTarget();

            if (target != null && target.isAlive()) {
                target.setPos(this.grabBoneX, this.grabBoneY, this.grabBoneZ);
                target.hurtMarked = true;
                calculateBonePos(target);
                setHasGrabbed(false);
            }

            if (this.stateTicks >= 40) {
                setTentacleState(STATE_IDLE);
            }
        }
    }

    @Override
    public void handleEntityEvent(byte id) {
        if (id == EVENT_SACRIFICE_KILL) {
            handleClientGrabEffects();
        } else if (id == EVENT_SUMMON) {
            handleClientSummonEffects();
        } else {
            super.handleEntityEvent(id);
        }
    }


    private void executeScreamParticles() {

        this.level().playLocalSound(this.getX(), this.getEyeY(), this.getZ(),
                ModSounds.HOSTILE_ARM_SCREAM.get(), this.getSoundSource(), 2.5F, 0.9F + this.random.nextFloat() * 0.2F, false);

        net.minecraft.world.phys.Vec3 lookDir = this.getLookAngle();
        double spawnX = this.getX();
        double spawnY = this.getEyeY();
        double spawnZ = this.getZ();

        for (int i = 0; i < 60; i++) {
            double spreadX = (this.random.nextDouble() - 0.5D) * 0.8D;
            double spreadY = (this.random.nextDouble() - 0.5D) * 0.8D;
            double spreadZ = (this.random.nextDouble() - 0.5D) * 0.8D;

            double velocityX = (lookDir.x + spreadX) * 2.0D;
            double velocityY = (lookDir.y + spreadY) * 2.0D;
            double velocityZ = (lookDir.z + spreadZ) * 2.0D;

            this.level().addParticle(net.minecraft.core.particles.ParticleTypes.POOF,
                    spawnX, spawnY, spawnZ,
                    velocityX, velocityY, velocityZ);
        }
    }

    private void calculateBonePos(LivingEntity target) {
        float horizontalPullFactor = 1.8F;

        if (this.stateTicks <= 30) {
            double dx = target.getX() - this.getX();
            double dz = target.getZ() - this.getZ()+2;
            float targetYaw = (float) -Math.atan2(dz, dx) + 1.5707F;

            float reachProgress = this.stateTicks / 30.0F;
            float easedReach = (float) Math.sin(reachProgress * Math.PI / 2.0);

            this.grabBoneX = (float) Mth.lerp(easedReach, this.getX(), target.getX());
            this.grabBoneY = (float) Mth.lerp(easedReach, this.getY(), target.getY() + target.getBbHeight() / 2.0);
            this.grabBoneZ = (float) Mth.lerp(easedReach, this.getZ(), target.getZ());

            this.setYRot(targetYaw * Mth.RAD_TO_DEG);

            if (this.stateTicks == 30) {
                setHasGrabbed(true);
                this.initialGrabX = target.getX();
                this.initialGrabY = target.getY();
                this.initialGrabZ = target.getZ();
            }

        } else if (this.stateTicks > 30 && this.stateTicks <= 60) {
            float pullProgress = (this.stateTicks - 30) / 30.0F;

            float easedPull = pullProgress < 0.5F
                    ? 4.0F * pullProgress * pullProgress * pullProgress
                    : 1.0F - (float) Math.pow(-2.0F * pullProgress + 2.0F, 3.0F) / 2.0F;

            float easedHorizontal = (float) Math.pow(easedPull, horizontalPullFactor);

            double endX = this.getX();
            double endY = this.getY() + this.getBbHeight();
            double endZ = this.getZ();

            float verticalArc = (float) Math.sin(pullProgress * Math.PI) * 1.9F;

            this.grabBoneX = (float) Mth.lerp(easedHorizontal, this.initialGrabX, endX);
            this.grabBoneY = (float) Mth.lerp(easedPull, this.initialGrabY, endY) + verticalArc;
            this.grabBoneZ = (float) Mth.lerp(easedHorizontal, this.initialGrabZ, endZ);

            target.setPos(this.grabBoneX, this.grabBoneY, this.grabBoneZ);
            target.hurtMarked = true;
        }
    }

    private void handleClientSummonEffects() {
        this.level().playSound(null, this.blockPosition(), SoundEvents.WARDEN_EMERGE, SoundSource.HOSTILE, 1.0f, 1.5f);
        executeScreamParticles();
    }

    private void hadleClientTickEffects(){
        if (this.tickCount % 20 == 0 && random.nextInt(3) == 0) {
            this.level().playSound(null, this.blockPosition(), SoundEvents.SCULK_CLICKING, SoundSource.HOSTILE, 0.5f, 0.5f);
        }
    }

    private void handleClientGrabEffects() {
        double spawnX = this.getX();
        double spawnY = this.getY() + UnknownPortalBlockEntity.Y_OFFSET;
        double spawnZ = this.getZ();

        this.level().playLocalSound(spawnX, spawnY, spawnZ,
                ModSounds.GRAWL_DEATH.get(), this.getSoundSource(), 1.5F, 0.8F + this.random.nextFloat() * 0.4F, false);

        for (int i = 0; i < 40; i++) {
            double offsetX = (this.random.nextDouble() - 0.5) * 1.5;
            double offsetY = this.random.nextDouble() * -2.0;
            double offsetZ = (this.random.nextDouble() - 0.5) * 1.5;

            double velocityY = -0.05 - (this.random.nextDouble() * 0.1);

            ChillFallingParticleOptions particleData = new ChillFallingParticleOptions(new Vector3f(1,0.9f,0), 0.03f, 30, 10);

            this.level().addParticle(particleData,
                    spawnX + offsetX,
                    spawnY + offsetY,
                    spawnZ + offsetZ,
                    0.0D, velocityY, 0.0D);

        }
    }

    private void findAndSetRandomTarget() {
        AABB searchArea = this.getBoundingBox().inflate(MAX_TARGET_RANGE);

        List<LivingEntity> potentialTargets = this.level().getEntitiesOfClass(LivingEntity.class, searchArea,
                entity -> {
                    if (!entity.isAlive() || entity.is(this)) return false;

                    if (entity instanceof Player player) {
                        if (player.isCreative() || player.isSpectator()) return false;
                    }

                    return true;
                });

        if (!potentialTargets.isEmpty()) {
            LivingEntity randomTarget = potentialTargets.get(this.random.nextInt(potentialTargets.size()));
            setTarget(randomTarget);
        } else {
            setTarget(null);
        }
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
    public AnimatableInstanceCache getAnimatableInstanceCache() { return this.cache; }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        this.setTentacleState(tag.getInt("TentacleState"));
        if (tag.contains("PortalPos")) {
            this.portalPos = NbtUtils.readBlockPos(tag.getCompound("PortalPos"));
        }
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        tag.putInt("TentacleState", this.getTentacleState());
        if (this.portalPos != null) {
            tag.put("PortalPos", NbtUtils.writeBlockPos(this.portalPos));
        }
    }
}