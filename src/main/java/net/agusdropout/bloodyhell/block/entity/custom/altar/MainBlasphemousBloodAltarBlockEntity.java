package net.agusdropout.bloodyhell.block.entity.custom.altar;

import net.agusdropout.bloodyhell.block.entity.ModBlockEntities;
import net.agusdropout.bloodyhell.block.entity.base.AbstractMainAltarBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.*;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;
import software.bernie.geckolib.util.RenderUtils;

public class MainBlasphemousBloodAltarBlockEntity extends AbstractMainAltarBlockEntity implements GeoBlockEntity {

    public final AnimatableInstanceCache animatableInstanceCache = GeckoLibUtil.createInstanceCache(this);

    public MainBlasphemousBloodAltarBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(ModBlockEntities.MAIN_BLASPHEMOUS_BLOOD_ALTAR.get(), blockPos, blockState);
    }

    private <T extends GeoBlockEntity> PlayState predicate(AnimationState<T> tAnimationState) {
        if(isActive()) {
            tAnimationState.getController().setAnimation(RawAnimation.begin().then("active", Animation.LoopType.LOOP));
        } else {
            tAnimationState.getController().setAnimation(RawAnimation.begin().then("idle", Animation.LoopType.LOOP));
        }
        return PlayState.CONTINUE;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "controller", 0, this::predicate));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return animatableInstanceCache;
    }

    @Override
    public double getTick(Object blockEntity) {
        return RenderUtils.getCurrentTick();
    }
}