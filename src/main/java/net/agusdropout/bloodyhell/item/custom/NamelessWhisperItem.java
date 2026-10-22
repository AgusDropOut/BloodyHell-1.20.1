package net.agusdropout.bloodyhell.item.custom;

import net.agusdropout.bloodyhell.entity.ModEntityTypes;
import net.agusdropout.bloodyhell.entity.effects.NamelessTrialRiftEntity;
import net.agusdropout.bloodyhell.entity.unknown.custom.EchoOfTheNamelessEntity;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
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


            serverLevel.playSound(null, player.blockPosition(), SoundEvents.AMBIENT_CAVE.get(), SoundSource.AMBIENT, 1.5F, 0.8F);
            serverLevel.playSound(null, player.blockPosition(), SoundEvents.WARDEN_HEARTBEAT, SoundSource.PLAYERS, 2.0F, 1.0F);
            serverLevel.playSound(null, player.blockPosition(), SoundEvents.SCULK_SHRIEKER_SHRIEK, SoundSource.HOSTILE, 0.5F, 0.5F);

            double baseAngle = player.getRandom().nextDouble() * Math.PI * 2;

            int lampSpacing = 25;
            int steps = 4;

            EchoOfTheNamelessEntity previousLamp = null;

            double currentX = player.getX();
            double currentZ = player.getZ();

            for (int i = 1; i <= steps; i++) {
                double angleWobble = baseAngle + (player.getRandom().nextDouble() - 0.5D) * 0.5D;

                currentX += Math.cos(angleWobble) * lampSpacing;
                currentZ += Math.sin(angleWobble) * lampSpacing;

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

            double finalAngle = baseAngle + (player.getRandom().nextDouble() - 0.5D) * 0.5D;
            currentX += Math.cos(finalAngle) * lampSpacing;
            currentZ += Math.sin(finalAngle) * lampSpacing;

            int endY = serverLevel.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, (int) currentX, (int) currentZ);

            NamelessTrialRiftEntity rift = ModEntityTypes.NAMELESS_TRIAL_RIFT.get().create(serverLevel);
            if (rift != null) {
                rift.setPos(currentX, endY + 1.0D, currentZ);
                rift.setTargetPlayer(player.getUUID());
                serverLevel.addFreshEntity(rift);

                if (previousLamp != null) {
                    previousLamp.setNextLampUUID(rift.getUUID());
                }
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