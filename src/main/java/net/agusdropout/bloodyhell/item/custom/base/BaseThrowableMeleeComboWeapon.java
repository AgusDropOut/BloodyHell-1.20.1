package net.agusdropout.bloodyhell.item.custom.base;

import net.agusdropout.bloodyhell.CrimsonveilPower.PlayerCrimsonveilProvider;
import net.agusdropout.bloodyhell.item.client.ClientItemHooks;
import net.agusdropout.bloodyhell.networking.ModMessages;
import net.agusdropout.bloodyhell.networking.packet.CrimsonVeilDataSyncS2CPacket;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.minecraft.world.level.Level;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;

public abstract class BaseThrowableMeleeComboWeapon extends BaseMeleeComboWeapon {

    public BaseThrowableMeleeComboWeapon(Tier tier, int damage, float speed, Properties props) {
        super(tier, damage, speed, props);
    }

    public abstract int getThrowCost();
    public abstract int getThrowSpawnTick();
    public abstract int getThrowDuration();
    public abstract void spawnProjectile(Level level, Player player, ItemStack stack);

    public abstract String getChargePlayerAnim();
    public abstract String getThrowPlayerAnim();
    public abstract String getChargeGeckoAnim();
    public abstract String getThrowGeckoTrigger();

    @Override
    public boolean onEntitySwing(ItemStack stack, LivingEntity entity) {
        if (entity.isUsingItem() || stack.getOrCreateTag().getBoolean("IsThrowing")) {
            return true;
        }
        return super.onEntitySwing(stack, entity);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (player.getCooldowns().isOnCooldown(this) || stack.getOrCreateTag().getBoolean("IsAttacking")) {
            return InteractionResultHolder.fail(stack);
        }

        player.startUsingItem(hand);
        return InteractionResultHolder.consume(stack);
    }

    @Override
    public void onUseTick(Level level, LivingEntity entity, ItemStack stack, int count) {
        if (level.isClientSide && entity instanceof Player player) {
            ClientItemHooks.playPlayerAnimatorAnim(player, getChargePlayerAnim());
        }
    }

    @Override
    public boolean onDroppedByPlayer(ItemStack item, Player player) {
        ClientItemHooks.triggerGeckoAnim(player, item, "Controller",  getIdleGeckoAnim());
        return super.onDroppedByPlayer(item, player);

    }



    @Override
    public void onStopUsing(ItemStack stack, LivingEntity entity, int count) {

        if(entity instanceof Player player) {
            ClientItemHooks.triggerGeckoAnim(player, stack, "Controller", getIdleGeckoAnim());
        }
        super.onStopUsing(stack, entity, count);
    }

    @Override
    public void releaseUsing(ItemStack stack, Level level, LivingEntity entity, int timeLeft) {
        if (entity instanceof Player player) {
            int duration = this.getUseDuration(stack) - timeLeft;

            if (duration >= 10) {
                CompoundTag tag = stack.getOrCreateTag();
                tag.putBoolean("IsThrowing", true);
                tag.putInt("ThrowTickCounter", 0);

                if (level.isClientSide) {
                    ClientItemHooks.playPlayerAnimatorAnim(player, getThrowPlayerAnim());

                }

                if (!level.isClientSide) {
                    player.getCapability(PlayerCrimsonveilProvider.PLAYER_CRIMSONVEIL).ifPresent(veil -> {
                        if (veil.getCrimsonVeil() >= getThrowCost()) {
                            veil.subCrimsomveil(getThrowCost());
                            ModMessages.sendToPlayer(new CrimsonVeilDataSyncS2CPacket(veil.getCrimsonVeil()), (ServerPlayer) player);
                            tag.putBoolean("CanSpawnProjectile", true);
                        } else {
                            tag.putBoolean("CanSpawnProjectile", false);
                        }
                    });
                }
            }
        }
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
        super.inventoryTick(stack, level, entity, slotId, isSelected);

        if (!level.isClientSide && entity instanceof Player player && isSelected) {
            CompoundTag tag = stack.getOrCreateTag();

            if (tag.getBoolean("IsThrowing")) {
                int counter = tag.getInt("ThrowTickCounter");
                counter++;
                tag.putInt("ThrowTickCounter", counter);

                if (counter == getThrowSpawnTick()) {
                    if (tag.getBoolean("CanSpawnProjectile")) {
                        spawnProjectile(level, player, stack);
                    }
                }

                if (counter >= getThrowDuration()) {
                    tag.putBoolean("IsThrowing", false);
                    tag.putBoolean("CanSpawnProjectile", false);
                }
            }

            if(!tag.getBoolean("IsAttacking") && !tag.getBoolean("IsThrowing")) {
                ClientItemHooks.triggerGeckoAnim(player, stack, "Controller", getIdleGeckoAnim());
            }
        }
    }


    protected PlayState throwableControllerPredicate(software.bernie.geckolib.core.animation.AnimationState<?> state) {
        if (ClientItemHooks.getLocalPlayer() == null) return PlayState.STOP;
        if ( !ClientItemHooks.isFirstPerson()) {
            return state.setAndContinue(IDLE_ANIM);
        }

        ItemStack stack = state.getData(DataTickets.ITEMSTACK);


        if (ClientItemHooks.getLocalPlayer().isUsingItem() && ClientItemHooks.getLocalPlayer().getUseItem() == stack) {
            return state.setAndContinue(RawAnimation.begin().thenLoop(getChargeGeckoAnim()));
        }


        return state.setAndContinue(IDLE_ANIM);
    }



    @Override
    public void resetAllStates(CompoundTag tag) {
        super.resetAllStates(tag);
        tag.putBoolean("IsThrowing", false);
        tag.putBoolean("CanSpawnProjectile", false);
        tag.putInt("ThrowTickCounter", 0);
    }

    @Override public int getUseDuration(ItemStack stack) { return 7200; }
    @Override public int getSpecialCost() { return 0; }
    @Override public int getSpecialDuration() { return 0; }
    @Override public void performSpecialTickLogic(Level level, Player player, ItemStack stack, int currentTick) {}
    @Override public void playSpecialStartSound(Level level, Player player) {}
    @Override public String getSpecialGeckoTrigger() { return ""; }
    @Override public String getSpecialPlayerAnim() { return ""; }
    public abstract String getIdleGeckoAnim();
}