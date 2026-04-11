package net.agusdropout.bloodyhell.item.custom;

import net.agusdropout.bloodyhell.entity.ModEntityTypes;
import net.agusdropout.bloodyhell.entity.effects.NamelessTrialRiftEntity;
import net.agusdropout.bloodyhell.entity.unknown.custom.EchoOfTheNamelessEntity;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.Heightmap;

public class NamelessWhisperItem extends Item {

    public NamelessWhisperItem(Properties properties) {
        super(properties);
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity entityLiving) {
        ItemStack result = super.finishUsingItem(stack, level, entityLiving);

        if (level instanceof ServerLevel serverLevel && entityLiving instanceof Player player) {
            double angle = player.getRandom().nextDouble() * Math.PI * 2;
            int totalDistance = 120;
            int lampSpacing = 15;
            int steps = totalDistance / lampSpacing;

            EchoOfTheNamelessEntity previousLamp = null;

            for (int i = 1; i <= steps; i++) {
                double currentX = player.getX() + (Math.cos(angle) * (i * lampSpacing));
                double currentZ = player.getZ() + (Math.sin(angle) * (i * lampSpacing));
                int currentY = serverLevel.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, (int) currentX, (int) currentZ);

                EchoOfTheNamelessEntity lamp = ModEntityTypes.ECHO_OF_THE_NAMELESS.get().create(serverLevel);
                if (lamp != null) {
                    lamp.setPos(currentX, currentY, currentZ);
                    lamp.setOwnerUUID(player.getUUID());
                    serverLevel.addFreshEntity(lamp);

                    if (previousLamp != null) {
                        previousLamp.setNextLampUUID(lamp.getUUID());
                    }
                    previousLamp = lamp;
                }
            }

            double targetX = player.getX() + Math.cos(angle) * totalDistance;
            double targetZ = player.getZ() + Math.sin(angle) * totalDistance;
            int endY = serverLevel.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, (int) targetX, (int) targetZ);

            NamelessTrialRiftEntity rift = ModEntityTypes.NAMELESS_TRIAL_RIFT.get().create(serverLevel);
            if (rift != null) {
                rift.setPos(targetX, endY + 1.0D, targetZ);
                rift.setTargetPlayer(player.getUUID());
                serverLevel.addFreshEntity(rift);
            }
        }

        return result;
    }

    @Override
    public int getUseDuration(ItemStack stack) {
        return 32;
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.EAT;
    }
}