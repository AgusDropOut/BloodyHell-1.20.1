package net.agusdropout.bloodyhell.entity.custom;

import net.agusdropout.bloodyhell.entity.ai.goals.VesperAttackGoal;
import net.agusdropout.bloodyhell.item.ModItems;
import net.agusdropout.bloodyhell.screen.custom.menu.VesperMenu;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.TimeUtil;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.*;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.ResetUniversalAngerTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;

public class VesperEntity extends PathfinderMob implements MenuProvider,NeutralMob {
    private final ItemStackHandler lazyItemHandler = new ItemStackHandler(2);
    private int currentInteractionCount;
    private static final UniformInt ANGER_TIME_RANGE = TimeUtil.rangeOfSeconds(20, 39);
    private int angerTime;
    private UUID targetUuid;
    private UUID clientUuid;
    public AnimationState attackAnimationState = new AnimationState();
    public AnimationState idleAnimationState = new AnimationState();
    private int idleAnimationTimeout = 0;
    public int attackAnimationTimeout = 0;
    private static final EntityDataAccessor<Boolean> ATTACKING =
            SynchedEntityData.defineId(VesperEntity.class, EntityDataSerializers.BOOLEAN);


    public VesperEntity(EntityType<? extends PathfinderMob> p_35267_, Level p_35268_) {
        super(p_35267_, p_35268_);
        currentInteractionCount = 0;
    }

    public static AttributeSupplier setAttributes() {
        return Monster.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 150)
                .add(Attributes.ATTACK_SPEED, 1f)
                .add(Attributes.ATTACK_DAMAGE, 10.0f)
                .add(Attributes.MOVEMENT_SPEED, 0.25f).build();
    }
    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(2, new WaterAvoidingRandomStrollGoal(this, 0.3D));
        this.goalSelector.addGoal(10, new LookAtPlayerGoal(this, LivingEntity.class, 32.0F));
        this.goalSelector.addGoal(6, new RandomLookAroundGoal(this));
        this.goalSelector.addGoal(1, new VesperAttackGoal(this, 1.0D, true));
        this.targetSelector.addGoal(1, (new HurtByTargetGoal(this)).setAlertOthers());
        this.targetSelector.addGoal(2, new ResetUniversalAngerTargetGoal<>(this, true));
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        if(currentInteractionCount == -1) {
            if (!this.level().isClientSide && player instanceof ServerPlayer serverPlayer) {
                NetworkHooks.openScreen(serverPlayer, this);
            }
            return InteractionResult.sidedSuccess(this.level().isClientSide);
        } else {
            player.displayClientMessage(Component.literal(getCurrentVesperSpeech(currentInteractionCount)).withStyle(ChatFormatting.RED), true);
        }
        nextVesperSpeech();
        return InteractionResult.sidedSuccess(this.level().isClientSide);

    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int i, Inventory inventory, Player player) {
        return new VesperMenu(i, inventory, lazyItemHandler, this);
    }

    @Override
    public Component getDisplayName() {
        return Component.literal("Vesper");
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (isItemQuantityValid(Items.BONE,10,0) && isItemQuantityValid(Items.ENDER_PEARL,1,1)) {
            double range = 5.0D;
            AABB effectArea = (new AABB(this.getOnPos())).inflate(range);
            List<Player> players = this.level().getEntitiesOfClass(Player.class, effectArea);
            for (Player player : players) { // Iterate over the players
                lazyItemHandler.getStackInSlot(0).shrink(10);
                lazyItemHandler.getStackInSlot(1).shrink(1);
                player.getInventory().add(ModItems.CHALICE_OF_THE_DAMMED.get().getDefaultInstance());
            }

        }
    }

    public String getCurrentVesperSpeech(int i) {
        if (i == 0) {
            return "Ah...you’ve arrived. Welcome, traveler, to the realm of blood. Once forever lost, now found";
        } else if (i == 1) {
            return "To enter the Blood Dimension, you must bring me a tribute: 10 bones and 1 ender pearl";
        } else if (i == 2) {
            return "Only then will you unlock its secrets, and discover the power that lies within";
        } else if (i==3) {
            return "Now go, and return quickly... the unknown is always watching";
        } else {
            return "";
        }
    }

    public void nextVesperSpeech() {
        if(currentInteractionCount != -1) {
            currentInteractionCount++;
            if (currentInteractionCount > 3) {
                currentInteractionCount = -1;
            }
        }
    }

    @Override
    public int getRemainingPersistentAngerTime() {
        return this.angerTime;
    }

    @Override
    public void setRemainingPersistentAngerTime(int i) {
        this.angerTime = i;
    }

    @Nullable
    @Override
    public UUID getPersistentAngerTarget() {
         return this.targetUuid;
    }

    @Override
    public void setPersistentAngerTarget(@Nullable UUID uuid) {
        this.setRemainingPersistentAngerTime(ANGER_TIME_RANGE.sample(this.random));
        this.targetUuid = uuid;
    }

    @Override
    public void startPersistentAngerTimer() {
        this.setRemainingPersistentAngerTime(ANGER_TIME_RANGE.sample(this.random));
    }

    @Override
    public void tick() {
        super.tick();
        if(this.level().isClientSide()) {
            setupAnimationStates();
        }
    }
    private void setupAnimationStates() {
        if(this.idleAnimationTimeout <= 0) {
            this.idleAnimationTimeout = this.random.nextInt(40) + 80;
            this.idleAnimationState.start(this.tickCount);
        } else {
            --this.idleAnimationTimeout;
        }

        if(this.isAttacking() && attackAnimationTimeout <= 0) {
            System.out.println("Attacking");
            attackAnimationTimeout = 45; // Length in ticks of your animation
            attackAnimationState.start(this.tickCount);
        } else {
            --this.attackAnimationTimeout;
        }

        if(!this.isAttacking()) {
            attackAnimationState.stop();
        }
    }
    @Override
    protected void updateWalkAnimation(float pPartialTick) {
        float f;
        if(this.getPose() == Pose.STANDING) {
            f = Math.min(pPartialTick * 6F, 1f);
        } else {
            f = 0f;
        }

        this.walkAnimation.update(f, 0.2f);
    }

    public void setAttacking(boolean attacking) {
        this.entityData.set(ATTACKING, attacking);
    }

    public boolean isAttacking() {
        return this.entityData.get(ATTACKING);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(ATTACKING, false);
    }

    @Override
    protected boolean shouldDespawnInPeaceful() {
        return false;
    }
    public boolean isItemQuantityValid(Item item, int quantity, int slot) {
        return lazyItemHandler.getStackInSlot(slot).getCount() >= quantity && lazyItemHandler.getStackInSlot(slot).getItem().equals(item);
    }

}



