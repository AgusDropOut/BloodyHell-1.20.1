package net.agusdropout.bloodyhell.entity.custom;

import net.agusdropout.bloodyhell.entity.ModEntityTypes;
import net.agusdropout.bloodyhell.entity.ai.goals.LanternPersecutionGoal;
import net.agusdropout.bloodyhell.entity.base.InsightEntity;
import net.agusdropout.bloodyhell.entity.client.UnknownLanternRenderer;
import net.agusdropout.bloodyhell.entity.effects.EntityCameraShake;
import net.agusdropout.bloodyhell.entity.effects.UnknownLanternRiftEntity;
import net.agusdropout.bloodyhell.particle.ParticleOptions.BlackHoleParticleOptions;
import net.agusdropout.bloodyhell.sound.ModSounds;
import net.agusdropout.bloodyhell.util.capability.InsightHelper;
import net.agusdropout.bloodyhell.util.visuals.ColorHelper;
import net.agusdropout.bloodyhell.util.visuals.SpellPalette;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class UnknownLanternEntity extends Monster implements GeoEntity, InsightEntity {

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private final BlockMemoryManager blockMemoryManager = new BlockMemoryManager();

    private static final EntityDataAccessor<Boolean> IS_SUMMONING = SynchedEntityData.defineId(UnknownLanternEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Optional<UUID>> TARGET_PLAYER = SynchedEntityData.defineId(UnknownLanternEntity.class, EntityDataSerializers.OPTIONAL_UUID);
    private static final EntityDataAccessor<Boolean> IS_GAZING = SynchedEntityData.defineId(UnknownLanternEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Float> GAZE_INTENSITY = SynchedEntityData.defineId(UnknownLanternEntity.class, EntityDataSerializers.FLOAT);

    private static final RawAnimation SUMMON_ANIM = RawAnimation.begin().thenPlay("summon");
    private static final RawAnimation WALK_ANIM = RawAnimation.begin().thenLoop("walk");
    private static final RawAnimation IDLE_ANIM = RawAnimation.begin().thenLoop("idle");

    private int summonTicks = 0;
    private Vec3 playerOriginPosition = null; // Stores where the player was when summoned

    public UnknownLanternEntity(EntityType<? extends Monster> type, Level level) {
        super(type, level);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new FloatGoal(this));
        this.goalSelector.addGoal(2, new LanternPersecutionGoal(this));
        this.goalSelector.addGoal(4, new WaterAvoidingRandomStrollGoal(this, 1.0D));
        this.goalSelector.addGoal(5, new RandomLookAroundGoal(this));
        this.goalSelector.addGoal(5, new LookAtPlayerGoal(this, Player.class, 8.0F));
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 80.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.22D)
                .add(Attributes.ATTACK_DAMAGE, 8.0D)
                .add(Attributes.FOLLOW_RANGE, 32.0D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.8D);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();

        this.entityData.define(IS_SUMMONING, false);
        this.entityData.define(TARGET_PLAYER, Optional.empty());
        this.entityData.define(IS_GAZING, false);
        this.entityData.define(GAZE_INTENSITY, 0.0f);
    }


    public void setSummoning(boolean isSummoning) {
        this.entityData.set(IS_SUMMONING, isSummoning);
    }



    public void setTargetPlayer(UUID uuid) {
        this.entityData.set(TARGET_PLAYER, Optional.ofNullable(uuid));
    }

    public UUID getTargetPlayer() {
        return this.entityData.get(TARGET_PLAYER).orElse(null);
    }

    public BlockMemoryManager getBlockMemoryManager() {
        return this.blockMemoryManager;
    }
    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        if (this.getTargetPlayer() != null) {
            tag.putUUID("TargetPlayer", this.getTargetPlayer());
        }
        if (this.playerOriginPosition != null) {
            tag.putDouble("OriginX", this.playerOriginPosition.x);
            tag.putDouble("OriginY", this.playerOriginPosition.y);
            tag.putDouble("OriginZ", this.playerOriginPosition.z);
        }

        tag.putBoolean("IsSummoning", this.isSummoning());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        if (tag.hasUUID("TargetPlayer")) {
            this.setTargetPlayer(tag.getUUID("TargetPlayer"));
        }
        if (tag.contains("OriginX")) {
            this.playerOriginPosition = new Vec3(
                    tag.getDouble("OriginX"),
                    tag.getDouble("OriginY"),
                    tag.getDouble("OriginZ")
            );
        }


        if (tag.contains("IsSummoning")) {
            this.entityData.set(IS_SUMMONING, tag.getBoolean("IsSummoning"));
        }
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        UUID targetId = this.getTargetPlayer();
        if (targetId != null) {
            Entity attacker = source.getEntity();

            if( attacker != null && targetId.equals(attacker.getUUID())){
                if(attacker instanceof ServerPlayer player) {
                    float currentInsight = InsightHelper.getInsight(player);
                    if (currentInsight < this.getMinimumInsight()) {
                        return false;
                    }
                }
            }

            if (attacker == null || !targetId.equals(attacker.getUUID())) {
                return false;
            }
        }
        return super.hurt(source, amount);
    }

    @Override
    public void remove(RemovalReason reason) {
        this.blockMemoryManager.restoreBlocks(this.level());
        super.remove(reason);
    }

    @Override
    public void tick() {
        super.tick();
        givePlayerEffects();

        if (this.isSummoning()) {
            this.summonTicks++;
            if (!this.level().isClientSide()) {
                this.getNavigation().stop();
                this.setDeltaMovement(0, this.getDeltaMovement().y, 0);

                if (this.summonTicks == 1) {
                    this.playSound(SoundEvents.WARDEN_EMERGE, 2.0F, 0.8F);
                    spawnRift();

                    // Capture the player's starting position instantly
                    if (this.playerOriginPosition == null && this.getTargetPlayer() != null) {
                        Player p = this.level().getPlayerByUUID(this.getTargetPlayer());
                        if (p != null) {
                            this.playerOriginPosition = p.position();
                        }
                    }
                }

                if (this.summonTicks >= 40) {
                    this.entityData.set(IS_SUMMONING, false);
                }
            } else {
                if (summonTicks == 1) {
                    BlackHoleParticleOptions goldBlackHole = new BlackHoleParticleOptions(2.0F, 1.0F, 0.84F, 0.0F, false);
                    this.level().addParticle(goldBlackHole, this.getX(), this.getY() + 0.05, this.getZ(), 0.0, 0.0, 0.0);
                }
                for (int i = 0; i < 2; i++) {
                    double offsetX = (this.random.nextDouble() - 0.5) * 1.5;
                    double offsetZ = (this.random.nextDouble() - 0.5) * 1.5;

                    this.level().addParticle(ParticleTypes.LARGE_SMOKE,
                            this.getX() + offsetX, this.getY(), this.getZ() + offsetZ,
                            0.0, 0.02, 0.0);
                }
            }
        }

        manageCrouching();

        if (!this.level().isClientSide() && this.isAlive()) {
            if (this.tickCount % 40 == 10) {
                this.playSound(ModSounds.UNKNOWN_LANTERN_HEARTBEAT.get(), 0.6F, 1.0F);
            }
        }
    }

    private void givePlayerEffects() {
        if (this.tickCount % 20 == 0 && this.getTargetPlayer() != null) {
            if (this.level() instanceof ServerLevel serverLevel) {
                Entity target = serverLevel.getEntity(this.getTargetPlayer());
                if (target instanceof Player player) {
                    player.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 60, 0, false, false, false));
                    player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 60, 2, false, false, false));
                }
            }
        }
    }

    public boolean isSummoning() {
        return this.entityData.get(IS_SUMMONING);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        AnimationController<UnknownLanternEntity> controller = new AnimationController<>(this, "controller", 5, event -> {
            if (this.isSummoning()) return event.setAndContinue(SUMMON_ANIM);

            if (this.getPose() == Pose.CROUCHING) {
                event.getController().setAnimation(RawAnimation.begin().thenLoop("crouch_walk"));
                return PlayState.CONTINUE;
            }

            if (event.isMoving()) return event.setAndContinue(WALK_ANIM);
            return event.setAndContinue(IDLE_ANIM);
        });

        controller.setCustomInstructionKeyframeHandler(event -> {
            String instruction = event.getKeyframeData().getInstructions();
            if (instruction.equals("rightLegStep;") || instruction.equals("leftLegStep;")) {
                if (this.level().isClientSide()) {
                    boolean isRightLeg = instruction.equals("rightLegStep;");
                    this.level().playLocalSound(this.getX(), this.getY(), this.getZ(),
                            SoundEvents.WARDEN_STEP, this.getSoundSource(), 1.2F, 0.6F, false);
                    triggerBoneParticles(isRightLeg);
                    EntityCameraShake.clientCameraShake(this.level(), this.position(), 5.0f, 0.3f, 10, 3);
                }
            }
        });

        controllers.add(controller);
    }

    @OnlyIn(Dist.CLIENT)
    private void triggerBoneParticles(boolean isRightLeg) {
        var renderer = Minecraft.getInstance().getEntityRenderDispatcher().getRenderer(this);
        if (renderer instanceof UnknownLanternRenderer lanternRenderer) {
            lanternRenderer.spawnRadialStepParticles(this, isRightLeg);
        }
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return this.random.nextBoolean() ? ModSounds.UNKNOWN_LANTERN_AMBIENT_1.get() : ModSounds.UNKNOWN_LANTERN_AMBIENT_2.get();
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }

    private void spawnRift() {
        UnknownLanternRiftEntity rift = ModEntityTypes.UNKNOWN_LANTERN_RIFT.get().create(this.level());
        if (rift != null) {
            rift.moveTo(this.getX(), this.getY(), this.getZ());
            rift.setColor(ColorHelper.vector3fToHex(SpellPalette.RHNULL.getColor(0)));
            rift.setLanternOwner(this.getUUID());
            if (this.getTargetPlayer() != null) {
                rift.setTargetPlayer(this.getTargetPlayer());
            }
            this.level().addFreshEntity(rift);
        }
    }

    public void success(Player player) {
        if (!this.level().isClientSide() && this.level() instanceof ServerLevel serverLevel) {
            if (player instanceof ServerPlayer serverPlayer) {
                InsightHelper.addInsight(serverPlayer, 10);
            }
            this.level().playSound(null, this.blockPosition(), SoundEvents.AMETHYST_CLUSTER_BREAK, this.getSoundSource(), 2.0F, 1.0F);
            this.level().playSound(null, this.blockPosition(), SoundEvents.EVOKER_CAST_SPELL, this.getSoundSource(), 1.5F, 1.2F);
            player.removeEffect(MobEffects.BLINDNESS);
            player.removeEffect(MobEffects.MOVEMENT_SLOWDOWN);
            serverLevel.sendParticles(ParticleTypes.END_ROD,
                    this.getX(), this.getY() + 0.5, this.getZ(),
                    40, 0.5, 0.5, 0.5, 0.1);


            if (this.playerOriginPosition != null) {
                player.teleportTo(this.playerOriginPosition.x, this.playerOriginPosition.y, this.playerOriginPosition.z);
                serverLevel.playSound(null, player.blockPosition(), SoundEvents.ENDERMAN_TELEPORT, this.getSoundSource(), 1.0F, 1.0F);
            }

            this.discard();
        }
    }

    public void fail() {
        if (!this.level().isClientSide() && this.level() instanceof ServerLevel serverLevel) {
            if (this.getTargetPlayer() != null) {
                Entity target = serverLevel.getEntity(this.getTargetPlayer());
                if (target instanceof Player player) {
                    player.removeEffect(MobEffects.BLINDNESS);
                    player.removeEffect(MobEffects.MOVEMENT_SLOWDOWN);

                    if (this.playerOriginPosition != null) {
                        player.teleportTo(this.playerOriginPosition.x, this.playerOriginPosition.y, this.playerOriginPosition.z);
                        serverLevel.playSound(null, player.blockPosition(), SoundEvents.ENDERMAN_TELEPORT, this.getSoundSource(), 1.0F, 0.5F);
                    }
                }
                if (target instanceof ServerPlayer serverPlayer) {
                    InsightHelper.subInsight(serverPlayer, 5);
                }
            }
            this.level().playSound(null, this.blockPosition(), SoundEvents.SCULK_SHRIEKER_SHRIEK, this.getSoundSource(), 2.0F, 0.5F);
            this.level().playSound(null, this.blockPosition(), SoundEvents.WARDEN_SONIC_BOOM, this.getSoundSource(), 1.5F, 0.8F);
            serverLevel.sendParticles(ParticleTypes.LARGE_SMOKE,
                    this.getX(), this.getY() + 0.5, this.getZ(),
                    50, 0.5, 0.5, 0.5, 0.2);
            serverLevel.sendParticles(ParticleTypes.SQUID_INK,
                    this.getX(), this.getY() + 0.5, this.getZ(),
                    50, 0.5, 0.5, 0.5, 0.2);

            this.discard();
        }
    }

    public void setGazing(boolean isGazing) {
        this.entityData.set(IS_GAZING, isGazing);
    }

    public boolean isGazing() {
        return this.entityData.get(IS_GAZING);
    }

    public void setGazeIntensity(float intensity) {
        this.entityData.set(GAZE_INTENSITY, intensity);
    }

    public float getGazeIntensity() {
        return this.entityData.get(GAZE_INTENSITY);
    }

    @Override
    public EntityDimensions getDimensions(Pose pose) {
        if (pose == Pose.CROUCHING) {
            return EntityDimensions.scalable(0.8F, 1.9F);
        }
        return EntityDimensions.scalable(0.8F, 3.0F);
    }

    private void manageCrouching() {
        if (!this.level().isClientSide() && this.isAlive()) {
            Vec3 forward = Vec3.directionFromRotation(0, this.yBodyRot).normalize();
            AABB headBox = this.getBoundingBox().setMinY(this.getY() + 1.9D).setMaxY(this.getY() + 3.0D);
            AABB futureHeadBox = headBox.move(forward.x * 1.0D, 0.0D, forward.z * 1.0D);

            boolean ceilingIsLow = !this.level().noCollision(this, headBox) ||
                    !this.level().noCollision(this, futureHeadBox);

            if (ceilingIsLow && this.getPose() != Pose.CROUCHING) {
                this.setPose(Pose.CROUCHING);
                this.refreshDimensions();
            }
            else if (!ceilingIsLow && this.getPose() == Pose.CROUCHING) {
                this.setPose(Pose.STANDING);
                this.refreshDimensions();
            }
        }
    }

    @Override
    public float getMinimumInsight() {
        return 10;
    }

    public static class BlockMemoryManager {
        private final Map<BlockPos, BlockState> memory = new HashMap<>();

        public void rememberBlock(BlockPos pos, BlockState state) {
            this.memory.putIfAbsent(pos.immutable(), state);
        }

        public void restoreBlocks(Level level) {
            if (level.isClientSide) return;
            for (Map.Entry<BlockPos, BlockState> entry : this.memory.entrySet()) {
                level.setBlock(entry.getKey(), entry.getValue(), 3);
            }
            this.memory.clear();
        }
    }
}