package net.agusdropout.bloodyhell.effect;

import net.minecraft.client.renderer.FogRenderer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;

import java.util.UUID;

public class BloodLustEffect extends MobEffect {
    public BloodLustEffect(MobEffectCategory mobEffectCategory, int color) {
        super(mobEffectCategory, color);
    }
    public static final UUID MOVEMENT_SPEED_MODIFIER_UUID = UUID.fromString("EC6F9365-42AA-4D06-AB22-36D799177F32");
    @Override
    public void applyEffectTick(LivingEntity entity, int amplifier) {

        if (!entity.level().isClientSide()) {

            if(entity instanceof Player) {
                entity.addEffect(new MobEffectInstance(MobEffects.DARKNESS, 1));
                this.addAttributeModifier(Attributes.MOVEMENT_SPEED, MOVEMENT_SPEED_MODIFIER_UUID.toString(), -0.15D, AttributeModifier.Operation.MULTIPLY_TOTAL);
            }
        }




        super.applyEffectTick(entity, amplifier);
    }

    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {

        return true;
    }
}
