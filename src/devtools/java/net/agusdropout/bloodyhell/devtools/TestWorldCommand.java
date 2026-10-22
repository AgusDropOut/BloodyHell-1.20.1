package net.agusdropout.bloodyhell.devtools;

import com.mojang.brigadier.CommandDispatcher;
import net.agusdropout.bloodyhell.BloodyHell;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.List;

@Mod.EventBusSubscriber(modid = BloodyHell.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class TestWorldCommand {
    private static final String TAG = "[BHTEST] ";
    private static final int SPACING = 3;
    private static final int COLUMNS = 16;
    private static final int SECTION_GAP = 6;
    private static final int CLEAR_HEIGHT = 8;
    private static final int CLEAR_MARGIN = 6;
    private static final int CLEAR_DEPTH_BELOW = 2;
    private static final int ENTITY_CLEAR_HEIGHT = 32;
    private static final int CHEST_SLOTS = 27;

    public record Layout(BlockPos blockOrigin, BlockPos entityOrigin, BlockPos itemOrigin, int width, int totalDepth) {
    }

    private static volatile Layout lastLayout;

    public static Layout getLastLayout() {
        return lastLayout;
    }

    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        if (FMLEnvironment.production) return;
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();
        dispatcher.register(Commands.literal("bhtest")
                .requires(source -> source.hasPermission(2))
                .then(Commands.literal("build").executes(context -> {
                    CommandSourceStack source = context.getSource();
                    build(source.getLevel(), originFor(source));
                    return 1;
                }))
                .then(Commands.literal("clear").executes(context -> {
                    CommandSourceStack source = context.getSource();
                    Layout layout = layoutFor(originFor(source));
                    int[] cleared = clearWorld(source.getLevel(), layout);
                    String summary = TAG + "cleared blocks=" + cleared[0] + " entities=" + cleared[1];
                    BloodyHell.LOGGER.info(summary);
                    source.sendSuccess(() -> Component.literal(summary), false);
                    return 1;
                })));
    }

    private static BlockPos originFor(CommandSourceStack source) {
        if (source.getEntity() instanceof Player player) return player.blockPosition();
        return source.getLevel().getSharedSpawnPos();
    }

    private static Layout layoutFor(BlockPos origin) {
        int blockCount = 0;
        for (Block block : ForgeRegistries.BLOCKS) {
            if (isOurs(ForgeRegistries.BLOCKS.getKey(block).getNamespace())) blockCount++;
        }
        int entityCount = 0;
        for (EntityType<?> type : ForgeRegistries.ENTITY_TYPES) {
            if (isOurs(ForgeRegistries.ENTITY_TYPES.getKey(type).getNamespace())) entityCount++;
        }
        int itemCount = 0;
        for (Item item : ForgeRegistries.ITEMS) {
            if (isOurs(ForgeRegistries.ITEMS.getKey(item).getNamespace())) itemCount++;
        }
        int chestCount = (itemCount + CHEST_SLOTS - 1) / CHEST_SLOTS;
        BlockPos blockOrigin = origin.offset(0, 0, SECTION_GAP);
        BlockPos entityOrigin = blockOrigin.offset(0, 0, rows(blockCount) * SPACING + SECTION_GAP);
        BlockPos itemOrigin = entityOrigin.offset(0, 0, rows(entityCount) * SPACING + SECTION_GAP);
        int totalDepth = (rows(blockCount) + rows(entityCount) + rows(chestCount)) * SPACING + SECTION_GAP * 3;
        return new Layout(blockOrigin, entityOrigin, itemOrigin, COLUMNS * SPACING, totalDepth);
    }

    public static void build(ServerLevel level, BlockPos origin) {
        MinecraftServer server = level.getServer();
        runSilently(server, "gamerule doDaylightCycle false");
        runSilently(server, "gamerule doMobSpawning false");
        runSilently(server, "time set day");
        runSilently(server, "weather clear");

        List<Block> blocks = new ArrayList<>();
        for (Block block : ForgeRegistries.BLOCKS) {
            if (isOurs(ForgeRegistries.BLOCKS.getKey(block).getNamespace())) blocks.add(block);
        }
        List<EntityType<?>> entityTypes = new ArrayList<>();
        for (EntityType<?> type : ForgeRegistries.ENTITY_TYPES) {
            if (isOurs(ForgeRegistries.ENTITY_TYPES.getKey(type).getNamespace())) entityTypes.add(type);
        }
        List<Item> items = new ArrayList<>();
        for (Item item : ForgeRegistries.ITEMS) {
            if (isOurs(ForgeRegistries.ITEMS.getKey(item).getNamespace())) items.add(item);
        }

        Layout layout = layoutFor(origin);
        lastLayout = layout;
        int[] cleared = clearWorld(level, layout);
        BloodyHell.LOGGER.info(TAG + "cleared blocks=" + cleared[0] + " entities=" + cleared[1]);

        int placedBlocks = placeBlocks(level, blocks, layout.blockOrigin());
        int spawnedEntities = spawnEntities(level, entityTypes, layout.entityOrigin());
        int filledChests = placeItemChests(level, items, layout.itemOrigin());

        String summary = TAG + "built blocks=" + placedBlocks + "/" + blocks.size()
                + " entities=" + spawnedEntities + "/" + entityTypes.size()
                + " itemChests=" + filledChests + " items=" + items.size();
        BloodyHell.LOGGER.info(summary);
        server.getPlayerList().broadcastSystemMessage(Component.literal(summary), false);
        BlockAudit.write(level);
    }

    private static void runSilently(MinecraftServer server, String command) {
        server.getCommands().performPrefixedCommand(server.createCommandSourceStack().withSuppressedOutput(), command);
    }

    private static boolean isOurs(String namespace) {
        return namespace.equals(BloodyHell.MODID);
    }

    private static int rows(int count) {
        return (count + COLUMNS - 1) / COLUMNS;
    }

    private static int[] clearWorld(ServerLevel level, Layout layout) {
        BlockPos start = layout.blockOrigin();
        BlockState air = Blocks.AIR.defaultBlockState();
        BlockState floor = Blocks.SMOOTH_STONE.defaultBlockState();
        int clearedBlocks = 0;
        for (int x = -CLEAR_MARGIN; x < layout.width() + CLEAR_MARGIN; x++) {
            for (int z = -CLEAR_MARGIN; z < layout.totalDepth() + CLEAR_MARGIN; z++) {
                boolean inside = x >= 0 && x < layout.width() && z >= 0 && z < layout.totalDepth();
                for (int y = 0; y < CLEAR_HEIGHT; y++) {
                    BlockPos pos = start.offset(x, y, z);
                    if (!level.getBlockState(pos).isAir() || !level.getFluidState(pos).isEmpty()) clearedBlocks++;
                    level.setBlock(pos, air, Block.UPDATE_CLIENTS | Block.UPDATE_SUPPRESS_DROPS);
                }
                if (inside) level.setBlock(start.offset(x, -1, z), floor, Block.UPDATE_CLIENTS);
            }
        }
        AABB area = new AABB(
                start.getX() - CLEAR_MARGIN, start.getY() - CLEAR_DEPTH_BELOW, start.getZ() - CLEAR_MARGIN,
                start.getX() + layout.width() + CLEAR_MARGIN, start.getY() + ENTITY_CLEAR_HEIGHT,
                start.getZ() + layout.totalDepth() + CLEAR_MARGIN);
        List<Entity> leftovers = level.getEntities((Entity) null, area, entity -> !(entity instanceof Player));
        for (Entity entity : leftovers) {
            entity.discard();
        }
        return new int[]{clearedBlocks, leftovers.size()};
    }

    private static int placeBlocks(ServerLevel level, List<Block> blocks, BlockPos origin) {
        int placed = 0;
        for (int i = 0; i < blocks.size(); i++) {
            Block block = blocks.get(i);
            BlockPos pos = origin.offset((i % COLUMNS) * SPACING, 0, (i / COLUMNS) * SPACING);
            try {
                level.setBlock(pos, block.defaultBlockState(), Block.UPDATE_CLIENTS);
                placed++;
            } catch (Throwable throwable) {
                BloodyHell.LOGGER.error(TAG + "block failed " + ForgeRegistries.BLOCKS.getKey(block), throwable);
            }
        }
        return placed;
    }

    private static int spawnEntities(ServerLevel level, List<EntityType<?>> types, BlockPos origin) {
        int spawned = 0;
        for (int i = 0; i < types.size(); i++) {
            EntityType<?> type = types.get(i);
            BlockPos pos = origin.offset((i % COLUMNS) * SPACING, 0, (i / COLUMNS) * SPACING);
            try {
                Entity entity = type.create(level);
                if (entity == null) continue;
                entity.moveTo(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, 0.0F, 0.0F);
                entity.setInvulnerable(true);
                if (entity instanceof Mob mob) {
                    mob.setNoAi(true);
                    mob.setPersistenceRequired();
                }
                if (level.addFreshEntity(entity)) spawned++;
            } catch (Throwable throwable) {
                BloodyHell.LOGGER.error(TAG + "entity failed " + ForgeRegistries.ENTITY_TYPES.getKey(type), throwable);
            }
        }
        return spawned;
    }

    private static int placeItemChests(ServerLevel level, List<Item> items, BlockPos origin) {
        int chests = 0;
        for (int start = 0; start < items.size(); start += CHEST_SLOTS) {
            int index = start / CHEST_SLOTS;
            BlockPos pos = origin.offset((index % COLUMNS) * SPACING, 0, (index / COLUMNS) * SPACING);
            level.setBlock(pos, Blocks.CHEST.defaultBlockState().setValue(ChestBlock.FACING, Direction.NORTH), Block.UPDATE_CLIENTS);
            if (!(level.getBlockEntity(pos) instanceof ChestBlockEntity chest)) continue;
            int end = Math.min(start + CHEST_SLOTS, items.size());
            for (int slot = 0; slot < end - start; slot++) {
                Item item = items.get(start + slot);
                try {
                    chest.setItem(slot, new ItemStack(item));
                } catch (Throwable throwable) {
                    BloodyHell.LOGGER.error(TAG + "item failed " + ForgeRegistries.ITEMS.getKey(item), throwable);
                }
            }
            chests++;
        }
        return chests;
    }
}
