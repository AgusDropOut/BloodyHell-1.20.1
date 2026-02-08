package net.agusdropout.bloodyhell.item.custom.base;

import net.agusdropout.bloodyhell.item.client.generic.GenericSpellBookModel;
import net.agusdropout.bloodyhell.item.client.generic.GenericSpellBookRenderer;
import net.agusdropout.bloodyhell.util.CrimsonVeilHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
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
import software.bernie.geckolib.renderer.GeoItemRenderer;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.List;
import java.util.function.Consumer;

public abstract class BaseSpellBookItem<T extends BaseSpellBookItem<T>> extends Item implements GeoItem {

    private static final RawAnimation OPEN_ANIM = RawAnimation.begin().thenLoop("open");
    private static final RawAnimation CLOSED_ANIM = RawAnimation.begin().thenLoop("closed");
    private static final RawAnimation ATTACK_ANIM = RawAnimation.begin().thenPlay("attack");
    private static final List<String> gem_sockets = List.of("socket_1","socket_2","socket_3");

    private static final String NBT_OPEN_KEY = "spellbook_is_open";

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    public BaseSpellBookItem(Properties properties) {
        super(properties);
        SingletonGeoAnimatable.registerSyncedAnimatable(this);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "controller", 5, state -> {
            ItemDisplayContext context = state.getData(DataTickets.ITEM_RENDER_PERSPECTIVE);

            if (context == ItemDisplayContext.GROUND || context == ItemDisplayContext.FIXED) {
                return state.setAndContinue(CLOSED_ANIM);
            }

            ItemStack stack = state.getData(DataTickets.ITEMSTACK);
            boolean isOpen = stack.hasTag() && stack.getTag().getBoolean(NBT_OPEN_KEY);

            if (isOpen) {
                return state.setAndContinue(OPEN_ANIM);
            } else {
                return state.setAndContinue(CLOSED_ANIM);
            }
        }).triggerableAnim("attack", ATTACK_ANIM));
    }

    @Override
    public void inventoryTick(ItemStack itemStack, Level level, Entity entity, int slot, boolean isSelected) {
        super.inventoryTick(itemStack, level, entity, slot, isSelected);

        if (entity instanceof Player player) {
            boolean isHeld = player.getItemInHand(InteractionHand.MAIN_HAND) == itemStack
                    || player.getItemInHand(InteractionHand.OFF_HAND) == itemStack;

            CompoundTag tag = itemStack.getOrCreateTag();

            if (tag.getBoolean(NBT_OPEN_KEY) != isHeld) {
                tag.putBoolean(NBT_OPEN_KEY, isHeld);
            }
        }
    }

    @Override
    public boolean onDroppedByPlayer(ItemStack item, Player player) {
        if (item.hasTag()) {
            item.getTag().putBoolean(NBT_OPEN_KEY, false);
        }
        return super.onDroppedByPlayer(item, player);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);

        // 1. Check for resource immediately
        if (!CrimsonVeilHelper.hasEnough(player, getCrimsonCost())) {

            // Audio Feedback: Pitch-shifted "Deactivate" sound implies failure
            level.playSound(player, player.getX(), player.getY(), player.getZ(),
                    SoundEvents.BEACON_DEACTIVATE, SoundSource.PLAYERS, 1.0f, 2.0f);

            if (!level.isClientSide) {
                // Visual/Text Feedback: Action Bar Message
                player.displayClientMessage(Component.translatable("message.bloodyhell.not_enough_veil")
                        .withStyle(ChatFormatting.DARK_RED), true);
            }

            // Fail the interaction so no animation plays
            return InteractionResultHolder.fail(itemstack);
        }

        player.startUsingItem(hand);
        return InteractionResultHolder.consume(itemstack);
    }

    @Override
    public void onUseTick(Level level, LivingEntity livingEntity, ItemStack stack, int remainingUseDuration) {
        if (livingEntity instanceof Player player) {
            int tick = getUseDuration(stack) - remainingUseDuration;

            if (level.isClientSide) {
                spawnProgressiveParticles(level, player, tick);
            }

            playChargeSound(level, player, tick);
        }
    }

    @Override
    public void releaseUsing(ItemStack stack, Level level, LivingEntity entity, int timeLeft) {
        if (entity instanceof Player player) {
            int duration = getUseDuration(stack) - timeLeft;

            if (duration >= getMinChargeTime()) {
                if (!level.isClientSide) {
                    // 2. Consume Resource
                    if (CrimsonVeilHelper.consume(player, getCrimsonCost())) {
                        performSpell(level, player, InteractionHand.MAIN_HAND, stack);
                        triggerAnim(player, GeoItem.getOrAssignId(stack, (ServerLevel)level), "controller", "attack");
                        player.getCooldowns().addCooldown(this, getCooldown());
                    } else {
                        // Fallback feedback if they somehow lost resources WHILE charging
                        player.displayClientMessage(Component.translatable("message.bloodyhell.not_enough_veil")
                                .withStyle(ChatFormatting.DARK_RED), true);
                    }
                } else {
                    performSpell(level, player, InteractionHand.MAIN_HAND, stack);
                }
            }
        }
    }

    public abstract int getCrimsonCost();

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.BOW;
    }

    @Override
    public int getUseDuration(ItemStack stack) {
        return 72000;
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() { return this.cache; }

    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(new IClientItemExtensions() {
            private GeoItemRenderer<T> renderer;
            @Override
            public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                if (this.renderer == null) this.renderer = createRenderer();
                return this.renderer;
            }
        });
    }

    public GeoItemRenderer<T> createRenderer() {
        return new GenericSpellBookRenderer<T>(new GenericSpellBookModel<>());
    }

    @Override
    public void appendHoverText(ItemStack itemStack, @Nullable Level level, List<Component> toolTip, TooltipFlag flag) {
        super.appendHoverText(itemStack, level, toolTip, flag);

        // 1. Get the Gems using your new robust method
        List<Gem> gems = GemType.getGemsFromWeapon(itemStack);

        if (!gems.isEmpty()) {
            toolTip.add(Component.literal("Socketed Gems:").withStyle(ChatFormatting.GRAY));

            // 2. Iterate the Gem objects directly
            for (Gem gem : gems) {
                String bonusType = gem.getStat(); // or gem.getStat()
                double value = gem.getValue();

                toolTip.add(Component.literal(" " + GemType.getFormattedBonus(bonusType, value))
                        .withStyle(GemType.getChatFormating(bonusType)));
            }
        } else {
            toolTip.add(Component.literal("No Gems Socketed").withStyle(ChatFormatting.DARK_GRAY));
        }
    }

    protected List<Gem> getGemsFromItemStack(ItemStack itemStack) {
        return GemType.getGemsFromWeapon(itemStack);
    }

    protected int getProjectileAdditionalFromGems(List<Gem> gems) {
        int additionalProjectiles = 0;
        for (Gem gem : gems) {
            if (gem.getStat().equals(GemType.TYPE_QUANTITY)) {
                additionalProjectiles += (int) gem.getValue();
            }
        }
        return additionalProjectiles;
    }



    public abstract void performSpell(Level level, Player player, InteractionHand hand, ItemStack itemStack);



    public abstract void spawnProgressiveParticles(Level level, Player player, int chargeTick);
    public abstract void playChargeSound(Level level, Player player, int chargeTick);
    public abstract int getMinChargeTime();
    public abstract int getCooldown();
    public abstract String getSpellBookId();


}