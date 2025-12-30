package net.agusdropout.bloodyhell.item.custom;

import net.agusdropout.bloodyhell.item.client.BlasphemousTwinDaggersRenderer;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
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

public class BlasphemousTwinDaggerItem extends Item implements GeoItem {

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private static final RawAnimation ATTACK_ANIM = RawAnimation.begin().thenPlay("attack");

    public BlasphemousTwinDaggerItem(Properties properties) {
        super(properties);
        SingletonGeoAnimatable.registerSyncedAnimatable(this);
    }

    // --- LÓGICA DE ATAQUE (Solo animación del ÍTEM) ---
    // Esto es útil para que en Primera Persona el ítem haga su movimiento.
    // La animación del CUERPO (Tercera Persona) la manejaremos con PlayerAnimator en otro lado (KeyBind o evento).
    @Override
    public boolean onEntitySwing(ItemStack stack, LivingEntity entity) {
        if (entity instanceof Player player && !player.level().isClientSide) {
            triggerItemAnimation(player, stack);
        }
        return false; // False permite el swing vanilla
    }

    private void triggerItemAnimation(Player player, ItemStack stack) {
        if (player.level() instanceof ServerLevel serverLevel) {
            long instanceId = GeoItem.getOrAssignId(stack, serverLevel);
            triggerAnim(player, instanceId, "controller", "attack_trigger");
        }
    }

    // --- GECKOLIB SETUP ---
    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "controller", 0, state -> PlayState.STOP)
                .triggerableAnim("attack_trigger", ATTACK_ANIM)
        );
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }

    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(new IClientItemExtensions() {
            private BlasphemousTwinDaggersRenderer renderer;

            @Override
            public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                if (this.renderer == null)
                    this.renderer = new BlasphemousTwinDaggersRenderer();
                return this.renderer;
            }
        });
    }
}