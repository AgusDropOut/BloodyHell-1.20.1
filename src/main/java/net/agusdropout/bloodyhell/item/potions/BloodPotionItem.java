package net.agusdropout.bloodyhell.item.potions;

import net.agusdropout.bloodyhell.capability.crimsonveilPower.PlayerCrimsonveilProvider;
import net.agusdropout.bloodyhell.item.ModItems;
import net.agusdropout.bloodyhell.networking.ModMessages;
import net.agusdropout.bloodyhell.networking.packet.CrimsonVeilDataSyncS2CPacket;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.PotionItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;

public class BloodPotionItem  extends PotionItem {
    private MobEffectInstance effect;
    public BloodPotionItem(Properties p_42979_, MobEffectInstance effect) {
        super(p_42979_);
        this.effect = effect;
    }
    public ItemStack finishUsingItem(ItemStack p_42984_, Level p_42985_, LivingEntity p_42986_) {
        Player $$3 = p_42986_ instanceof Player ? (Player)p_42986_ : null;
        if ($$3 instanceof ServerPlayer) {
            CriteriaTriggers.CONSUME_ITEM.trigger((ServerPlayer)$$3, p_42984_);
        }

        if (!p_42985_.isClientSide) {

            p_42986_.getCapability(PlayerCrimsonveilProvider.PLAYER_CRIMSONVEIL).ifPresent(crimsonVeil -> {
                if(crimsonVeil.getCrimsonVeil() < 100 ) {
                    crimsonVeil.addCrimsomveil(20);
                    ModMessages.sendToPlayer(new CrimsonVeilDataSyncS2CPacket(crimsonVeil.getCrimsonVeil()), ((ServerPlayer) p_42986_));
                }
            });


                    p_42986_.addEffect(effect);

        }


        if ($$3 != null) {
            $$3.awardStat(Stats.ITEM_USED.get(this));
            if (!$$3.getAbilities().instabuild) {
                p_42984_.shrink(1);
            }
        }

        if ($$3 == null || !$$3.getAbilities().instabuild) {
            if (p_42984_.isEmpty()) {
                return new ItemStack(ModItems.BLOOD_FLASK.get());
            }

            if ($$3 != null) {
                $$3.getInventory().add(new ItemStack(ModItems.BLOOD_FLASK.get()));
            }
        }
        p_42986_.gameEvent(GameEvent.DRINK);
        return p_42984_;
    }


}
