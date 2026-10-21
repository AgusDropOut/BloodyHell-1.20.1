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
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
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
    private static final int CLEAR_HEIGHT = 6;
    private static final int CHEST_SLOTS = 27;

    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        if (FMLEnvironment.production) return;
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();
        dispatcher.register(Commands.literal("bhtest")
                .requires(source -> source.hasPermission(2))
                .then(Commands.literal("build").executes(context -> {
                    CommandSourceStack source = context.getSource();
                    build(source.getLevel(), BlockPos.containing(source.getPosition()));
                    return 1;
                })));
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

        int blockRows = rows(blocks.size());
        int entityRows = rows(entityTypes.size());
        int chestCount = (items.size() + CHEST_SLOTS - 1) / CHEST_SLOTS;
        int itemRows = rows(chestCount);

        BlockPos blockOrigin = origin.offset(0, 0, SECTION_GAP);
        BlockPos entityOrigin = blockOrigin.offset(0, 0, blockRows * SPACING + SECTION_GAP);
        BlockPos itemOrigin = entityOrigin.offset(0, 0, entityRows * SPACING + SECTION_GAP);

        int totalDepth = (blockRows + entityRows + itemRows) * SPACING + SECTION_GAP * 3;
        clearArea(level, blockOrigin, COLUMNS * SPACING, totalDepth);

        int placedBlocks = placeBlocks(level, blocks, blockOrigin);
        int spawnedEntities = spawnEntities(level, entityTypes, entityOrigin);
        int filledChests = placeItemChests(level, items, itemOrigin);

        String summary = TAG + "built blocks=" + placedBlocks + "/" + blocks.size()
                + " entities=" + spawnedEntities + "/" + entityTypes.size()
                + " itemChests=" + filledChests + " items=" + items.size();
        BloodyHell.LOGGER.info(summary);
        server.getPlayerList().broadcastSystemMessage(Component.literal(summary), false);
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

    private static void clearArea(ServerLevel level, BlockPos start, int width, int depth) {
        BlockState air = Blocks.AIR.defaultBlockState();
        BlockState floor = Blocks.SMOOTH_STONE.defaultBlockState();
        for (int x = 0; x < width; x++) {
            for (int z = 0; z < depth; z++) {
                for (int y = 0; y < CLEAR_HEIGHT; y++) {
                    level.setBlock(start.offset(x, y, z), air, Block.UPDATE_CLIENTS);
                }
                level.setBlock(start.offset(x, -1, z), floor, Block.UPDATE_CLIENTS);
            }
        }
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
