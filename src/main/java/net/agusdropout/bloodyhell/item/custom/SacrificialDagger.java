package net.agusdropout.bloodyhell.item.custom;

import net.agusdropout.bloodyhell.entity.custom.SanguineSacrificeEntity;
import net.agusdropout.bloodyhell.item.ModItems;
import net.minecraft.ChatFormatting;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class SacrificialDagger extends SwordItem {

    public SacrificialDagger(Tier p_43269_, int p_43270_, float p_43271_, Properties p_43272_) {
        super(p_43269_, p_43270_, p_43271_, p_43272_);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.literal("Sacrifice entities to summon their souls").withStyle(ChatFormatting.RED));
        tooltip.add(Component.literal("Right-click with Blood Flask in off-hand to extract own blood").withStyle(ChatFormatting.DARK_RED));
        super.appendHoverText(stack, level, tooltip, flag);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack dagger = player.getItemInHand(hand);

        if (hand == InteractionHand.MAIN_HAND && !player.getCooldowns().isOnCooldown(this)) {
            ItemStack offhandItem = player.getItemInHand(InteractionHand.OFF_HAND);

            if (offhandItem.getItem() == ModItems.BLOOD_FLASK.get()) {
                if (!level.isClientSide) {
                    player.hurt(level.damageSources().magic(), 4.0F);

                    offhandItem.shrink(1);

                    ItemStack boundFlask = new ItemStack(ModItems.BOUND_BLOOD_FLASK.get());
                    CompoundTag nbt = boundFlask.getOrCreateTag();
                    nbt.putUUID("BloodOwnerUUID", player.getUUID());
                    nbt.putString("BloodOwnerName", player.getScoreboardName());

                    if (!player.getInventory().add(boundFlask)) {
                        player.drop(boundFlask, false);
                    }

                    player.getCooldowns().addCooldown(this, 100);

                    level.playSound(null, player.blockPosition(), SoundEvents.BOTTLE_FILL_DRAGONBREATH, SoundSource.PLAYERS, 1.0F, 0.8F);
                    level.playSound(null, player.blockPosition(), SoundEvents.PLAYER_HURT, SoundSource.PLAYERS, 1.0F, 1.0F);

                    ((ServerLevel) level).sendParticles(ParticleTypes.DAMAGE_INDICATOR,
                            player.getX(), player.getY() + 1.0, player.getZ(),
                            15, 0.2, 0.2, 0.2, 0.1);
                }
                return InteractionResultHolder.sidedSuccess(dagger, level.isClientSide());
            }
        }
        return super.use(level, player, hand);
    }

    @Override
    public boolean hurtEnemy(ItemStack itemStack, LivingEntity target, LivingEntity dealer) {
        if (!((Player)dealer).getCooldowns().isOnCooldown(this)) {

            target.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 200, 250, false, false));
            target.hasImpulse = false;

            if (dealer instanceof Player player) {
                player.getCooldowns().addCooldown(this, 200);
            }

            SanguineSacrificeEntity unknownEye = new SanguineSacrificeEntity(target.level(), target.getX(), target.getY(), target.getZ(), target.getYRot(), 0, 1, dealer, target);
            unknownEye.moveTo(target.getX(), target.getY(), target.getZ(), target.getYRot(), target.getXRot());
            target.setDeltaMovement(0, 0, 0);
            target.level().addFreshEntity(unknownEye);

            if (target.level() instanceof ServerLevel serverLevel) {
                serverLevel.sendParticles(ParticleTypes.SMOKE,
                        target.getX(), target.getY() + 0.5, target.getZ(),
                        20, 0.3, 0.3, 0.3, 0.01);
            }

            target.level().playSound(null, target.blockPosition(), SoundEvents.ENDERMAN_TELEPORT, SoundSource.PLAYERS, 1.0F, 0.5F);
        }
        return super.hurtEnemy(itemStack, dealer, target);
    }
}