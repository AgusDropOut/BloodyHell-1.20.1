package net.agusdropout.bloodyhell.item.custom.base;

import net.agusdropout.bloodyhell.client.render.entity.armor.GenericGeckoArmorRenderer;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.Animation;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.function.Consumer;

public class BaseGeckoArmorItem extends ArmorItem implements GeoItem, GeckoAsset {
    public record Look(String assetName, boolean animated, boolean glowing) {}

    public static final Look SANGUINITE = new Look("sanguinite_armor", true, false);
    public static final Look RHNULL = new Look("rhnull_armor", true, true);
    public static final Look BLASPHEMITE = new Look("blasphemite_armor", false, true);

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private final Look look;

    public BaseGeckoArmorItem(ArmorMaterial material, Type type, Properties properties, Look look) {
        super(material, type, properties);
        this.look = look;
    }

    public Look getLook() {
        return look;
    }

    @Override
    public String getId() {
        return look.assetName();
    }

    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(new IClientItemExtensions() {
            private GenericGeckoArmorRenderer renderer;

            @Override
            public @NotNull HumanoidModel<?> getHumanoidArmorModel(LivingEntity livingEntity, ItemStack itemStack,
                                                                   EquipmentSlot equipmentSlot, HumanoidModel<?> original) {
                if (this.renderer == null) this.renderer = new GenericGeckoArmorRenderer(look);
                this.renderer.prepForRender(livingEntity, itemStack, equipmentSlot, original);
                return this.renderer;
            }
        });
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "controller", 0, state -> {
            if (!look.animated()) return PlayState.STOP;
            state.getController().setAnimation(RawAnimation.begin().then("idle", Animation.LoopType.LOOP));
            return PlayState.CONTINUE;
        }));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }
}
