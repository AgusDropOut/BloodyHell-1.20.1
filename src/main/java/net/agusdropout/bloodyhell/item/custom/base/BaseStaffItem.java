package net.agusdropout.bloodyhell.item.custom.base;

import net.agusdropout.bloodyhell.item.client.base.BaseStaffRenderer;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animatable.SingletonGeoAnimatable;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.*;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;
import software.bernie.geckolib.util.RenderUtils;

import java.util.function.Consumer;

public abstract class BaseStaffItem extends Item implements GeoItem {

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    protected static final RawAnimation IDLE_ANIM = RawAnimation.begin().thenLoop("idle");
    protected static final RawAnimation THROW_CASTING_ANIM = RawAnimation.begin().thenLoop("throw_casting");
    protected static final RawAnimation CIRCLE_CASTING_ANIM = RawAnimation.begin().thenLoop("circle_casting");
    protected static final RawAnimation HOLD_CASTING_ANIM = RawAnimation.begin().thenLoop("hold_casting");
    protected static final RawAnimation RISE_CASTING_ANIM = RawAnimation.begin().thenPlay("rise_casting").thenLoop("rise_casting_hold");

    protected static final RawAnimation SEQ_1_ANIM = RawAnimation.begin().thenLoop("seq_casting_1");
    protected static final RawAnimation SEQ_2_ANIM = RawAnimation.begin().thenLoop("seq_casting_2");

    public BaseStaffItem(Properties properties) {
        super(properties);
        SingletonGeoAnimatable.registerSyncedAnimatable(this);
    }

    @Override
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
        return slotChanged || oldStack.getItem() != newStack.getItem();
    }

    @Override
    public net.minecraft.world.item.UseAnim getUseAnimation(ItemStack stack) {
        return net.minecraft.world.item.UseAnim.NONE;
    }

    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(new IClientItemExtensions() {
            private BaseStaffRenderer renderer;
            @Override
            public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                if (this.renderer == null) this.renderer = new BaseStaffRenderer();
                return this.renderer;
            }
        });
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "base_controller", 2, this::basePredicate));


        controllers.add(new AnimationController<>(this, "action_controller", 2, state -> PlayState.STOP)
                .triggerableAnim("inspect_1", RawAnimation.begin().thenPlay("inspect_1"))
                .triggerableAnim("inspect_2", RawAnimation.begin().thenPlay("inspect_2"))
                .triggerableAnim("inspect_3", RawAnimation.begin().thenPlay("inspect_3"))
                .triggerableAnim("rise_finish", RawAnimation.begin().thenPlay("rise_casting_finish")));
    }

    protected <T extends GeoAnimatable> PlayState basePredicate(AnimationState<T> event) {
        Entity entity = event.getData(DataTickets.ENTITY);
        if (entity == null) entity = net.minecraft.client.Minecraft.getInstance().player;

        if (entity instanceof LivingEntity livingEntity && livingEntity.isUsingItem() && livingEntity.getUseItem().getItem() == this) {
            ItemStack stack = livingEntity.getUseItem();
            ItemStack offhandStack = livingEntity.getItemInHand(InteractionHand.OFF_HAND);

            String castTypeStr = stack.getOrCreateTag().getString("CastType");
            StaffCastType castType = StaffCastType.HOLD;
            try { castType = StaffCastType.valueOf(castTypeStr); } catch (IllegalArgumentException ignored) {}

            float animSpeed = 1.0f;
            int chargeTime = 20;
            int cooldown = 0;

            if (offhandStack.getItem() instanceof BaseSpellBookItem spellBook) {
                chargeTime = Math.max(1, spellBook.getMinChargeTime());
                cooldown = Math.max(0, spellBook.getCooldown());
            }

            switch (castType) {
                case THROW:
                    animSpeed = castType.getBaseTicks() / (float) chargeTime;
                    event.getController().setAnimationSpeed(animSpeed);
                    event.getController().setAnimation(THROW_CASTING_ANIM);
                    break;
                case CIRCLE:
                    event.getController().setAnimationSpeed(1.0f);
                    event.getController().setAnimation(CIRCLE_CASTING_ANIM);
                    break;
                case RISE:
                    animSpeed = castType.getBaseTicks() / (float) chargeTime;
                    event.getController().setAnimationSpeed(animSpeed);
                    event.getController().setAnimation(RISE_CASTING_ANIM);
                    break;
                case HOLD:
                    event.getController().setAnimationSpeed(1.0f);
                    event.getController().setAnimation(HOLD_CASTING_ANIM);
                    break;
                case SEQUENTIAL:
                    int cycleLength = chargeTime + cooldown;
                    animSpeed = castType.getBaseTicks() / (float) cycleLength;
                    event.getController().setAnimationSpeed(animSpeed);

                    int ticksUsed = stack.getUseDuration() - livingEntity.getUseItemRemainingTicks();

                    if ((ticksUsed / cycleLength) % 2 == 0) {
                        event.getController().setAnimation(SEQ_1_ANIM);
                    } else {
                        event.getController().setAnimation(SEQ_2_ANIM);
                    }
                    break;
            }
        } else {
            event.getController().setAnimationSpeed(1.0f);
            event.getController().setAnimation(IDLE_ANIM);
        }

        return PlayState.CONTINUE;
    }

    @Override
    public int getUseDuration(ItemStack stack) {
        return 72000;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack itemStack = player.getItemInHand(hand);
        ItemStack offhandStack = player.getItemInHand(InteractionHand.OFF_HAND);

        if (offhandStack.getItem() instanceof BaseSpellBookItem spellBook) {
            if (!spellBook.canStartCasting(level, player)) {
                return InteractionResultHolder.fail(itemStack);
            }

            if (!level.isClientSide) {
                itemStack.getOrCreateTag().putString("CastType", spellBook.getStaffCastType().name());
            }

            player.startUsingItem(hand);
            return InteractionResultHolder.consume(itemStack);
        }
        return InteractionResultHolder.pass(itemStack);
    }

    @Override
    public void onUseTick(Level level, LivingEntity livingEntity, ItemStack stack, int remainingUseDuration) {
        if (livingEntity instanceof Player player) {
            ItemStack offhandStack = player.getItemInHand(InteractionHand.OFF_HAND);

            if (offhandStack.getItem() instanceof BaseSpellBookItem spellBook) {
                spellBook.onUseTick(level, player, offhandStack, remainingUseDuration);

                int ticksUsed = getUseDuration(stack) - remainingUseDuration;
                int chargeTime = Math.max(1, spellBook.getMinChargeTime());
                int cooldown = Math.max(0, spellBook.getCooldown());
                int cycleLength = chargeTime + cooldown;
                StaffCastType castType = spellBook.getStaffCastType();

                if (ticksUsed > 0) {
                    if (castType == StaffCastType.SEQUENTIAL) {
                        int currentCycleTick = ticksUsed % cycleLength;
                        if (currentCycleTick == chargeTime) {
                            spellBook.executeCast(level, player, InteractionHand.MAIN_HAND, offhandStack);
                        }
                    }
                    else if (castType == StaffCastType.THROW && ticksUsed == chargeTime) {
                        spellBook.executeCast(level, player, InteractionHand.MAIN_HAND, offhandStack);
                        player.stopUsingItem();
                    }
                }
            }
        }
        super.onUseTick(level, livingEntity, stack, remainingUseDuration);
    }

    @Override
    public void releaseUsing(ItemStack stack, Level level, LivingEntity entity, int timeLeft) {
        if (entity instanceof Player player) {
            ItemStack offhandStack = player.getItemInHand(InteractionHand.OFF_HAND);

            if (offhandStack.getItem() instanceof BaseSpellBookItem spellBook) {
                StaffCastType castType = spellBook.getStaffCastType();
                if (castType != StaffCastType.SEQUENTIAL && castType != StaffCastType.THROW) {
                    spellBook.releaseUsing(offhandStack, level, player, timeLeft);
                }
            }

            if (!level.isClientSide) {
                String castTypeStr = stack.getOrCreateTag().getString("CastType");
                if (StaffCastType.RISE.name().equals(castTypeStr)) {
                    triggerAnim(player, GeoItem.getId(stack), "action_controller", "rise_finish");
                }
            }
        }
        super.releaseUsing(stack, level, entity, timeLeft);
    }

    public void triggerInspect(Player player, ItemStack stack) {

        int randomInspect = player.level().random.nextInt(3) + 1;
        triggerAnim(player, GeoItem.getId(stack), "action_controller", "inspect_" + randomInspect);
    }

    @Override public AnimatableInstanceCache getAnimatableInstanceCache() { return cache; }
    @Override public double getTick(Object itemStack) { return RenderUtils.getCurrentTick(); }
    public abstract String getId();
}