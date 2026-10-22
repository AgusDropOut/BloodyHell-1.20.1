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
        this.noPhysics = true;
    }

    public BloodNovaDebrisEntity(Level worldIn, BlockState blockState, int duration) {
        super(ModEntityTypes.BLOOD_NOVA_DEBRIS_ENTITY.get(), worldIn);
        setBlock(blockState);
        setDuration(duration);
        this.noPhysics = true;
    }

    @Override
    public boolean isNoGravity() {
        return true;
    }

    @Override
    public void tick() {

        this.xo = this.getX();
        this.yo = this.getY();
        this.zo = this.getZ();

        this.baseTick();

        if (getMode() == EnumFallingBlockMode.MOBILE) {

            this.move(MoverType.SELF, this.getDeltaMovement());

            this.setDeltaMovement(this.getDeltaMovement().scale(0.98));

            if (this.tickCount > getDuration()) {
                this.discard();
            }
        }
        else {

            float animVY = getAnimVY();
            this.prevAnimY = this.animY;
            this.animY += animVY;
            this.setAnimVY(animVY - 0.1f);
            if (this.animY < -0.5) this.discard();
        }
    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }
}