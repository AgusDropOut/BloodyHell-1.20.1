package net.agusdropout.bloodyhell.item.custom.base;

import net.agusdropout.bloodyhell.BloodyHell;
import net.agusdropout.bloodyhell.client.render.item.generic.GenericSpellBookRenderer;
import net.agusdropout.bloodyhell.item.custom.spellbooks.OmnitomeItem;
import net.agusdropout.bloodyhell.util.capability.CrimsonVeilHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animatable.SingletonGeoAnimatable;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.List;
import java.util.function.Consumer;

public abstract class BaseSpellBookItem<T extends BaseSpellBookItem<T>> extends Item implements GeoItem {

    private static final RawAnimation OPEN_ANIM = RawAnimation.begin().thenLoop("open");
    private static final RawAnimation CLOSED_ANIM = RawAnimation.begin().thenLoop("closed");
    private static final RawAnimation ATTACK_ANIM = RawAnimation.begin().thenPlay("attack");
    private static final List<String> gem_sockets = List.of("socket_1", "socket_2", "socket_3");
    private static final RawAnimation ATTACK_SEQ_ANIM = RawAnimation.begin().thenLoop("attack_seq");

    private static final String NBT_OPEN_KEY = "spellbook_is_open";

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    private final SpellBookDefinition definition;

    public BaseSpellBookItem(Properties properties, SpellBookDefinition definition) {
        super(properties);
        this.definition = definition;
        SingletonGeoAnimatable.registerSyncedAnimatable(this);
    }

    public SpellBookDefinition getDefinition() {
        return definition;
    }

    public CompoundTag serializeBookData(ItemStack bookStack) {
        CompoundTag serialized = new CompoundTag();
        bookStack.save(serialized);
        return serialized;
    }

    public static ItemStack deserializeBookData(CompoundTag nbt) {
        if (nbt == null || nbt.isEmpty()) {
            return ItemStack.EMPTY;
        }
        return ItemStack.of(nbt);
    }

    @Override
    public boolean isPerspectiveAware() {
        return true;
    }

    @Override
    public void onUseTick(Level level, LivingEntity livingEntity, ItemStack stack, int remainingUseDuration) {
        if (livingEntity instanceof Player player) {
            int ticksUsed = getUseDuration(stack) - remainingUseDuration;


            if (getStaffCastType() == StaffCastType.SEQUENTIAL && ticksUsed > 0) {
                int chargeTime = Math.max(1, getMinChargeTime());
                int cooldown = Math.max(0, getCooldown());
                int cycleLength = chargeTime + cooldown;


                if (ticksUsed % cycleLength == chargeTime % cycleLength) {
                    executeCast(level, player, player.getUsedItemHand(), stack);
                }
            }

            if (level.isClientSide) {
                spawnProgressiveParticles(level, player, ticksUsed);
            }
            playChargeSound(level, player, ticksUsed);
        }
    }

    public void releaseUsing(ItemStack stack, Level level, LivingEntity entity, int timeLeft, InteractionHand hand) {
        if (entity instanceof Player player) {
            int duration = getUseDuration(stack) - timeLeft;

            if (duration >= getMinChargeTime()) {

                if (getStaffCastType() != StaffCastType.SEQUENTIAL) {
                    if (!level.isClientSide) {
                        if (CrimsonVeilHelper.consume(player, getCrimsonCost())) {
                            performSpell(level, player, hand, stack);
                            triggerAnim(player, GeoItem.getOrAssignId(stack, (ServerLevel) level), "controller", "attack");
                            player.getCooldowns().addCooldown(this, getCooldown());
                        } else {
                            player.displayClientMessage(Component.translatable("message.bloodyhell.not_enough_veil")
                                    .withStyle(ChatFormatting.DARK_RED), true);
                        }
                    } else {
                        performSpell(level, player, hand, stack);
                    }
                }
            }
        }
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "controller", 5, state -> {
            ItemDisplayContext context = state.getData(DataTickets.ITEM_RENDER_PERSPECTIVE);

            if (context != ItemDisplayContext.FIRST_PERSON_RIGHT_HAND &&
                    context != ItemDisplayContext.FIRST_PERSON_LEFT_HAND &&
                    context != ItemDisplayContext.THIRD_PERSON_RIGHT_HAND &&
                    context != ItemDisplayContext.THIRD_PERSON_LEFT_HAND) {
                state.getController().setAnimationSpeed(1.0f);
                return state.setAndContinue(CLOSED_ANIM);
            }

            Entity entity = state.getData(DataTickets.ENTITY);
            if (entity == null) {
                entity = net.agusdropout.bloodyhell.client.ClientItemHooks.getLocalPlayerAsEntity();
            }


            if (entity instanceof LivingEntity living && living.isUsingItem()) {
                Item usedItem = living.getUseItem().getItem();
                if (usedItem instanceof BaseStaffItem || usedItem instanceof BaseSpellBookItem<?> || usedItem instanceof OmnitomeItem) {
                    if (getStaffCastType() == StaffCastType.SEQUENTIAL) {
                        int cycleLength = Math.max(1, getMinChargeTime()) + Math.max(0, getCooldown());
                        state.getController().setAnimationSpeed(10.0f / (float) cycleLength);
                        return state.setAndContinue(ATTACK_SEQ_ANIM);
                    }
                }
            }

            boolean isAttacking = state.getController().getCurrentAnimation() != null &&
                    state.getController().getCurrentAnimation().animation().name().equals("attack");

            if (isAttacking) {
                float speed = 1.0f;
                if (getStaffCastType() == StaffCastType.THROW || getStaffCastType() == StaffCastType.RISE) {
                    speed = 10.0f / (float)Math.max(1, getMinChargeTime());
                }
                state.getController().setAnimationSpeed(Math.min(speed, 8.0f));

                if (!state.getController().hasAnimationFinished()) {
                    return software.bernie.geckolib.core.object.PlayState.CONTINUE;
                }
            } else {
                state.getController().setAnimationSpeed(1.0f);
            }

            ItemStack stack = state.getData(DataTickets.ITEMSTACK);
            boolean isOpen = stack != null && stack.hasTag() && stack.getTag().getBoolean(NBT_OPEN_KEY);

            return state.setAndContinue(isOpen ? OPEN_ANIM : CLOSED_ANIM);

        }).triggerableAnim("attack", ATTACK_ANIM));
    }

    public boolean canStartCasting(Level level, Player player) {
        if (player.getCooldowns().isOnCooldown(this)) {
            return false;
        }

        if (!CrimsonVeilHelper.hasEnough(player, getCrimsonCost())) {
            level.playSound(null, player.getX(), player.getY(), player.getZ(),
                    SoundEvents.BEACON_DEACTIVATE, SoundSource.PLAYERS, 1.0f, 2.0f);

            if (!level.isClientSide) {
                player.displayClientMessage(Component.translatable("message.bloodyhell.not_enough_veil")
                        .withStyle(ChatFormatting.DARK_RED), true);
            }
            return false;
        }

        return true;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);

        if (!canStartCasting(level, player)) {
            return InteractionResultHolder.fail(itemstack);
        }

        player.startUsingItem(hand);
        return InteractionResultHolder.consume(itemstack);
    }

    public void executeCast(Level level, Player player, InteractionHand hand, ItemStack stack) {
        if (!level.isClientSide) {
            if (CrimsonVeilHelper.consume(player, getCrimsonCost())) {
                performSpell(level, player, hand, stack);


                if (getStaffCastType() != StaffCastType.SEQUENTIAL) {
                    triggerAnim(player, GeoItem.getOrAssignId(stack, (ServerLevel) level), "controller", "attack");
                }

                if (getCooldown() > 0 && getStaffCastType() != StaffCastType.SEQUENTIAL) {
                    player.getCooldowns().addCooldown(this, getCooldown());
                }
            } else {
                player.displayClientMessage(Component.translatable("message.bloodyhell.not_enough_veil").withStyle(ChatFormatting.DARK_RED), true);
                player.stopUsingItem();
            }
        } else {
            performSpell(level, player, hand, stack);
        }
    }


    @Override
    public void inventoryTick(ItemStack itemStack, Level level, Entity entity, int slot, boolean isSelected) {
        super.inventoryTick(itemStack, level, entity, slot, isSelected);
        if (entity instanceof Player player) {
            boolean isHeld = player.getItemInHand(InteractionHand.MAIN_HAND) == itemStack || player.getItemInHand(InteractionHand.OFF_HAND) == itemStack;
            CompoundTag tag = itemStack.getOrCreateTag();
            if (tag.getBoolean(NBT_OPEN_KEY) != isHeld) tag.putBoolean(NBT_OPEN_KEY, isHeld);
        }
    }

    @Override
    public boolean onDroppedByPlayer(ItemStack item, Player player) {
        if (item.hasTag()) item.getTag().putBoolean(NBT_OPEN_KEY, false);
        return super.onDroppedByPlayer(item, player);
    }

    public int getCrimsonCost() { return definition.crimsonCost(); }
    @Override public UseAnim getUseAnimation(ItemStack stack) { return UseAnim.BOW; }
    @Override public int getUseDuration(ItemStack stack) { return 72000; }
    @Override public AnimatableInstanceCache getAnimatableInstanceCache() { return this.cache; }

    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(new IClientItemExtensions() {
            private BlockEntityWithoutLevelRenderer renderer;
            @Override
            public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                if (this.renderer == null) this.renderer = new GenericSpellBookRenderer();
                return this.renderer;
            }
        });
    }

    @Override
    public void appendHoverText(ItemStack itemStack, @Nullable Level level, List<Component> toolTip, TooltipFlag flag) {
        super.appendHoverText(itemStack, level, toolTip, flag);
        List<Gem> gems = GemType.getGemsFromWeapon(itemStack);
        if (!gems.isEmpty()) {
            toolTip.add(Component.literal("Socketed Gems:").withStyle(ChatFormatting.GRAY));
            for (Gem gem : gems) {
                String bonusType = gem.getStat();
                double value = gem.getValue();
                toolTip.add(Component.literal(" " + GemType.getFormattedBonus(bonusType, value)).withStyle(GemType.getChatFormating(bonusType)));
            }
        } else {
            toolTip.add(Component.literal("No Gems Socketed").withStyle(ChatFormatting.DARK_GRAY));
        }
    }

    protected List<Gem> getGemsFromItemStack(ItemStack itemStack) { return GemType.getGemsFromWeapon(itemStack); }
    protected int getProjectileAdditionalFromGems(List<Gem> gems) {
        int additionalProjectiles = 0;
        for (Gem gem : gems) {
            if (gem.getStat().equals(GemType.TYPE_QUANTITY)) additionalProjectiles += (int) gem.getValue();
        }
        return additionalProjectiles;
    }

    public abstract void performSpell(Level level, Player player, InteractionHand hand, ItemStack itemStack);
    public abstract void spawnProgressiveParticles(Level level, Player player, int chargeTick);
    public abstract void playChargeSound(Level level, Player player, int chargeTick);
    public int getMinChargeTime() { return definition.chargeTicks(); }
    public int getCooldown() { return definition.cooldownTicks(); }
    public String getSpellBookId() { return definition.id(); }
    public StaffCastType getStaffCastType() { return definition.castType(); }
    public SpellAffinity getAffinity() { return definition.affinity(); }
    public String getSpellIconName() { return definition.icon(); }
    public ResourceLocation getSpellIconLocation() {
        return new ResourceLocation(BloodyHell.MODID, "textures/gui/" + getSpellIconName() + ".png");
    }

    public String getAnimationFileName() {
        return "generic_spellbook";
    }
}