package net.agusdropout.bloodyhell.entity.custom;

import net.agusdropout.bloodyhell.item.custom.BlasphemousTwinDaggerItem;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.Optional;
import java.util.UUID;

public class BlasphemousTwinDaggersCloneEntity extends PathfinderMob implements GeoEntity {

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    // Identificador del dueño
    private static final EntityDataAccessor<Optional<UUID>> OWNER_UUID =
            SynchedEntityData.defineId(BlasphemousTwinDaggersCloneEntity.class, EntityDataSerializers.OPTIONAL_UUID);

    // Variables de Estado de Animación
    private boolean isAttacking = false;
    private int attackTimer = 0;

    public BlasphemousTwinDaggersCloneEntity(EntityType<? extends PathfinderMob> type, Level level) {
        super(type, level);
        this.setNoGravity(true);
        this.noPhysics = true;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return PathfinderMob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 100.0D) // Mucha vida para que no muera por error
                .add(Attributes.MOVEMENT_SPEED, 0.0D);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(OWNER_UUID, Optional.empty());
    }

    // --- SETUP DEL DUEÑO ---
    public void setOwner(Player player) {
        this.entityData.set(OWNER_UUID, Optional.of(player.getUUID()));
        this.setPos(player.getX(), player.getY(), player.getZ());
    }

    public Player getOwner() {
        Optional<UUID> uuid = this.entityData.get(OWNER_UUID);
        return uuid.map(value -> this.level().getPlayerByUUID(value)).orElse(null);
    }

    // --- CICLO DE VIDA (TICK) ---
    @Override
    public void tick() {
        super.tick();

        Player owner = getOwner();

        // 1. REGLA DE VIDA: Si no hay dueño vivo o no tiene las dagas -> DESPAWN
        if (!this.level().isClientSide) {
            if (owner == null || !owner.isAlive() || !(owner.getMainHandItem().getItem() instanceof BlasphemousTwinDaggerItem)) {
                this.discard();
                return;
            }

            // Gestión del Temporizador de Ataque
            if (isAttacking) {
                attackTimer--;
                if (attackTimer <= 0) {
                    isAttacking = false; // El ataque terminó, volver a caminar/idle
                }
            }
        }

        // 2. MOVIMIENTO (Lado Cliente y Servidor para suavidad)
        if (owner != null) {
            // Copiar posición exacta
            this.setPos(owner.getX(), owner.getY(), owner.getZ());

            // Copiar rotaciones (Cuerpo y Cabeza)
            this.setYRot(owner.getYRot());
            this.setXRot(owner.getXRot());
            this.yBodyRot = owner.yBodyRot;
            this.yHeadRot = owner.yHeadRot;
            this.yHeadRotO = owner.yHeadRotO;
            this.yBodyRotO = owner.yBodyRotO;
        }
    }

    // --- ACTIVADOR DE ATAQUE (Llamado desde el Item) ---
    public void triggerAttack(String animName, int durationTicks) {
        if (!this.level().isClientSide) {
            this.isAttacking = true;
            this.attackTimer = durationTicks;
            // Forzamos la señal a GeckoLib para que todos los clientes la vean
            this.triggerAnim("controller", animName);
        }
    }

    // --- MÁQUINA DE ESTADOS (ANIMACIONES) ---
    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "controller", 5, state -> {

                    // A. PRIORIDAD ABSOLUTA: ATAQUE
                    // Si el timer dice que estamos atacando, no interrumpir con caminar
                    if (this.isAttacking) {
                        return PlayState.CONTINUE; // Dejamos que el trigger original siga su curso
                    }

                    // B. MOVIMIENTO (WALK / RUN / IDLE)
                    Player owner = getOwner();
                    if (owner != null) {
                        // Cálculo de velocidad real (Pitágoras)
                        double dx = owner.getX() - owner.xo;
                        double dz = owner.getZ() - owner.zo;
                        double speed = Math.sqrt(dx * dx + dz * dz);

                        // Umbral mínimo para considerar que se mueve (evita temblequeo)
                        if (speed > 0.005) {

                            // AJUSTE DINÁMICO DE VELOCIDAD
                            // Multiplicador 3.5: Ajusta este número si ves que patina
                            state.getController().setAnimationSpeed((float)(speed * 3.5));

                            // Decisión: ¿Correr o Caminar?
                            if (speed > 0.22) { // Umbral de sprint
                                return state.setAndContinue(RawAnimation.begin().thenLoop("run"));
                            } else {
                                return state.setAndContinue(RawAnimation.begin().thenLoop("walk"));
                            }
                        }
                    }

                    // C. ESTADO POR DEFECTO: IDLE
                    // Reseteamos la velocidad a normal para que el Idle no se vea lento/rápido
                    state.getController().setAnimationSpeed(1.0f);
                    return state.setAndContinue(RawAnimation.begin().thenLoop("idle"));
                })
                        // Registramos los triggers para que triggerAnim funcione
                        .triggerableAnim("attack", RawAnimation.begin().thenPlay("attack"))
                        .triggerableAnim("special_attack", RawAnimation.begin().thenPlay("special_attack"))
        );
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    // --- GUARDADO DE DATOS (NBT) ---
    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        if (this.entityData.get(OWNER_UUID).isPresent()) {
            tag.putUUID("Owner", this.entityData.get(OWNER_UUID).get());
        }
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        if (tag.hasUUID("Owner")) {
            this.entityData.set(OWNER_UUID, Optional.of(tag.getUUID("Owner")));
        }
    }
}