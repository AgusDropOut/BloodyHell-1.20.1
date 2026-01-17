package net.agusdropout.bloodyhell.entity.effects;

import net.agusdropout.bloodyhell.entity.ModEntityTypes;
import net.agusdropout.bloodyhell.entity.effects.EntityFallingBlock;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.network.NetworkHooks;

public class BloodNovaDebrisEntity extends EntityFallingBlock {

    public BloodNovaDebrisEntity(EntityType<?> entityTypeIn, Level worldIn) {
        super(entityTypeIn, worldIn);
        this.noPhysics = true; // 1. Disable wall collision checks (smoother)
    }

    public BloodNovaDebrisEntity(Level worldIn, BlockState blockState, int duration) {
        super(ModEntityTypes.BLOOD_NOVA_DEBRIS_ENTITY.get(), worldIn);
        setBlock(blockState);
        setDuration(duration);
        this.noPhysics = true;
    }

    @Override
    public boolean isNoGravity() {
        return true; // 2. CRITICAL FIX: Stops client from predicting gravity
    }

    @Override
    public void tick() {
        // 3. Sync Previous Position (Vital for interpolation)
        this.xo = this.getX();
        this.yo = this.getY();
        this.zo = this.getZ();

        // Standard base tick (timers, portals)
        this.baseTick();

        if (getMode() == EnumFallingBlockMode.MOBILE) {
            // Apply the velocity set by the BloodNovaEntity
            this.move(MoverType.SELF, this.getDeltaMovement());

            // Drag (keeps movement controlled)
            this.setDeltaMovement(this.getDeltaMovement().scale(0.98));

            if (this.tickCount > getDuration()) {
                this.discard();
            }
        }
        else {
            // Fallback for popup animation mode
            float animVY = getAnimVY();
            this.prevAnimY = this.animY;
            this.animY += animVY;
            this.setAnimVY(animVY - 0.1f);
            if (this.animY < -0.5) this.discard();
        }
    }

    // 4. Packet Syncing: Ensures custom entity data reaches the client
    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }
}