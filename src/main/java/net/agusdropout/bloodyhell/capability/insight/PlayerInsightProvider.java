package net.agusdropout.bloodyhell.capability.insight;

import net.agusdropout.bloodyhell.capability.crimsonveilPower.PlayerCrimsonVeil;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PlayerInsightProvider implements ICapabilityProvider, INBTSerializable<CompoundTag> {

        public static Capability<PlayerInsight> PLAYER_INSIGHT = CapabilityManager.get(new CapabilityToken<PlayerInsight>() {
        });

        private PlayerInsight insight = null;
        private final LazyOptional<PlayerInsight> optional = LazyOptional.of(this::createPlayerInsight);

        private PlayerInsight createPlayerInsight() {
            if (this.insight == null) {
                this.insight = new PlayerInsight();
            }

            return this.insight;
        }

        @Override
        public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
            if (cap == PLAYER_INSIGHT) {
                return optional.cast();
            }

            return LazyOptional.empty();
        }

        @Override
        public CompoundTag serializeNBT() {
            CompoundTag nbt = new CompoundTag();
            createPlayerInsight().saveNBTData(nbt);
            return nbt;
        }

        @Override
        public void deserializeNBT(CompoundTag nbt) {
            createPlayerInsight().loadNBTData(nbt);
        }

}
