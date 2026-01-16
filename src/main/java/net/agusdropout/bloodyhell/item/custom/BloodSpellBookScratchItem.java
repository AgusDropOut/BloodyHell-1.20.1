package net.agusdropout.bloodyhell.item.custom;

import net.agusdropout.bloodyhell.CrimsonveilPower.PlayerCrimsonveilProvider;
import net.agusdropout.bloodyhell.entity.custom.BloodSlashEntity;
 
import net.agusdropout.bloodyhell.entity.projectile.VirulentAnchorProjectileEntity;
import net.agusdropout.bloodyhell.item.client.BloodSpellBookScratchItemRenderer;
import net.agusdropout.bloodyhell.networking.ModMessages;
import net.agusdropout.bloodyhell.networking.packet.CrimsonVeilDataSyncS2CPacket;
import net.agusdropout.bloodyhell.particle.ModParticles;
import net.agusdropout.bloodyhell.util.ClientTickHandler;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.core.Direction;
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
import software.bernie.geckolib.core.animation.*;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.function.Consumer;

public class BloodSpellBookScratchItem extends Item implements GeoItem {
    private static final RawAnimation IDLE_ANIM = RawAnimation.begin().thenLoop("idle");
    private static final RawAnimation CLOSE_ANIM = RawAnimation.begin().thenPlay("close");
    private int useTicks = 0;
    private boolean open = false;

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    public BloodSpellBookScratchItem(Properties properties) {
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
                if(open) {
                    // 🔹 Obtener el yaw del jugador
                    float yaw = player.getYRot();
                    float pitch = player.getXRot();
                    double radians = Math.toRadians(-yaw);

                    // 🔹 Posición base (frontal)
                    double baseX = player.getX() + Math.sin(radians) * 1.0;
                    double baseY = player.getY() + 0.5;
                    double baseZ = player.getZ() + Math.cos(radians) * 1.0;

                    // 🔹 Calcular desplazamientos laterales
                    double offsetX = Math.sin(radians + Math.PI / 2) * 1.0; // Lado derecho
                    double offsetZ = Math.cos(radians + Math.PI / 2) * 1.0; // Lado derecho

                    double leftX = baseX - offsetX;
                    double leftZ = baseZ - offsetZ;

                    double rightX = baseX + offsetX;
                    double rightZ = baseZ + offsetZ;





                    player.getCapability(PlayerCrimsonveilProvider.PLAYER_CRIMSONVEIL).ifPresent(playerCrimsonVeil -> {

                        if(playerCrimsonVeil.getCrimsonVeil() >= 5 && !player.getCooldowns().isOnCooldown(this)){

                            player.getCooldowns().addCooldown(this, 50);


                            playerCrimsonVeil.subCrimsomveil(10);

                            BloodSlashEntity centerSlash = new BloodSlashEntity(level, baseX, baseY, baseZ, 30.0F, player, -yaw, pitch);
                            BloodSlashEntity leftSlash = new BloodSlashEntity(level, leftX, baseY, leftZ, 30.0F, player, -yaw - 15, pitch);  // Izquierda
                            BloodSlashEntity rightSlash = new BloodSlashEntity(level, rightX, baseY, rightZ, 30.0F, player, -yaw + 15, pitch); // Derecha

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
        if (open){
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
