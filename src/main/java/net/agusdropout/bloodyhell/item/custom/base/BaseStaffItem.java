package net.agusdropout.bloodyhell.item.custom.base;


import net.agusdropout.bloodyhell.item.client.base.BaseStaffRenderer;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.world.item.Item;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animatable.SingletonGeoAnimatable;
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
    protected static final RawAnimation CAST_ANIM = RawAnimation.begin().thenPlay("cast");

    public BaseStaffItem(Properties properties) {
        super(properties);
        SingletonGeoAnimatable.registerSyncedAnimatable(this);
    }

    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(new IClientItemExtensions() {
            private BaseStaffRenderer renderer;

            @Override
            public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                if (this.renderer == null) {

                    this.renderer = new BaseStaffRenderer();
                }
                return this.renderer;
            }
        });
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "controller", 2, this::predicate));
    }

    protected <T extends GeoAnimatable> PlayState predicate(AnimationState<T> event) {
        
        event.getController().setAnimation(IDLE_ANIM);
        return PlayState.CONTINUE;
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    @Override
    public double getTick(Object itemStack) {
        return RenderUtils.getCurrentTick();
    }

    public abstract String getId();
}