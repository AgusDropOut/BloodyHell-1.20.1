package net.agusdropout.bloodyhell.block.entity;

import net.agusdropout.bloodyhell.block.custom.TombBlock;

import net.agusdropout.bloodyhell.entity.ModEntityTypes;
import net.agusdropout.bloodyhell.entity.custom.GraveWalkerEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

import java.util.List;

public class TombBlockEntity extends BlockEntity {

    private boolean isRitualActive = false;
    private int timer = 0;
    private final int TIME_TO_SPAWN = 60; // 3 segundos
    private final double DETECTION_RADIUS = 5.0;

    public TombBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.TOMB_ENTITY.get(), pos, state);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, TombBlockEntity entity) {
        // 1. Validaciones básicas: Si es cliente o la tumba ya está abierta, salir.
        if (level.isClientSide || state.getValue(TombBlock.OPEN)) {
            return;
        }

        // 2. Lógica de Detección (Si no ha empezado el ritual)
        if (!entity.isRitualActive) {
            // Definir el área de detección
            AABB detectionBox = new AABB(pos).inflate(entity.DETECTION_RADIUS);

            // Buscamos jugadores con un FILTRO:
            // - Debe ser Player.class
            // - NO debe estar en Creativo (!p.isCreative())
            // - NO debe estar en Espectador (!p.isSpectator())
            List<Player> validPlayers = level.getEntitiesOfClass(Player.class, detectionBox,
                    player -> !player.isCreative() && !player.isSpectator());

            // Solo si encontramos jugadores válidos (Survival/Adventure), activamos la trampa
            if (!validPlayers.isEmpty()) {
                entity.startRitual();
            }
        }

        // 3. Lógica del Ritual (Si ya se activó)
        else {
            entity.timer++;

            // Efecto de sonido (tic-tac o latido) cada segundo
            if (entity.timer % 20 == 0) {
                level.playSound(null, pos, SoundEvents.STONE_HIT, SoundSource.BLOCKS, 1.0f, 0.5f);
            }

            // Al terminar el tiempo, spawnear
            if (entity.timer >= entity.TIME_TO_SPAWN) {
                entity.spawnGraveWalker(level, pos, state);
            }
        }
    }

    private void startRitual() {
        this.isRitualActive = true;
        this.timer = 0;
        // Sonido de activación ("Se ha despertado")
        level.playSound(null, worldPosition, SoundEvents.SCULK_SHRIEKER_SHRIEK, SoundSource.BLOCKS, 1.0f, 0.5f);
    }

    private void spawnGraveWalker(Level level, BlockPos pos, BlockState state) {
        // Obtenemos la dirección de la tumba
        Direction facing = state.getValue(TombBlock.FACING);

        // Calculamos posición: 1 bloque hacia adelante (StepX/StepZ) y al mismo nivel del suelo
        double spawnX = pos.getX() + 0.5 + facing.getStepX();
        double spawnY = pos.getY();
        double spawnZ = pos.getZ() + 0.5 + facing.getStepZ();

        GraveWalkerEntity boss = ModEntityTypes.GRAVE_WALKER_ENTITY.get().create(level);

        if (boss != null) {
            // Rotación: Convertimos la dirección (Norte/Sur/etc) a grados (YRot)
            float rotacion = facing.toYRot();

            // Ubicamos al mob mirando hacia el mismo lado que la tumba
            boss.moveTo(spawnX, spawnY, spawnZ, rotacion, 0.0F);
            boss.setYBodyRot(rotacion);
            boss.setYHeadRot(rotacion);

            level.addFreshEntity(boss);

            // Abrimos la tumba visualmente
            level.setBlock(pos, state.setValue(TombBlock.OPEN, true), 3);

            // Sonido de aparición final
            level.playSound(null, pos, SoundEvents.ZOMBIE_VILLAGER_CONVERTED, SoundSource.HOSTILE, 1.0f, 1.0f);
        }

        // Apagamos el ritual para detener el tick
        this.isRitualActive = false;
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.putBoolean("active", isRitualActive);
        tag.putInt("timer", timer);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        isRitualActive = tag.getBoolean("active");
        timer = tag.getInt("timer");
    }
}