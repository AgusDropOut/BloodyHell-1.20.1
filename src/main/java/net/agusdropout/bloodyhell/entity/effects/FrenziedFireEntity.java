package net.agusdropout.bloodyhell.entity.effects;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkHooks;

public class FrenziedFireEntity extends Entity {

    private int lifespan = 100;
    private int age = 0;

    public FrenziedFireEntity(EntityType<?> type, Level level) {
        super(type, level);
    }

    @Override
    protected void defineSynchedData() {
    }

    @Override
    public void tick() {
        super.tick();

        this.age++;
        if (!this.level().isClientSide && this.age >= this.lifespan) {
            this.discard();
        }

        if (!this.level().isClientSide) {
            /* Area of effect damage logic is executed here */
        }
    }

    public float getLifeProgress() {
        return (float) this.age / (float) this.lifespan;
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        this.age = tag.getInt("Age");
        this.lifespan = tag.getInt("Lifespan");
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        tag.putInt("Age", this.age);
        tag.putInt("Lifespan", this.lifespan);
    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }
}