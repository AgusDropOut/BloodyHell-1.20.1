package net.agusdropout.bloodyhell.item.custom.base;

import net.agusdropout.bloodyhell.CrimsonveilPower.PlayerCrimsonveilProvider;
import net.agusdropout.bloodyhell.item.client.ClientItemHooks;
import net.agusdropout.bloodyhell.networking.ModMessages;
import net.agusdropout.bloodyhell.networking.packet.CrimsonVeilDataSyncS2CPacket;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.level.Level;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

public abstract class BaseMeleeComboWeapon extends SwordItem implements GeoItem, IComboWeapon {

    protected final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    protected static final RawAnimation IDLE_ANIM = RawAnimation.begin().thenLoop("idle");
    protected static final long COMBO_RESET_MS = 2000;

    public BaseMeleeComboWeapon(Tier tier, int damage, float speed, Properties props) {
        super(tier, damage, speed, props);
    }

    @Override public boolean isPerspectiveAware() { return true; }
    @Override public boolean shouldCancelStandardAttack() { return true; }
    @Override public AnimatableInstanceCache getAnimatableInstanceCache() { return this.cache; }

    @Override
    public boolean onEntitySwing(ItemStack stack, LivingEntity entity) {
        if (entity instanceof Player player) {
            if (player.getCooldowns().isOnCooldown(this)) return true;
            if (stack.getOrCreateTag().getBoolean("IsSpecial")) return true;

            int currentCombo = updateAndGetCombo(stack);
            int duration = getComboDuration(currentCombo);
            player.getCooldowns().addCooldown(this, duration);

            if (!player.level().isClientSide) {
                // Server logic (Damage, Sound)
                CompoundTag tag = stack.getOrCreateTag();
                tag.putBoolean("IsAttacking", true);
                tag.putBoolean("IsSpecial", false);
                tag.putInt("AttackCombo", currentCombo);
                tag.putInt("AttackTickCounter", 0);

                player.level().playSound(null, player.getX(), player.getY(), player.getZ(),
                        getAttackSound(currentCombo), net.minecraft.sounds.SoundSource.PLAYERS, 0.5f, 0.9f + (player.getRandom().nextFloat() * 0.2f));
            }

            if (player.level().isClientSide) {
                // Client logic (Animation)
                handleClientAnimations(player, stack, currentCombo, false);
            }
        }
        return true;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (hand == InteractionHand.MAIN_HAND) {
            if (player.getCooldowns().isOnCooldown(this)) return InteractionResultHolder.fail(stack);

            stack.getOrCreateTag().putLong("LastHitTime", System.currentTimeMillis());

            if (level.isClientSide) {
                stack.getOrCreateTag().putBoolean("IsSpecial", true);
                handleClientAnimations(player, stack, 0, true);
                return InteractionResultHolder.sidedSuccess(stack, true);
            }

            player.getCapability(PlayerCrimsonveilProvider.PLAYER_CRIMSONVEIL).ifPresent(veil -> {
                if (veil.getCrimsonVeil() >= getSpecialCost()) {
                    veil.subCrimsomveil(getSpecialCost());
                    ModMessages.sendToPlayer(new CrimsonVeilDataSyncS2CPacket(veil.getCrimsonVeil()), ((ServerPlayer) player));

                    player.getCooldowns().addCooldown(this, getSpecialDuration());

                    CompoundTag tag = stack.getOrCreateTag();
                    tag.putBoolean("IsAttacking", true);
                    tag.putBoolean("IsSpecial", true);
                    tag.putInt("AttackTickCounter", 0);

                    playSpecialStartSound(level, player);
                } else {
                    player.getCooldowns().addCooldown(this, 10);
                }
            });
            return InteractionResultHolder.sidedSuccess(stack, false);
        }
        return super.use(level, player, hand);
    }

    public void resetAllStates(CompoundTag tag) {
        tag.putBoolean("IsAttacking", false);
        tag.putBoolean("IsSpecial", false);
        tag.putInt("AttackTickCounter", 0);
        tag.putInt("AttackCombo", 0);
    }



    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
        if (isSelected && entity instanceof Player player) {
            ItemStack offhandStack = player.getOffhandItem();
            if (!offhandStack.isEmpty()) {
                if (!player.getInventory().add(offhandStack)) player.drop(offhandStack, true);
                player.setItemInHand(InteractionHand.OFF_HAND, ItemStack.EMPTY);
            }



            if (!level.isClientSide) {
                CompoundTag tag = stack.getOrCreateTag();


                if (tag.getBoolean("IsAttacking")) {
                    int counter = tag.getInt("AttackTickCounter");
                    counter++;
                    tag.putInt("AttackTickCounter", counter);

                    boolean isSpecial = tag.getBoolean("IsSpecial");

                    if (isSpecial) {
                        performSpecialTickLogic(level, player, stack, counter);
                        if (counter >= getSpecialDuration()) resetAttackState(tag);
                    } else {
                        int combo = tag.getInt("AttackCombo");
                        if (counter == getDamageTick(combo)) performComboDamage(level, player, combo, stack);
                        if (counter >= getComboDuration(combo)) resetAttackState(tag);
                    }
                }
            }
        }
    }

    protected void resetAttackState(CompoundTag tag) {
        tag.putBoolean("IsAttacking", false);
        tag.putBoolean("IsSpecial", false);
        tag.putInt("AttackTickCounter", 0);
    }

    public int updateAndGetCombo(ItemStack stack) {
        CompoundTag tag = stack.getOrCreateTag();
        long lastTime = tag.getLong("LastHitTime");
        int currentCombo = tag.getInt("CurrentCombo");
        long currentTime = System.currentTimeMillis();

        if (currentTime - lastTime > COMBO_RESET_MS) currentCombo = 0;
        currentCombo++;
        if (currentCombo > getMaxCombos()) currentCombo = 1;

        tag.putInt("CurrentCombo", currentCombo);
        tag.putLong("LastHitTime", currentTime);
        return currentCombo;
    }

    private void handleClientAnimations(Player player, ItemStack stack, int combo, boolean isSpecial) {
        if (isSpecial) {
            if (ClientItemHooks.isFirstPerson()) {
                ClientItemHooks.triggerGeckoAnim(player, stack, "Controller", getSpecialGeckoTrigger());
            } else {
                ClientItemHooks.triggerGeckoAnim(player, stack, "Controller", "idle_trigger");
                ClientItemHooks.playPlayerAnimatorAnim(player, getSpecialPlayerAnim());
            }
        } else {
            if (ClientItemHooks.isFirstPerson()) {
                // DEBUG: Print to ensure this block is reached
                System.out.println("DEBUG: Triggering FPV Combo: " + combo + " | Trigger: " + getComboGeckoTrigger(combo));
                ClientItemHooks.triggerGeckoAnim(player, stack, "Controller", getComboGeckoTrigger(combo));
            } else {
                ClientItemHooks.triggerGeckoAnim(player, stack, "Controller", "idle_trigger");
                ClientItemHooks.playPlayerAnimatorAnim(player, getComboPlayerAnim(combo));
            }
        }
    }

    // Abstract methods to implement in specific weapons
    public abstract int getMaxCombos();
    public abstract int getComboDuration(int combo);
    public abstract int getDamageTick(int combo);
    public abstract void performComboDamage(Level level, Player player, int combo, ItemStack stack);

    public abstract int getSpecialCost();
    public abstract int getSpecialDuration();
    public abstract void performSpecialTickLogic(Level level, Player player, ItemStack stack, int currentTick);
    public abstract void playSpecialStartSound(Level level, Player player);

    public abstract String getComboGeckoTrigger(int combo);
    public abstract String getSpecialGeckoTrigger();
    public abstract String getComboPlayerAnim(int combo);
    public abstract String getSpecialPlayerAnim();
    public abstract SoundEvent getAttackSound(int combo);

    @Override
    public boolean isComboWindowExpired(ItemStack stack, long currentTime) {
        CompoundTag tag = stack.getTag();
        return tag != null && tag.contains("LastHitTime") && (currentTime - tag.getLong("LastHitTime") > COMBO_RESET_MS);
    }
}