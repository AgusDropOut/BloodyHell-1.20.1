package net.agusdropout.bloodyhell.block.entity.custom.mechanism;

import net.agusdropout.bloodyhell.block.custom.mechanism.RhnullBloodEnginePillarBlock;
import net.agusdropout.bloodyhell.block.entity.ModBlockEntities;
import net.agusdropout.bloodyhell.block.entity.base.BaseGeckoBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;
import software.bernie.geckolib.core.animation.AnimatableManager;

public class RhnullBloodEnginePillarBlockEntity extends BaseGeckoBlockEntity {

    private boolean isEngineActive = false;

    private final ItemStackHandler itemHandler = new ItemStackHandler(1) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
            if (level != null && !level.isClientSide) {
                level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
            }
        }
    };
    private final LazyOptional<IItemHandler> optionalItemHandler = LazyOptional.of(() -> itemHandler);

    public RhnullBloodEnginePillarBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.RHNULL_BLOOD_ENGINE_PILLAR.get(), pos, state);
    }

    public void tick(Level level, BlockPos pos, BlockState state) {
        if (level.isClientSide || !this.isEngineActive) return;


        if (level.getGameTime() % 3 == 0 && state.hasProperty(RhnullBloodEnginePillarBlock.CORNER)) {
            RhnullBloodEnginePillarBlock.Corner corner = state.getValue(RhnullBloodEnginePillarBlock.CORNER);

            double dirX = 0;
            double dirZ = 0;

            switch (corner) {
                case NORTH_WEST -> { dirX = 1.0; dirZ = 1.0; }
                case NORTH_EAST -> { dirX = -1.0; dirZ = 1.0; }
                case SOUTH_WEST -> { dirX = 1.0; dirZ = -1.0; }
                case SOUTH_EAST -> { dirX = -1.0; dirZ = -1.0; }
                default -> { }
            }

            if (dirX != 0 || dirZ != 0) {
                BlockEntity be = level.getBlockEntity(pos.offset((int) dirX, 0, (int) dirZ));
                if (be instanceof RhnullBloodEngineBlockEntity engine) {
                    Vector3f color = engine.getBloodBaseColor();

                    if (level instanceof ServerLevel serverLevel) {
                        double xPos = pos.getX() + 0.5;
                        double yPos = pos.getY() + (level.random.nextDouble() * 2.0);
                        double zPos = pos.getZ() + 0.5;

                        serverLevel.sendParticles(
                                new net.agusdropout.bloodyhell.particle.ParticleOptions.TinyBloomParticleOptions(color, 0.5f),
                                xPos, yPos, zPos,
                                0,
                                dirX, 0.0, dirZ,
                                0.05D
                        );
                    }
                }
            }
        }
    }

    public void setEngineActive(boolean active) {
        if (this.isEngineActive != active) {
            this.isEngineActive = active;
            setChanged();
            if (level != null && !level.isClientSide) {
                level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
            }
        }
    }

    public boolean isEngineActive() {
        return this.isEngineActive;
    }

    private void checkEngineState() {
        BlockPos[] offsets = {
                worldPosition.offset(1, 0, 1), worldPosition.offset(1, 0, -1),
                worldPosition.offset(-1, 0, 1), worldPosition.offset(-1, 0, -1)
        };
        for (BlockPos offsetPos : offsets) {
            if (level.getBlockEntity(offsetPos) instanceof RhnullBloodEngineBlockEntity engine) {
                this.isEngineActive = engine.isActive();
                level.sendBlockUpdated(this.worldPosition, this.getBlockState(), this.getBlockState(), 3);
                break;
            }
        }
    }

    public InteractionResult interact(Player player, InteractionHand hand) {
        ItemStack heldItem = player.getItemInHand(hand);
        ItemStack stackInSlot = itemHandler.getStackInSlot(0);

        if (heldItem.isEmpty()) {
            if (!stackInSlot.isEmpty()) {
                player.setItemInHand(hand, itemHandler.extractItem(0, 64, false));
                return InteractionResult.sidedSuccess(level.isClientSide);
            }
        } else {
            if (stackInSlot.isEmpty()) {
                ItemStack remainder = itemHandler.insertItem(0, heldItem.copy(), false);
                player.setItemInHand(hand, remainder);
                return InteractionResult.sidedSuccess(level.isClientSide);
            }
        }
        return InteractionResult.PASS;
    }

    public ItemStack getRenderStack() {
        return itemHandler.getStackInSlot(0);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
    }

    @Override
    public String getAssetPathName() {
        return "rhnull_blood_engine_pillar_block";
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        itemHandler.deserializeNBT(tag.getCompound("Inventory"));
        this.isEngineActive = tag.getBoolean("IsEngineActive");
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.put("Inventory", itemHandler.serializeNBT());
        tag.putBoolean("IsEngineActive", this.isEngineActive);
    }

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag() {
        return saveWithoutMetadata();
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == ForgeCapabilities.ITEM_HANDLER) {
            return optionalItemHandler.cast();
        }
        return super.getCapability(cap, side);
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        optionalItemHandler.invalidate();
    }

    @Override
    public AABB getRenderBoundingBox() {
        return new AABB(getBlockPos()).expandTowards(0, 2, 0);
    }

    @Override
    public void onLoad() {
        super.onLoad();
        if (level != null && !level.isClientSide) {
            checkEngineState();
        }
    }
}