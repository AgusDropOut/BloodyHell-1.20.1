package net.agusdropout.bloodyhell.block.entity.custom;

import net.agusdropout.bloodyhell.block.entity.ModBlockEntities;
import net.agusdropout.bloodyhell.event.handlers.RitualAmbienceHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.Containers;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.*;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;
import software.bernie.geckolib.util.RenderUtils;

public class MainBloodAltarBlockEntity extends BlockEntity implements GeoBlockEntity {

    private boolean active;
    public final AnimatableInstanceCache animatableInstanceCache = GeckoLibUtil.createInstanceCache(this);

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

    public MainBloodAltarBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(ModBlockEntities.MAIN_BLOOD_ALTAR.get(), blockPos, blockState);
    }

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

    // --- SINCRONIZACIÓN DE DATOS (IMPORTANTE) ---

    @Override
    protected void saveAdditional(CompoundTag nbt) {
        super.saveAdditional(nbt); // Llamar a super es buena práctica
        nbt.put("inventory", itemHandler.serializeNBT());
        nbt.putBoolean("active", active); // Guardar estado activo
    }

    @Override
    public void load(CompoundTag nbt) {
        super.load(nbt);
        itemHandler.deserializeNBT(nbt.getCompound("inventory"));
        this.active = nbt.getBoolean("active"); // Cargar estado activo
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

    // --- HACK: RECEPCIÓN DEL EVENTO DEL BLOQUE ---
    // Esto recibe la señal enviada por level.blockEvent desde el Bloque
    @Override
    public boolean triggerEvent(int id, int type) {
        if (id == 1) { // ID 1 = Ritual Completado (Ambience)
            if (this.level.isClientSide) {
                // Solo el cliente ejecuta esto visual/sonoro
                RitualAmbienceHandler.triggerRitual(160);
            }
            return true;
        }
        return super.triggerEvent(id, type);
    }

    // --- LÓGICA DE INVENTARIO ---

    public void drops() {
        SimpleContainer inventory = new SimpleContainer(itemHandler.getSlots());
        for (int i = 0; i < itemHandler.getSlots(); i++) {
            inventory.setItem(i, itemHandler.getStackInSlot(i));
        }
        Containers.dropContents(this.level, this.worldPosition, inventory);
    }

    public ItemStackHandler getItemHandler() {
        return itemHandler;
    }

    public boolean isActive() {
        return active;
    }

    // Setter mejorado para sincronizar automáticamente
    public void setActive(boolean active) {
        this.active = active;
        setChanged(); // Marcar que hay cambios para guardar en disco

        // Sincronizar con clientes inmediatamente
        if (level != null && !level.isClientSide) {
            level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
        }
    }

    // --- GECKOLIB ---

    private <T extends GeoBlockEntity> PlayState predicate(AnimationState<T> tAnimationState) {
        // Usa la variable 'active' que ahora está sincronizada correctamente
        if(isActive()) {
            tAnimationState.getController().setAnimation(RawAnimation.begin().then("animation.model.active", Animation.LoopType.LOOP));
        } else {
            tAnimationState.getController().setAnimation(RawAnimation.begin().then("animation.model.idle", Animation.LoopType.LOOP));
        }
        return PlayState.CONTINUE;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "controller", 0, this::predicate));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return animatableInstanceCache;
    }

    @Override
    public double getTick(Object blockEntity) {
        return RenderUtils.getCurrentTick();
    }
}