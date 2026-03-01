package net.agusdropout.bloodyhell.capability.crimsonveilPower;

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



    public class PlayerCrimsonveilProvider implements ICapabilityProvider, INBTSerializable<CompoundTag> {
        public static Capability<PlayerCrimsonVeil> PLAYER_CRIMSONVEIL = CapabilityManager.get(new CapabilityToken<PlayerCrimsonVeil>() {
        });

        private PlayerCrimsonVeil crimsonVeil = null;
        private final LazyOptional<PlayerCrimsonVeil> optional = LazyOptional.of(this::createPlayerCrimsonVeil);

        private PlayerCrimsonVeil createPlayerCrimsonVeil() {
            if (this.crimsonVeil == null) {
                this.crimsonVeil = new PlayerCrimsonVeil();
            }

            return this.crimsonVeil;
        }

        @Override
        public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
            if (cap == PLAYER_CRIMSONVEIL) {
                return optional.cast();
            }

            return LazyOptional.empty();
        }

        @Override
        public CompoundTag serializeNBT() {
            CompoundTag nbt = new CompoundTag();
            createPlayerCrimsonVeil().saveNBTData(nbt);
            return nbt;
        }

        @Override
        public void deserializeNBT(CompoundTag nbt) {
            createPlayerCrimsonVeil().loadNBTData(nbt);
        }
    }