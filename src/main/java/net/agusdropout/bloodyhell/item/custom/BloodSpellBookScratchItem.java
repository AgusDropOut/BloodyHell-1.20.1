package net.agusdropout.bloodyhell.item.custom;

import net.agusdropout.bloodyhell.CrimsonveilPower.PlayerCrimsonveilProvider;
import net.agusdropout.bloodyhell.entity.projectile.BloodSlashEntity;
import net.agusdropout.bloodyhell.item.client.BloodSpellBookScratchItemRenderer;
import net.agusdropout.bloodyhell.networking.ModMessages;
import net.agusdropout.bloodyhell.networking.packet.CrimsonVeilDataSyncS2CPacket;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animatable.SingletonGeoAnimatable;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.*;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.function.Consumer;

public class BloodSpellBookScratchItem extends Item implements GeoItem {
    private static final RawAnimation IDLE_ANIM = RawAnimation.begin().thenLoop("idle");
    private static final RawAnimation CLOSE_ANIM = RawAnimation.begin().thenPlay("close");

    private boolean open = false;
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    public BloodSpellBookScratchItem(Properties properties) {
        super(properties);
        SingletonGeoAnimatable.registerSyncedAnimatable(this);
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slot, boolean selected) {
        super.inventoryTick(stack, level, entity, slot, selected);
    }

    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(new IClientItemExtensions() {
            private BloodSpellBookScratchItemRenderer renderer;

            @Override
            public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                if (this.renderer == null)
                    this.renderer = new BloodSpellBookScratchItemRenderer();

                return this.renderer;
            }
        });
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "Controller", 0, state -> PlayState.CONTINUE)
                .triggerableAnim("close", CLOSE_ANIM)
                .triggerableAnim("idle", IDLE_ANIM));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        if (level instanceof ServerLevel serverLevel) {
            if (open) {
                // Get player rotation
                float yaw = player.getYRot();
                float pitch = player.getXRot();

                // Math helper for spawn positions
                double radians = Math.toRadians(-yaw);
                double xDir = Math.sin(radians);
                double zDir = Math.cos(radians);

                // Base position (1.5 blocks in front of player to avoid clipping)
                double baseX = player.getX() + xDir * 1.5;
                double baseY = player.getEyeY() - 0.4; // Slightly below eye level
                double baseZ = player.getZ() + zDir * 1.5;

                // Calculate perpendicular vector for lateral offsets
                // (Rotate 90 degrees)
                double offsetRadians = Math.toRadians(-yaw + 90);
                double offsetX = Math.sin(offsetRadians) * 1.2; // 1.2 blocks separation
                double offsetZ = Math.cos(offsetRadians) * 1.2;

                // Position calculations
                double leftX = baseX - offsetX;
                double leftZ = baseZ - offsetZ;

                double rightX = baseX + offsetX;
                double rightZ = baseZ + offsetZ;

                player.getCapability(PlayerCrimsonveilProvider.PLAYER_CRIMSONVEIL).ifPresent(playerCrimsonVeil -> {
                    if (playerCrimsonVeil.getCrimsonVeil() >= 5 && !player.getCooldowns().isOnCooldown(this)) {

                        player.getCooldowns().addCooldown(this, 30); // Reduced cooldown slightly for better feel
                        playerCrimsonVeil.subCrimsomveil(10);

                        // --- CENTER SLASH ---
                        // Goes straight
                        BloodSlashEntity centerSlash = new BloodSlashEntity(level, baseX, baseY, baseZ, 10.0F, player, yaw, pitch);

                        float spreadAngle = 10.0f; // Reduced spread to prevent "entangling"

                        // --- RIGHT SLASH ---
                        BloodSlashEntity rightSlash = new BloodSlashEntity(level, leftX, baseY, leftZ, 10.0F, player, yaw + spreadAngle, pitch);

                        // --- LEFT SLASH ---
                        // We want it to spawn on the left, but angle slightly outwards so they fan out
                        // If we just change position but keep Yaw, they fly parallel.
                        // If we change Yaw by -15, it flies to the left.

                        BloodSlashEntity leftSlash = new BloodSlashEntity(level, rightX, baseY, rightZ, 10.0F, player, yaw - spreadAngle, pitch);

                        // Add entities to world
                        level.addFreshEntity(centerSlash);
                        level.addFreshEntity(leftSlash);
                        level.addFreshEntity(rightSlash);

                        if (!level.isClientSide()) {
                            ModMessages.sendToPlayer(new CrimsonVeilDataSyncS2CPacket(playerCrimsonVeil.getCrimsonVeil()), ((ServerPlayer) player));
                        }
                    }
                });

                triggerAnim(player, GeoItem.getOrAssignId(player.getItemInHand(hand), serverLevel), "Controller", "idle");
                open = false;
            } else {
                triggerAnim(player, GeoItem.getOrAssignId(player.getItemInHand(hand), serverLevel), "Controller", "close");
                open = true;
            }
        }

        if (open) {
            level.playLocalSound(player.getX(), player.getY(), player.getZ(), SoundEvents.EVOKER_CAST_SPELL, player.getSoundSource(), 1.0f, 1.0f, false);
        }
        return super.use(level, player, hand);
    }

    @Override
    public void releaseUsing(ItemStack stack, Level level, LivingEntity entity, int timeLeft) {
        super.releaseUsing(stack, level, entity, timeLeft);
        if (level instanceof ServerLevel serverLevel) {
            Player player = (Player) entity;
            triggerAnim(player, GeoItem.getOrAssignId(player.getItemInHand(player.getUsedItemHand()), serverLevel), "Controller", "close");
        }
    }

    @Override
    public void onStopUsing(ItemStack stack, LivingEntity entity, int count) {
        super.onStopUsing(stack, entity, count);
        if (entity.level() instanceof ServerLevel serverLevel) {
            Player player = (Player) entity;
            triggerAnim(player, GeoItem.getOrAssignId(player.getItemInHand(player.getUsedItemHand()), serverLevel), "Controller", "close");
        }
    }

    @Override
    public double getTick(Object itemStack) {
        return GeoItem.super.getTick(itemStack);
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }
}