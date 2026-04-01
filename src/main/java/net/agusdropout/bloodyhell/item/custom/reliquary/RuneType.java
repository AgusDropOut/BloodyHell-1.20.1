package net.agusdropout.bloodyhell.item.custom.reliquary;

import net.agusdropout.bloodyhell.entity.ModEntityTypes;
import net.agusdropout.bloodyhell.entity.minions.base.AbstractMinionEntity;
import net.agusdropout.bloodyhell.item.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;

import java.util.function.Supplier;

public enum RuneType {

    RESTLESS_SLUMBER(
            ModItems.MARK_OF_THE_RESTLESS_SLUMBER,
            10,
            (level, player, pos) -> {
                return ModEntityTypes.FAILED_SON_OF_THE_UNKNOWN.get().spawn(level, pos, MobSpawnType.MOB_SUMMONED);
            }
    ),
    RAVENOUS_GAZE(
            ModItems.RUNE_OF_THE_RAVENOUS_GAZE,
            25,
            (level, player, pos) -> {
                return ModEntityTypes.WEEPING_OCULAR.get().spawn(level, pos, MobSpawnType.MOB_SUMMONED);
            }
    ),
    HOLLOW_BULWARK(
            ModItems.SEAL_OF_THE_HOLLOW_BULWARK,
            35,
                    (level, player, pos) -> {
            return ModEntityTypes.BASTION_OF_THE_UNKNOWN.get().spawn(level, pos, MobSpawnType.MOB_SUMMONED);
            }
    ),
    CRUSHING_TOLL(
            ModItems.OMEN_OF_THE_CRUSHING_TOLL,
            40,
                    (level, player, pos) -> {
            return ModEntityTypes.BURDEN_OF_THE_UNKNOWN.get().spawn(level, pos, MobSpawnType.MOB_SUMMONED);
            }
    );


    private final Supplier<Item> itemSupplier;
    private final int capacityCost;
    private final RuneSummonAction summonAction;

    RuneType(Supplier<Item> itemSupplier, int capacityCost, RuneSummonAction summonAction) {
        this.itemSupplier = itemSupplier;
        this.capacityCost = capacityCost;
        this.summonAction = summonAction;
    }

    public Item getItem() {
        return this.itemSupplier.get();
    }

    public int getCapacityCost() {
        return this.capacityCost;
    }

    public AbstractMinionEntity executeSummon(ServerLevel level, Player player, BlockPos pos) {
        return this.summonAction.summon(level, player, pos);
    }

    /* Retrieves the corresponding RuneType for a given Item instance. Returns null if no match is found. */
    public static RuneType getByItem(Item item) {
        for (RuneType type : values()) {
            if (type.getItem().equals(item)) {
                return type;
            }
        }
        return null;
    }

    /* Functional interface to handle specific entity spawning logic per rune. */
    public interface RuneSummonAction {
        AbstractMinionEntity summon(ServerLevel level, Player player, BlockPos pos);
    }
}