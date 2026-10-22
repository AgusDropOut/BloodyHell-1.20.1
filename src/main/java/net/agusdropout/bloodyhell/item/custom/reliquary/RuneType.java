package net.agusdropout.bloodyhell.item.custom.reliquary;

import net.agusdropout.bloodyhell.item.registry.ReliquaryItems;
import net.agusdropout.bloodyhell.entity.ModEntityTypes;
import net.agusdropout.bloodyhell.entity.minions.base.AbstractMinionEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;

import java.util.function.Supplier;

public enum RuneType {

    RESTLESS_SLUMBER(
            ReliquaryItems.MARK_OF_THE_RESTLESS_SLUMBER,
            5,
            0,
            (level, player, pos) -> {
                return ModEntityTypes.FAILED_SON_OF_THE_UNKNOWN.get().spawn(level, pos, MobSpawnType.MOB_SUMMONED);
            }
    ),
    RAVENOUS_GAZE(
            ReliquaryItems.RUNE_OF_THE_RAVENOUS_GAZE,
            10,
            0,
            (level, player, pos) -> {
                return ModEntityTypes.WEEPING_OCULAR.get().spawn(level, pos, MobSpawnType.MOB_SUMMONED);
            }
    ),
    HOLLOW_BULWARK(
            ReliquaryItems.SEAL_OF_THE_HOLLOW_BULWARK,
            15,
            51,
            (level, player, pos) -> {
                return ModEntityTypes.BASTION_OF_THE_UNKNOWN.get().spawn(level, pos, MobSpawnType.MOB_SUMMONED);
            }
    ),
    CRUSHING_TOLL(
            ReliquaryItems.OMEN_OF_THE_CRUSHING_TOLL,
            20,
            51,
            (level, player, pos) -> {
                return ModEntityTypes.BURDEN_OF_THE_UNKNOWN.get().spawn(level, pos, MobSpawnType.MOB_SUMMONED);
            }
    );

    private final Supplier<Item> itemSupplier;
    private final int capacityCost;
    private final int requiredInsight;
    private final RuneSummonAction summonAction;

    RuneType(Supplier<Item> itemSupplier, int capacityCost, int requiredInsight, RuneSummonAction summonAction) {
        this.itemSupplier = itemSupplier;
        this.capacityCost = capacityCost;
        this.requiredInsight = requiredInsight;
        this.summonAction = summonAction;
    }

    public Item getItem() {
        return this.itemSupplier.get();
    }

    public int getCapacityCost() {
        return this.capacityCost;
    }

    public int getRequiredInsight() {
        return this.requiredInsight;
    }

    public AbstractMinionEntity executeSummon(ServerLevel level, Player player, BlockPos pos) {
        return this.summonAction.summon(level, player, pos);
    }

    public static RuneType getByItem(Item item) {
        for (RuneType type : values()) {
            if (type.getItem().equals(item)) {
                return type;
            }
        }
        return null;
    }

    // Keeping your typo version intact to avoid breaking your other classes calling it!
    public int getCaoapacityCost() {
        return this.capacityCost;
    }

    public interface RuneSummonAction {
        AbstractMinionEntity summon(ServerLevel level, Player player, BlockPos pos);
    }
}