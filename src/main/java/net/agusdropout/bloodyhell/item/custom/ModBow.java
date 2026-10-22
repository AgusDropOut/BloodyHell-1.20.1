package net.agusdropout.bloodyhell.item.custom;

import net.agusdropout.bloodyhell.item.ModItems;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.item.ArrowItem;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.ForgeEventFactory;

public class ModBow extends BowItem {
    public ModBow(Properties p_40660_) {
        super(p_40660_);
    }

    public void releaseUsing(ItemStack itemStack, Level level, LivingEntity entity, int num) {
        if (entity instanceof Player player) {
            boolean flag = player.getAbilities().instabuild || EnchantmentHelper.getItemEnchantmentLevel(Enchantments.INFINITY_ARROWS, itemStack) > 0;
            ItemStack itemstack = player.getProjectile(itemStack);
            int i = this.getUseDuration(itemStack) - num;
            i = ForgeEventFactory.onArrowLoose(itemStack, level, player, i, !itemstack.isEmpty() || flag);
            if (i < 0) {
                return;
            }

            if (!itemstack.isEmpty() || flag) {
                if (itemstack.isEmpty()) {
                    itemstack = new ItemStack(Items.ARROW);
                }

                float f = getPowerForTime(i);
                if (!((double) f < 0.1)) {
                    boolean flag1 = player.getAbilities().instabuild || itemstack.getItem() instanceof ArrowItem && ((ArrowItem) itemstack.getItem()).isInfinite(itemstack, itemStack, player);
                    if (!level.isClientSide) {
                        ArrowItem arrowitem = (ArrowItem) (itemstack.getItem() instanceof ArrowItem ? itemstack.getItem() : Items.ARROW);
                        AbstractArrow abstractarrow = arrowitem.createArrow(level, itemstack, player);
                        AbstractArrow abstractarrow1 = arrowitem.createArrow(level, itemstack, player);
                        AbstractArrow abstractarrow2 = arrowitem.createArrow(level, itemstack, player);
                        AbstractArrow abstractarrow3 = arrowitem.createArrow(level, itemstack, player);
                        AbstractArrow abstractarrow4 = arrowitem.createArrow(level, itemstack, player);
                        abstractarrow = this.customArrow(abstractarrow);
                        abstractarrow1 = this.customArrow(abstractarrow1);
                        abstractarrow2 = this.customArrow(abstractarrow2);
                        abstractarrow3 = this.customArrow(abstractarrow3);
                        abstractarrow4 = this.customArrow(abstractarrow4);

                        abstractarrow.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, f * 3.0F, 1.0F);
                        abstractarrow1.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, f * 3.0F, 2.0F);
                        abstractarrow2.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, f * 3.0F, 0.1F);
                        abstractarrow3.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, f * 3.0F, 0.5F);
                        abstractarrow4.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, f * 3.0F, 3.0F);
                        if (f == 1.0F) {
                            abstractarrow.setCritArrow(true);
                            abstractarrow1.setCritArrow(true);
                            abstractarrow2.setCritArrow(true);
                            abstractarrow3.setCritArrow(true);
                            abstractarrow4.setCritArrow(true);

                        }

                        int j = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.POWER_ARROWS, itemStack);
                        if (j > 0) {
                            abstractarrow.setBaseDamage(abstractarrow.getBaseDamage() + (double) j * 0.5 + 0.5);
                        }

                        int k = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.PUNCH_ARROWS, itemStack);
                        if (k > 0) {
                            abstractarrow.setKnockback(k);
                        }

                        if (EnchantmentHelper.getItemEnchantmentLevel(Enchantments.FLAMING_ARROWS, itemStack) > 0) {
                            abstractarrow.setSecondsOnFire(100);
                        }

                        itemStack.hurtAndBreak(1, player, (p_289501_) -> {
                            p_289501_.broadcastBreakEvent(player.getUsedItemHand());
                        });
                        if (flag1 || player.getAbilities().instabuild && (itemstack.is(Items.SPECTRAL_ARROW) || itemstack.is(Items.TIPPED_ARROW))) {
                            abstractarrow.pickup = AbstractArrow.Pickup.CREATIVE_ONLY;
                        }

                        level.addFreshEntity(abstractarrow);
                        level.addFreshEntity(abstractarrow1);
                        level.addFreshEntity(abstractarrow2);
                        level.addFreshEntity(abstractarrow3);
                        level.addFreshEntity(abstractarrow4);
                    }

                    level.playSound((Player) null, player.getX(), player.getY(), player.getZ(), SoundEvents.ARROW_SHOOT, SoundSource.PLAYERS, 1.0F, 1.0F / (level.getRandom().nextFloat() * 0.4F + 1.2F) + f * 0.5F);
                    if (!flag1 && !player.getAbilities().instabuild) {
                        itemstack.shrink(5);
                        if (itemstack.isEmpty()) {
                            player.getInventory().removeItem(itemstack);
                        }
                    }

                    player.awardStat(Stats.ITEM_USED.get(this));
                }
            }

        }
    }
}