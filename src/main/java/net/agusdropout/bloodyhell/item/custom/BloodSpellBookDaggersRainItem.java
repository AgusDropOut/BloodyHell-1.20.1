package net.agusdropout.bloodyhell.item.custom;

import net.agusdropout.bloodyhell.CrimsonveilPower.PlayerCrimsonveilProvider;
import net.agusdropout.bloodyhell.entity.projectile.BloodPortalEntity;

import net.agusdropout.bloodyhell.item.client.BloodSpellBookDaggersRainItemRenderer;
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
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animatable.SingletonGeoAnimatable;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.function.Consumer;

public class BloodSpellBookDaggersRainItem extends Item implements GeoItem {
    private static final RawAnimation IDLE_ANIM = RawAnimation.begin().thenLoop("idle");
    private static final RawAnimation CLOSE_ANIM = RawAnimation.begin().thenPlay("close");
    private int useTicks = 0;
    private boolean open = false;

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    public BloodSpellBookDaggersRainItem(Properties properties) {
        super(properties);
        SingletonGeoAnimatable.registerSyncedAnimatable(this);
    }

    @Override
    public void inventoryTick(ItemStack p_41404_, Level p_41405_, Entity p_41406_, int p_41407_, boolean p_41408_) {
        super.inventoryTick(p_41404_, p_41405_, p_41406_, p_41407_, p_41408_);
    }

    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(new IClientItemExtensions() {
            private BloodSpellBookDaggersRainItemRenderer renderer;

            @Override
            public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                if (this.renderer == null)
                    this.renderer = new BloodSpellBookDaggersRainItemRenderer();

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




                player.getCapability(PlayerCrimsonveilProvider.PLAYER_CRIMSONVEIL).ifPresent(playerCrimsonVeil -> {
                    if (playerCrimsonVeil.getCrimsonVeil() >= 10 && !player.getCooldowns().isOnCooldown(this)) {
                        player.getCooldowns().addCooldown(this, 200);
                        playerCrimsonVeil.subCrimsomveil(20);
                        double centerX = player.getX();
                        double centerY = player.getY() + 8;
                        double centerZ = player.getZ();

                        // 🔹 Generar 5 dagas con posiciones y ángulos aleatorios
                        for (int i = 0; i < 1; i++) {

                            float xMotion = (float) (Math.random() * 2 - 1);
                            float yMotion = -(float) 0.5;
                            float zMotion = (float) (Math.random() * 2 - 1);


                        }

                        // 🔹 Sincronizar CrimsonVeil con el cliente
                        if (!level.isClientSide()) {
                            ModMessages.sendToPlayer(
                                    new CrimsonVeilDataSyncS2CPacket(playerCrimsonVeil.getCrimsonVeil()),
                                    (ServerPlayer) player
                            );
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
