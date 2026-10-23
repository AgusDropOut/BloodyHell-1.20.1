package net.agusdropout.bloodyhell.entity.custom;

import net.agusdropout.bloodyhell.item.registry.SanguiniteItems;
import com.google.common.collect.Sets;
import net.agusdropout.bloodyhell.entity.ModEntityTypes;
import net.minecraft.client.renderer.entity.ArrowRenderer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.Level;

public class SanguiniteArrowEntity extends AbstractArrow {


    public SanguiniteArrowEntity(EntityType<? extends AbstractArrow> p_36721_, Level p_36722_) {
        super(p_36721_, p_36722_);
    }

    public SanguiniteArrowEntity(EntityType<? extends AbstractArrow> p_36711_, double p_36712_, double p_36713_, double p_36714_, Level p_36715_) {
        super(p_36711_, p_36712_, p_36713_, p_36714_, p_36715_);
    }

    public SanguiniteArrowEntity(EntityType<? extends AbstractArrow> p_36717_, LivingEntity p_36718_, Level p_36719_) {
        super(p_36717_, p_36718_, p_36719_);
    }
    public SanguiniteArrowEntity(Level p_36866_, LivingEntity p_36867_) {
        super(ModEntityTypes.SANGUINITE_ARROW.get(), p_36867_, p_36866_);
    }



    @Override
    protected ItemStack getPickupItem() {
        return new ItemStack(SanguiniteItems.SANGUINITE_ARROW.get());
    }


}
