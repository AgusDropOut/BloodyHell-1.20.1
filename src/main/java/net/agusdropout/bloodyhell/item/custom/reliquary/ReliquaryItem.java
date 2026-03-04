package net.agusdropout.bloodyhell.item.custom.reliquary;


import net.agusdropout.bloodyhell.item.client.ClientItemHooks;
import net.agusdropout.bloodyhell.item.custom.base.BaseGeckoItem;
import net.agusdropout.bloodyhell.screen.custom.menu.ReliquaryMenu;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.network.NetworkHooks;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;

import javax.annotation.Nullable;

public class ReliquaryItem extends BaseGeckoItem {

    private static final RawAnimation IDLE_ANIM = RawAnimation.begin().thenLoop("idle");
    private static final RawAnimation CHARGING_ANIM = RawAnimation.begin().thenLoop("charging");

    public ReliquaryItem(Properties properties) {
        super(properties);
    }

    @Override
    public String getId() {
        return "reliquary";
    }

    @Override
    public int getUseDuration(ItemStack stack) {
        return 72000;
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.BOW;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);

        if (player.isShiftKeyDown()) {
            if (!level.isClientSide && player instanceof ServerPlayer serverPlayer) {
                NetworkHooks.openScreen(serverPlayer, new SimpleMenuProvider(
                        (containerId, playerInventory, p) -> new ReliquaryMenu(containerId, playerInventory, itemstack),
                        Component.literal("Ocular Reliquary")
                ), buf -> buf.writeEnum(hand));
            }
            return InteractionResultHolder.sidedSuccess(itemstack, level.isClientSide());
        }

        player.startUsingItem(hand);

        if (!level.isClientSide) {
            level.playSound(null, player.blockPosition(), SoundEvents.WARDEN_TENDRIL_CLICKS, SoundSource.PLAYERS, 1.0F, 0.5F);
            level.playSound(null, player.blockPosition(), SoundEvents.SOUL_ESCAPE, SoundSource.PLAYERS, 0.8F, 0.5F);
        }

        return InteractionResultHolder.consume(itemstack);
    }

    @Override
    public void onUseTick(Level level, LivingEntity entity, ItemStack stack, int count) {
        if (!level.isClientSide) {
            int durationUsed = getUseDuration(stack) - count;

            if (durationUsed % 15 == 0) {
                float pitch = 0.5F + (durationUsed / 100.0F);
                level.playSound(null, entity.blockPosition(), SoundEvents.WARDEN_HEARTBEAT, SoundSource.PLAYERS, 1.0F, pitch);
                level.playSound(null, entity.blockPosition(), SoundEvents.HONEY_BLOCK_SLIDE, SoundSource.PLAYERS, 0.5F, pitch * 0.8F);
            }
        }
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "controller", 5, state -> {

            ItemStack stack = state.getData(DataTickets.ITEMSTACK);

            if (ClientItemHooks.getLocalPlayer().isUsingItem() && ClientItemHooks.getLocalPlayer().getUseItem() == stack) {
                return state.setAndContinue(CHARGING_ANIM);
            }

            return state.setAndContinue(IDLE_ANIM);
        }));
    }

    public java.util.List<RuneType> getSlottedRunes(ItemStack reliquaryStack) {
        java.util.List<RuneType> activeRunes = new java.util.ArrayList<>();

        reliquaryStack.getCapability(net.minecraftforge.common.capabilities.ForgeCapabilities.ITEM_HANDLER).ifPresent(handler -> {
            for (int i = 0; i < 12; i++) {
                ItemStack slotStack = handler.getStackInSlot(i);
                if (!slotStack.isEmpty()) {
                    RuneType type = RuneType.getByItem(slotStack.getItem());
                    if (type != null) {
                        activeRunes.add(type);
                    }
                }
            }
        });

        return activeRunes;
    }

    @Nullable
    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundTag nbt) {
        return new ReliquaryCapabilityProvider();
    }
}