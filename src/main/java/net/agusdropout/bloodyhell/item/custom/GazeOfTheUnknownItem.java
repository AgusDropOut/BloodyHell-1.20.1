package net.agusdropout.bloodyhell.item.custom;

import net.agusdropout.bloodyhell.entity.ModEntityTypes;
import net.agusdropout.bloodyhell.entity.custom.UnknownLanternEntity;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class GazeOfTheUnknownItem extends Item {
    private final double minDistance;
    private final double maxDistance;

    public GazeOfTheUnknownItem(Properties properties, double minDistance, double maxDistance) {
        super(properties);
        this.minDistance = minDistance;
        this.maxDistance = maxDistance;
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity entityLiving) {
        if (entityLiving instanceof Player player) {


            if (!level.isClientSide()) {
                ServerLevel serverLevel = (ServerLevel) level;

                double spawnX = player.getX();
                double spawnZ = player.getZ();
                int spawnY = player.getBlockY();

                boolean foundSpot = false;

                for (int i = 0; i < 15; i++) {
                    double angle = level.random.nextDouble() * Math.PI * 2;
                    double distance = this.minDistance + level.random.nextDouble() * (this.maxDistance - this.minDistance);

                    double testX = player.getX() + distance * Math.cos(angle);
                    double testZ = player.getZ() + distance * Math.sin(angle);

                    int maxY = player.getBlockY() + 5;
                    int minY = player.getBlockY() - 5;

                    for (int y = maxY; y >= minY; y--) {
                        BlockPos checkPos = BlockPos.containing(testX, y, testZ);
                        BlockPos floorPos = checkPos.below();


                        if (serverLevel.getBlockState(checkPos).isAir() &&
                                serverLevel.getBlockState(checkPos.above()).isAir() &&
                                serverLevel.getBlockState(floorPos).isSolidRender(serverLevel, floorPos) &&
                                serverLevel.getFluidState(checkPos).isEmpty() &&
                                serverLevel.getFluidState(floorPos).isEmpty()) {

                            spawnX = testX;
                            spawnY = y;
                            spawnZ = testZ;
                            foundSpot = true;
                            break;
                        }
                    }

                    if (foundSpot) {
                        break;
                    }
                }

                player.displayClientMessage(Component.literal("Your search of forbidden knowledge attracts a visitor").withStyle(ChatFormatting.GOLD), true);

                UnknownLanternEntity lantern = ModEntityTypes.UNKNOWN_LANTERN.get().create(serverLevel);
                if (lantern != null) {
                    lantern.moveTo(spawnX, spawnY, spawnZ, level.random.nextFloat() * 360.0F, 0.0F);
                    lantern.setSummoning(true);
                    lantern.setTargetPlayer(player.getUUID());
                    serverLevel.addFreshEntity(lantern);
                }
            }
            else {
                level.playSound(player, player.blockPosition(), SoundEvents.ELDER_GUARDIAN_CURSE, SoundSource.AMBIENT, 1.0F, 0.5F);
            }
        }

        return super.finishUsingItem(stack, level, entityLiving);
    }
}