package net.agusdropout.bloodyhell.item.custom;

import net.agusdropout.bloodyhell.entity.ModEntityTypes;
import net.agusdropout.bloodyhell.entity.custom.UnknownLanternEntity;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.Heightmap;

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
        if (!level.isClientSide() && entityLiving instanceof Player player) {
            ServerLevel serverLevel = (ServerLevel) level;

            double angle = level.random.nextDouble() * Math.PI * 2;
            double distance = this.minDistance + level.random.nextDouble() * (this.maxDistance - this.minDistance);
            player.displayClientMessage(Component.literal("Your search of forbidden knowledge attracts a visitor").withStyle(ChatFormatting.GOLD), true);
            double spawnX = player.getX() + distance * Math.cos(angle);
            double spawnZ = player.getZ() + distance * Math.sin(angle);

            int spawnY = serverLevel.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, (int) spawnX, (int) spawnZ);


            UnknownLanternEntity lantern = ModEntityTypes.UNKNOWN_LANTERN.get().create(serverLevel);
            if (lantern != null) {
                lantern.moveTo(spawnX, spawnY, spawnZ, level.random.nextFloat() * 360.0F, 0.0F);
                lantern.setSummoning(true);
                lantern.setTargetPlayer(player.getUUID());
                serverLevel.addFreshEntity(lantern);
            }
        }

        return super.finishUsingItem(stack, level, entityLiving);
    }
}