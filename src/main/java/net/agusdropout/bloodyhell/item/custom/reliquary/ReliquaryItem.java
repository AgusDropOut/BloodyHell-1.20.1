package net.agusdropout.bloodyhell.item.custom.reliquary;

import net.agusdropout.bloodyhell.entity.minions.base.AbstractMinionEntity;
import net.agusdropout.bloodyhell.item.client.ClientItemHooks;
import net.agusdropout.bloodyhell.item.custom.base.BaseGeckoItem;
import net.agusdropout.bloodyhell.screen.custom.menu.ReliquaryMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;

import javax.annotation.Nullable;
import java.awt.*;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

public class ReliquaryItem extends BaseGeckoItem {

    private static final RawAnimation IDLE_ANIM = RawAnimation.begin().thenLoop("idle");
    private static final RawAnimation CHARGING_ANIM = RawAnimation.begin().thenLoop("charging");

    // The maximum radius around the player where a minion can spawn
    public static final double MAX_SUMMON_DISTANCE = 8.0;

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
    public void releaseUsing(ItemStack stack, Level level, LivingEntity entityLiving, int timeLeft) {
        if (!(entityLiving instanceof Player player) || level.isClientSide()) return;

        int durationUsed = this.getUseDuration(stack) - timeLeft;

        if (durationUsed >= 20) {
            ServerLevel serverLevel = (ServerLevel) level;
            CompoundTag nbt = stack.getOrCreateTag();

            // 1. EXTRACT COLOR TINT FROM SLOT 13
            AtomicInteger tintColor = getAtomicInteger(stack);
            int finalColor = tintColor.get();

            // 2. CLEAR OLD SUMMONS
            if (nbt.contains("SummonedUUIDs")) {
                ListTag oldUuidList = nbt.getList("SummonedUUIDs", Tag.TAG_INT_ARRAY);
                for (int i = 0; i < oldUuidList.size(); i++) {
                    UUID uuid = NbtUtils.loadUUID(oldUuidList.get(i));
                    Entity oldSummon = serverLevel.getEntity(uuid);
                    if (oldSummon != null && oldSummon.isAlive()) {
                        oldSummon.discard();
                    }
                }
            }

            // 3. SUMMON NEW ENTITIES
            ListTag newUuidList = new ListTag();
            java.util.List<RuneType> activeRunes = getSlottedRunes(stack);
            boolean summonedAny = false;

            for (RuneType rune : activeRunes) {
                BlockPos spawnPos = getValidSpawnPos(player, serverLevel);

                AbstractMinionEntity spawnedMinion = rune.executeSummon(serverLevel, player, spawnPos);

                if (spawnedMinion != null) {
                    // Set Owner
                    spawnedMinion.setOwnerUUID(player.getUUID());
                    // Apply Custom Color!
                    spawnedMinion.setStripeColor(finalColor);

                    newUuidList.add(NbtUtils.createUUID(spawnedMinion.getUUID()));
                    summonedAny = true;
                }
            }

            // 4. FINALIZE
            if (summonedAny) {
                nbt.put("SummonedUUIDs", newUuidList);
                level.playSound(null, player.blockPosition(), SoundEvents.WITHER_SPAWN, SoundSource.PLAYERS, 0.5F, 1.5F);
                player.getCooldowns().addCooldown(this, 200);
            }
        }
    }

    private static @NotNull AtomicInteger getAtomicInteger(ItemStack stack) {
        AtomicInteger tintColor = new AtomicInteger(0xFFBF00);

        stack.getCapability(ForgeCapabilities.ITEM_HANDLER).ifPresent(handler -> {
            ItemStack tintStack = handler.getStackInSlot(13);
            if (!tintStack.isEmpty() && tintStack.getItem() instanceof DyeItem dye) {

                int baseColor = dye.getDyeColor().getFireworkColor();

                int r = (baseColor >> 16) & 0xFF;
                int g = (baseColor >> 8) & 0xFF;
                int b = baseColor & 0xFF;

                float[] hsb = Color.RGBtoHSB(r, g, b, null);

                int maxColor = Color.HSBtoRGB(hsb[0], 1.0F, 1.0F) & 0xFFFFFF;

                tintColor.set(maxColor);
            }
        });

        return tintColor;
    }

    private BlockPos getValidSpawnPos(Player player, ServerLevel level) {
        Random random = new Random();
        for (int i = 0; i < 10; i++) {
            double offsetX = (random.nextDouble() * 2 - 1) * MAX_SUMMON_DISTANCE;
            double offsetZ = (random.nextDouble() * 2 - 1) * MAX_SUMMON_DISTANCE;

            BlockPos targetPos = BlockPos.containing(player.getX() + offsetX, player.getY(), player.getZ() + offsetZ);

            while (targetPos.getY() > level.getMinBuildHeight() && level.getBlockState(targetPos).isAir()) {
                targetPos = targetPos.below();
            }
            targetPos = targetPos.above();

            if (level.getBlockState(targetPos).isAir() && level.getBlockState(targetPos.above()).isAir()) {
                return targetPos;
            }
        }
        return player.blockPosition();
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

        reliquaryStack.getCapability(ForgeCapabilities.ITEM_HANDLER).ifPresent(handler -> {
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