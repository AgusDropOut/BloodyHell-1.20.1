package net.agusdropout.bloodyhell.item.custom;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tier;

public class SoulItem extends SwordItem{
    private static final int BLEED_DURATION = 20 * 10;

    public SoulItem(Tier tier, int attackDamageIn, float attackSpeedIn, Item.Properties builder) {
        super(tier, attackDamageIn, attackSpeedIn, builder);
    }

    @Override
    public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {

        if (attacker instanceof Player) {
            Player player = (Player) attacker;
            player.heal(2);
        }

        applyBleedingEffect(target);
        return super.hurtEnemy(stack, target, attacker);
    }

    private void applyBleedingEffect(LivingEntity target) {
        MobEffectInstance bleedingEffect = new MobEffectInstance(MobEffects.WITHER, BLEED_DURATION, 1);
        MobEffectInstance slowingEffect = new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, BLEED_DURATION, 1);
        target.addEffect(bleedingEffect);
        target.addEffect(slowingEffect);
    }
}
