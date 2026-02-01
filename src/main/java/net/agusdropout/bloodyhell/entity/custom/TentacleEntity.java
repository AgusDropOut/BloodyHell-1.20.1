package net.agusdropout.bloodyhell.entity.custom;

import net.agusdropout.bloodyhell.block.custom.BloodAltarBlock;
import net.agusdropout.bloodyhell.block.entity.custom.BloodAltarBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.AnimationState;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

public class TentacleEntity extends Entity {

    // --- TIEMPOS ---
    public static final int GRAB_TIME = 45;
    public static final int GIVE_TIME = 85;
    private static final int DEATH_TIME = 120;

    // ----------------------------------------------

    private static final EntityDataAccessor<ItemStack> ITEM_1 = SynchedEntityData.defineId(TentacleEntity.class, EntityDataSerializers.ITEM_STACK);
    private static final EntityDataAccessor<ItemStack> ITEM_2 = SynchedEntityData.defineId(TentacleEntity.class, EntityDataSerializers.ITEM_STACK);
    private static final EntityDataAccessor<ItemStack> ITEM_3 = SynchedEntityData.defineId(TentacleEntity.class, EntityDataSerializers.ITEM_STACK);
    private static final EntityDataAccessor<Optional<BlockPos>> TARGET_ALTAR_POS = SynchedEntityData.defineId(TentacleEntity.class, EntityDataSerializers.OPTIONAL_BLOCK_POS);
    private static final EntityDataAccessor<Integer> LIFE_TICKS = SynchedEntityData.defineId(TentacleEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Float> RANDOM_SEED = SynchedEntityData.defineId(TentacleEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Boolean> IS_GIVER = SynchedEntityData.defineId(TentacleEntity.class, EntityDataSerializers.BOOLEAN);

    public final AnimationState grabAnimationState = new AnimationState();

    public float clientAge;
    public float prevClientAge;

    private int initialDelay = 0;
    private Player summoner;
    private boolean hasPlayedSpawnSound = false; // Para que suene al aparecer

    public TentacleEntity(EntityType<?> entityType, Level level) {
        super(entityType, level);
        this.noPhysics = true;
        this.setNoGravity(true);
    }

    @Override
    protected void defineSynchedData() {
        this.entityData.define(ITEM_1, ItemStack.EMPTY);
        this.entityData.define(ITEM_2, ItemStack.EMPTY);
        this.entityData.define(ITEM_3, ItemStack.EMPTY);
        this.entityData.define(TARGET_ALTAR_POS, Optional.empty());
        this.entityData.define(LIFE_TICKS, 0);
        this.entityData.define(RANDOM_SEED, new Random().nextFloat() * 1000f);
        this.entityData.define(IS_GIVER, false);
    }

    // Getters y Setters
    public float getRandomSeed() { return this.entityData.get(RANDOM_SEED); }
    public void setInitialDelay(int ticks) { this.initialDelay = ticks; }
    public void setTargetAltar(BlockPos pos) { this.entityData.set(TARGET_ALTAR_POS, Optional.ofNullable(pos)); }
    public BlockPos getTargetAltar() { return this.entityData.get(TARGET_ALTAR_POS).orElse(null); }
    public void setSummoner(Player player) { this.summoner = player; }
    public int getInitialDelay() { return initialDelay; }
    public void setGiver(boolean isGiver) { this.entityData.set(IS_GIVER, isGiver); }
    public boolean isGiver() { return this.entityData.get(IS_GIVER); }

    public void setRewardItem(ItemStack stack) {
        this.setGiver(true);
        this.entityData.set(ITEM_1, stack);
    }

    @Override
    public void tick() {
        super.tick();

        // --- EFECTOS AMBIENTALES (CLIENTE & SERVIDOR) ---
        // 1. Partículas de oscuridad goteando
        if (this.level().isClientSide && this.entityData.get(LIFE_TICKS) > 0) {
            if (random.nextInt(3) == 0) {
                // Generar partículas a lo largo de una línea imaginaria hacia el objetivo
                // (Simulación barata de que el cuerpo emite humo)
                this.level().addParticle(ParticleTypes.SQUID_INK,
                        this.getX() + (random.nextDouble() - 0.5),
                        this.getY() + 1.0 + (random.nextDouble() * 2),
                        this.getZ() + (random.nextDouble() - 0.5),
                        0, -0.05, 0);
            }
        }

        // CLIENTE
        if (this.level().isClientSide) {
            this.prevClientAge = this.clientAge;
            if (this.entityData.get(LIFE_TICKS) > 0) {
                this.clientAge += 1.0f;
            }
            if (this.clientAge >= 25 && !grabAnimationState.isStarted()) {
                grabAnimationState.start(this.tickCount);
            }
        }
        // SERVIDOR
        else {
            this.setDeltaMovement(Vec3.ZERO);

            if (this.initialDelay > 0) {
                this.initialDelay--;
                return;
            }

            // SONIDO DE APARICIÓN (Solo una vez cuando empieza a vivir)
            if (!hasPlayedSpawnSound) {
                // Sonido de WARDEN EMERGE pero más agudo y rápido (desgarro)
                this.level().playSound(null, this.blockPosition(), SoundEvents.WARDEN_EMERGE, SoundSource.HOSTILE, 1.0f, 1.5f);
                hasPlayedSpawnSound = true;
            }

            // SONIDO AMBIENTAL (Latido/Susurro)
            if (this.tickCount % 20 == 0 && random.nextInt(3) == 0) {
                // Click de Sculk (sensación insectoide/alienígena)
                this.level().playSound(null, this.blockPosition(), SoundEvents.SCULK_CLICKING, SoundSource.HOSTILE, 0.5f, 0.5f);
            }

            if (isGiver() && summoner != null) {
                this.setTargetAltar(summoner.blockPosition().above());
            }

            int currentLife = this.entityData.get(LIFE_TICKS);
            this.entityData.set(LIFE_TICKS, currentLife + 1);

            // LOGICA DIFERENCIADA
            if (!isGiver() && currentLife == GRAB_TIME) {
                performGrab();
            }

            if (isGiver() && currentLife == GIVE_TIME) {
                performGive();
            }

            if (currentLife >= DEATH_TIME) {
                // Sonido de desaparecer (glitch/magia oscura)
                this.level().playSound(null, this.blockPosition(), SoundEvents.SCULK_CATALYST_BLOOM, SoundSource.HOSTILE, 1.0f, 0.8f);
                this.discard();
            }
        }
    }

    private void performGive() {
        ItemStack reward = this.entityData.get(ITEM_1);
        if (!reward.isEmpty()) {
            BlockPos targetPos = getTargetAltar();
            double spawnX, spawnY, spawnZ;

            if (targetPos != null) {
                spawnX = targetPos.getX() + 0.5;
                spawnY = targetPos.getY() - 0.5;
                spawnZ = targetPos.getZ() + 0.5;
            } else {
                spawnX = this.getX(); spawnY = this.getY(); spawnZ = this.getZ();
            }

            ItemEntity itemEntity = new ItemEntity(this.level(), spawnX, spawnY, spawnZ, reward);
            itemEntity.setDeltaMovement(0, 0.1, 0);

            this.level().addFreshEntity(itemEntity);

            // SONIDO DE RECOMPENSA DARK:
            // Un sonido místico pero oscuro (Respawn Anchor o Beacon Ambient)
            this.level().playSound(null, new BlockPos((int)spawnX, (int)spawnY, (int)spawnZ), SoundEvents.ZOMBIE_VILLAGER_CONVERTED, SoundSource.PLAYERS, 1.0f, 1.2f);
            this.level().playSound(null, new BlockPos((int)spawnX, (int)spawnY, (int)spawnZ), SoundEvents.AMETHYST_BLOCK_CHIME, SoundSource.PLAYERS, 2.0f, 0.5f);

            this.entityData.set(ITEM_1, ItemStack.EMPTY);
        }
    }

    private void performGrab() {
        BlockPos target = getTargetAltar();
        if (target == null) return;
        if (level().getBlockState(target).getBlock() instanceof BloodAltarBlock) {

            // Partículas Eldritch al impactar
            spawnEldritchImpactParticles(target);

            if (level().getBlockEntity(target) instanceof BloodAltarBlockEntity tile) {
                List<Item> items = tile.getItemsInside();
                if (!items.isEmpty()) {

                    // SONIDOS DE IMPACTO HORROR:
                    // 1. Grito del Shrieker (Susto)
                    this.level().playSound(null, target, SoundEvents.SCULK_SHRIEKER_SHRIEK, SoundSource.HOSTILE, 1.0f, 1.2f);
                    // 2. Ruptura húmeda (Sculk Break) en vez de piedra
                    this.level().playSound(null, target, SoundEvents.SCULK_BLOCK_BREAK, SoundSource.HOSTILE, 1.0f, 0.8f);

                    if (items.size() > 0) this.entityData.set(ITEM_1, new ItemStack(items.get(0)));
                    if (items.size() > 1) this.entityData.set(ITEM_2, new ItemStack(items.get(1)));
                    if (items.size() > 2) this.entityData.set(ITEM_3, new ItemStack(items.get(2)));
                    tile.clearItemsInside();
                }
            }
        }
    }

    // Nuevo método de partículas para el impacto
    private void spawnEldritchImpactParticles(BlockPos pos) {
        if (level() instanceof ServerLevel serverLevel) {
            BlockState state = level().getBlockState(pos);

            // 1. Debris del bloque (Base física)
            serverLevel.sendParticles(new BlockParticleOption(ParticleTypes.BLOCK, state),
                    pos.getX() + 0.5, pos.getY() + 1.2, pos.getZ() + 0.5,
                    20, 0.3, 0.3, 0.3, 0.15);

            // 2. Almas escapando (SCULK_SOUL)
            serverLevel.sendParticles(ParticleTypes.SCULK_SOUL,
                    pos.getX() + 0.5, pos.getY() + 1.2, pos.getZ() + 0.5,
                    15, 0.2, 0.2, 0.2, 0.05);

            // 3. Onda Expansiva oscura (SONIC_BOOM o SQUID_INK rápido)
            serverLevel.sendParticles(ParticleTypes.SONIC_BOOM,
                    pos.getX() + 0.5, pos.getY() + 1.5, pos.getZ() + 0.5,
                    1, 0, 0, 0, 0); // 1 sola onda sónica queda brutal
        }
    }

    public float getLifeTicks(float partialTick) {
        return Mth.lerp(partialTick, prevClientAge, clientAge);
    }

    public List<ItemStack> getHeldVisualItems() {
        List<ItemStack> list = new ArrayList<>();
        if (!entityData.get(ITEM_1).isEmpty()) list.add(entityData.get(ITEM_1));
        if (!entityData.get(ITEM_2).isEmpty()) list.add(entityData.get(ITEM_2));
        if (!entityData.get(ITEM_3).isEmpty()) list.add(entityData.get(ITEM_3));
        return list;
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        this.entityData.set(LIFE_TICKS, tag.getInt("Age"));
        this.initialDelay = tag.getInt("Delay");
        this.entityData.set(RANDOM_SEED, tag.getFloat("Seed"));
        this.setGiver(tag.getBoolean("IsGiver"));
        if (tag.contains("Item1")) this.entityData.set(ITEM_1, ItemStack.of(tag.getCompound("Item1")));
        if (tag.contains("TargetPos")) this.setTargetAltar(NbtUtils.readBlockPos(tag.getCompound("TargetPos")));
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        tag.putInt("Age", this.entityData.get(LIFE_TICKS));
        tag.putInt("Delay", this.initialDelay);
        tag.putFloat("Seed", this.entityData.get(RANDOM_SEED));
        tag.putBoolean("IsGiver", isGiver());
        if (!this.entityData.get(ITEM_1).isEmpty()) tag.put("Item1", this.entityData.get(ITEM_1).save(new CompoundTag()));
        if (getTargetAltar() != null) tag.put("TargetPos", NbtUtils.writeBlockPos(getTargetAltar()));
    }

    @Override public boolean isPushable() { return false; }
    @Override public boolean isPushedByFluid() { return false; }
    @Override public Packet<ClientGamePacketListener> getAddEntityPacket() { return new ClientboundAddEntityPacket(this); }
}