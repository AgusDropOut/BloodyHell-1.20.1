package net.agusdropout.bloodyhell.block.entity;

import net.agusdropout.bloodyhell.networking.ModMessages;
import net.agusdropout.bloodyhell.networking.packet.EnergySyncS2CPacket;
import net.agusdropout.bloodyhell.util.ModEnergyStorage;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.Containers;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.*;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;
import software.bernie.geckolib.util.RenderUtils;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.UUID;

public class MainBloodAltarBlockEntity extends BlockEntity implements GeoBlockEntity {

    private boolean active;
    public final AnimatableInstanceCache animatableInstanceCache = GeckoLibUtil.createInstanceCache(this);

    // --- LÓGICA DE SPAWNEO SECUENCIAL ---
    private final Queue<BlockPos> pendingTentacles = new LinkedList<>();
    private int spawnCooldown = 0;
    private UUID summonerUUID; // Guardamos el UUID para persistencia básica
    private Player cachedSummoner;

    public MainBloodAltarBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(ModBlockEntities.MAIN_BLOOD_ALTAR.get(), blockPos, blockState);
    }

    private final ItemStackHandler itemHandler = new ItemStackHandler(3) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
            if(!level.isClientSide()) {
                level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
            }
        }
    };
    private LazyOptional<IItemHandler> lazyItemHandler = LazyOptional.empty();

    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
        if (cap == ForgeCapabilities.ITEM_HANDLER) {
            return lazyItemHandler.cast();
        }
        return super.getCapability(cap, side);
    }

    @Override
    public void onLoad() {
        super.onLoad();
        lazyItemHandler = LazyOptional.of(() -> itemHandler);
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        lazyItemHandler.invalidate();
    }

    @Override
    protected void saveAdditional(CompoundTag nbt) {
        nbt.put("inventory", itemHandler.serializeNBT());
        super.saveAdditional(nbt);
    }

    @Override
    public void load(CompoundTag nbt) {
        super.load(nbt);
        itemHandler.deserializeNBT(nbt.getCompound("inventory"));

    }

    public void drops() {
        SimpleContainer inventory = new SimpleContainer(itemHandler.getSlots());
        for (int i = 0; i < itemHandler.getSlots(); i++) {
            inventory.setItem(i, itemHandler.getStackInSlot(i));
        }

        Containers.dropContents(this.level, this.worldPosition, inventory);
    }


    public void tick(Level level, BlockPos pos, BlockState state) {
        if (level.isClientSide()) {
            return;
        }
        setChanged(level, pos, state);
    }

    // Este método lo llamarás desde el Bloque cuando el ritual termine
    public void triggerTentacleSequence(List<BlockPos> targets, Player player) {
        this.pendingTentacles.clear();
        this.pendingTentacles.addAll(targets);
        this.summonerUUID = player.getUUID();
        this.cachedSummoner = player;
        this.spawnCooldown = 0; // El primero sale instantáneo (o pon un delay si quieres)
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

    private <T extends GeoAnimatable> PlayState predicate(AnimationState<T> tAnimationState) {
        if(isActive()) {
            tAnimationState.getController().setAnimation(RawAnimation.begin().then("animation.model.active", Animation.LoopType.LOOP));
        } else {
            tAnimationState.getController().setAnimation(RawAnimation.begin().then("animation.model.idle", Animation.LoopType.LOOP));
        }
        return PlayState.CONTINUE;
    }


    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController(this, "controller",
                0, this::predicate));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return animatableInstanceCache;
    }

    @Override
    public double getTick(Object blockEntity) {
        return RenderUtils.getCurrentTick();
    }

    public ItemStackHandler getItemHandler() {
        return itemHandler;
    }
    public boolean isActive() {
        return active;
    }
    public void setActive(boolean active) {
        this.active = active;
    }
}
