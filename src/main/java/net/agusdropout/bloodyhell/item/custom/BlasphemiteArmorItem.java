package net.agusdropout.bloodyhell.item.custom;

import net.agusdropout.bloodyhell.entity.client.armor.BlasphemiteArmorRenderer;
import net.agusdropout.bloodyhell.entity.client.armor.BloodArmorRenderer;
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
import software.bernie.geckolib.core.animatable.instance.SingletonAnimatableInstanceCache;
import software.bernie.geckolib.core.animation.*;
import software.bernie.geckolib.core.object.PlayState;

import java.util.function.Consumer;

public class BlasphemiteArmorItem extends ArmorItem implements GeoItem {
    // 1. Necesitas el cache OBLIGATORIAMENTE para que no crashee
    private final AnimatableInstanceCache cache = new SingletonAnimatableInstanceCache(this);

    public BlasphemiteArmorItem(ArmorMaterial material, Type type, Properties properties) {
        super(material, type, properties);
    }

    // 2. Esto es lo que hace que se vea el modelo 3D
    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(new IClientItemExtensions() {
            private BlasphemiteArmorRenderer renderer;

            @Override
            public @NotNull HumanoidModel<?> getHumanoidArmorModel(LivingEntity livingEntity, ItemStack itemStack,
                                                                   EquipmentSlot equipmentSlot, HumanoidModel<?> original) {
                if (this.renderer == null)
                    this.renderer = new BlasphemiteArmorRenderer();

                this.renderer.prepForRender(livingEntity, itemStack, equipmentSlot, original);
                return this.renderer;
            }
        });
    }

    // 3. AQUÍ ESTÁ EL TRUCO: Simplemente devolvemos STOP
    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "controller", 0, state -> {
            // Al devolver STOP, el modelo se queda en su pose "bedrock" (la pose por defecto de Blockbench)
            return PlayState.STOP;
        }));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }
}
