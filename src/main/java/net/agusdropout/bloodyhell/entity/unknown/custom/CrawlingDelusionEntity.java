package net.agusdropout.bloodyhell.entity.unknown.custom;

import net.agusdropout.bloodyhell.entity.base.AbstractInsightMonster;
import net.agusdropout.bloodyhell.util.visuals.ParticleHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

public class CrawlingDelusionEntity extends AbstractInsightMonster implements GeoEntity {

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    private static final EntityDataAccessor<Integer> STATE = SynchedEntityData.defineId(CrawlingDelusionEntity.class, EntityDataSerializers.INT);

    private static final int STATE_NORMAL = 0;
    private static final int STATE_UNBURROWING = 1;
    private static final int STATE_EXPLODING = 2;

    private static final RawAnimation ANIM_IDLE = RawAnimation.begin().thenLoop("idle");
    private static final RawAnimation ANIM_WALKING = RawAnimation.begin().thenLoop("walking");
    private static final RawAnimation ANIM_UNBURROWING = RawAnimation.begin().thenPlay("unburrowing");
    private static final RawAnimation ANIM_EXPLODE = RawAnimation.begin().thenPlay("explode");

    private int stateTicks = 0;
    private int deathCooldown = 20;

    public CrawlingDelusionEntity(EntityType<? extends Monster> entityType, Level level) {
        super(entityType, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 30.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.15D)
                .add(Attributes.ATTACK_DAMAGE, 5.0D);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(STATE, STATE_UNBURROWING);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new FloatGoal(this));
        this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.2D, false));
        this.goalSelector.addGoal(3, new WaterAvoidingRandomStrollGoal(this, 1.0D));
        this.goalSelector.addGoal(4, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(5, new RandomLookAroundGoal(this));

        this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, Player.class, true));
    }

    private int getEntityState() {
        return this.entityData.get(STATE);
    }

    private void setEntityState(int state) {
        this.entityData.set(STATE, state);
    }

    @Override
    public float getMinimumInsight() {
        return 10.0F;
    }

    @Override
    public void aiStep() {
        super.aiStep();

        if (this.getEntityState() != STATE_NORMAL) {
            this.getNavigation().stop();
            this.setTarget(null);
            this.setDeltaMovement(0, this.getDeltaMovement().y, 0);
        }
    }

    @Override
    public void tick() {
        super.tick();

        int currentState = this.getEntityState();

        if (currentState == STATE_UNBURROWING) {
            BlockPos posBelow = this.blockPosition().below();
            BlockState stateBelow = this.level().getBlockState(posBelow);

            if (this.level().isClientSide()) {
                if (!stateBelow.isAir()) {
                    /* Constant dirt disturbance during the unburrowing sequence */
                    ParticleHelper.spawnRisingBurst(
                            this.level(),
                            new BlockParticleOption(ParticleTypes.BLOCK, stateBelow),
                            this.position(),
                            4, 0.8D, 0.15D, 0.2D
                    );
                }
            } else {
                if (this.tickCount % 5 == 0 && !stateBelow.isAir()) {
                    SoundType soundType = stateBelow.getSoundType(this.level(), posBelow, this);
                    this.playSound(soundType.getBreakSound(), soundType.getVolume() * 0.5F, soundType.getPitch() * 0.8F);
                }
            }

            this.stateTicks++;
            if (this.stateTicks >= 40) {
                if (!this.level().isClientSide()) {
                    this.setEntityState(STATE_NORMAL);
                }
            }
        }
    }

    @Override
    protected void tickDeath() {
        if (this.getEntityState() != STATE_EXPLODING) {
            this.setEntityState(STATE_EXPLODING);
        }

        this.hurtTime = 0;
        this.deathCooldown--;

        if (this.deathCooldown <= 0) {
            if (!this.level().isClientSide()) {
                this.remove(Entity.RemovalReason.KILLED);
            }
        }
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        AnimationController<CrawlingDelusionEntity> controller = new AnimationController<>(this, "controller", 5, event -> {
            int state = this.getEntityState();

            if (state == STATE_EXPLODING) {
                return event.setAndContinue(ANIM_EXPLODE);
            } else if (state == STATE_UNBURROWING) {
                return event.setAndContinue(ANIM_UNBURROWING);
            } else if (event.isMoving()) {
                return event.setAndContinue(ANIM_WALKING);
            } else {
                return event.setAndContinue(ANIM_IDLE);
            }
        });

        controller.setCustomInstructionKeyframeHandler(event -> {
            if (event.getKeyframeData().getInstructions().equals("bodyImpact;")) {
                this.handleBodyImpactInstruction();
            }
        });

        controllers.add(controller);
    }

    private void handleBodyImpactInstruction() {
        if (this.level().isClientSide() && this.hasSufficientClientInsight()) {
            BlockPos posBelow = this.blockPosition().below();
            BlockState stateBelow = this.level().getBlockState(posBelow);

            if (!stateBelow.isAir()) {
                SoundType soundType = stateBelow.getSoundType(this.level(), posBelow, this);
                this.level().playLocalSound(this.getX(), this.getY(), this.getZ(),
                        soundType.getHitSound(), this.getSoundSource(),
                        soundType.getVolume() * 0.8F, soundType.getPitch(), false);

                /* Heavy impact combining outward ring debris and an upward blast */
                ParticleHelper.spawnCrownSplash(
                        this.level(),
                        new BlockParticleOption(ParticleTypes.BLOCK, stateBelow),
                        this.position(),
                        20, 0.5D, 0.15D, 0.3D
                );

                ParticleHelper.spawnHemisphereExplosion(
                        this.level(),
                        new BlockParticleOption(ParticleTypes.BLOCK, stateBelow),
                        this.position(),
                        15, 0.3D, 0.25D
                );
            }
        }
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }
}