package net.agusdropout.bloodyhell.entity.base;

import net.agusdropout.bloodyhell.client.data.ClientInsightData;
import net.minecraft.world.level.Level;

public interface InsightEntity {
    /**
     * @return The minimum insight required for the player to see this entity normally.
     */
    float getMinimumInsight();

    Level level();

    default boolean hasSufficientClientInsight() {
        return !level().isClientSide || ClientInsightData.canSee(this);
    }
}
