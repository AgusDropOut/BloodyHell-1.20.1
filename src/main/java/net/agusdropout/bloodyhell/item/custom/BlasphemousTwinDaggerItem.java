package net.agusdropout.bloodyhell.item.custom;

// ... imports existentes ...
import net.agusdropout.bloodyhell.CrimsonveilPower.PlayerCrimsonveilProvider;
import net.agusdropout.bloodyhell.entity.projectile.SpecialSlashEntity;
import net.agusdropout.bloodyhell.item.client.BlasphemousTwinDaggersRenderer;
import net.agusdropout.bloodyhell.networking.ModMessages;
import net.agusdropout.bloodyhell.networking.packet.CrimsonVeilDataSyncS2CPacket;
import net.agusdropout.bloodyhell.sound.ModSounds;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ToolAction;
import net.minecraftforge.common.ToolActions;
import software.bernie.geckolib.constant.DataTickets;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animatable.SingletonGeoAnimatable;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

import dev.kosmx.playerAnim.api.layered.IAnimation;
import dev.kosmx.playerAnim.api.layered.KeyframeAnimationPlayer;
import dev.kosmx.playerAnim.api.layered.ModifierLayer;
import dev.kosmx.playerAnim.minecraftApi.PlayerAnimationAccess;
import dev.kosmx.playerAnim.minecraftApi.PlayerAnimationRegistry;
import net.agusdropout.bloodyhell.BloodyHell;

import java.util.List;
import java.util.function.Consumer;

public class BlasphemousTwinDaggerItem extends SwordItem implements GeoItem, IComboWeapon {

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    public static final float COMBO_1_BONUS = 0.0f;
    public static final float COMBO_2_BONUS = 2.0f;
    public static final float COMBO_3_BONUS = 4.0f;

    // Tags NBT para manejar el delay
    private static final String DAMAGE_DELAY_TAG = "DamageDelay";
    private static final String PENDING_COMBO_TAG = "PendingComboStep";
    private static final int ATTACK_DELAY_TICKS = 18; // 4 ticks = 0.2 segundos de retraso (ajusta según la animación)

    private static final RawAnimation ATTACK_1 = RawAnimation.begin().thenPlay("attack_1");
    private static final RawAnimation ATTACK_2 = RawAnimation.begin().thenPlay("attack_2");
    private static final RawAnimation ATTACK_3 = RawAnimation.begin().thenPlay("attack_3");
    private static final RawAnimation SPECIAL_ATK = RawAnimation.begin().thenPlay("special_attack").thenLoop("idle");
    private static final RawAnimation IDLE_ANIM = RawAnimation.begin().thenLoop("idle");

    public BlasphemousTwinDaggerItem(Tier tier, int attackDamageIn, float attackSpeedIn, Properties builder) {
        super(tier, attackDamageIn, attackSpeedIn, builder);
        SingletonGeoAnimatable.registerSyncedAnimatable(this);
    }

    @Override
    public boolean isPerspectiveAware() { return true; }

    @Override
    public boolean canPerformAction(ItemStack stack, ToolAction toolAction) {
        return toolAction != ToolActions.SWORD_SWEEP && super.canPerformAction(stack, toolAction);
    }

    @Override
    public float getComboDamageBonus(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        if (tag != null && tag.contains("CurrentCombo")) {
            int currentCombo = tag.getInt("CurrentCombo");
            return switch (currentCombo) {
                case 2 -> COMBO_2_BONUS;
                case 3 -> COMBO_3_BONUS;
                default -> 0.0f;
            };
        }
        return 0.0f;
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
        // Lógica de offhand existente
        if (isSelected && entity instanceof Player player) {
            ItemStack offhandStack = player.getOffhandItem();
            if (!offhandStack.isEmpty()) {
                if (!player.getInventory().add(offhandStack)) {
                    player.drop(offhandStack, true);
                }
                player.setItemInHand(InteractionHand.OFF_HAND, ItemStack.EMPTY);
            }

            // --- LÓGICA DE DELAY DE DAÑO (NUEVO) ---
            if (!level.isClientSide) {
                CompoundTag tag = stack.getOrCreateTag();
                if (tag.contains(DAMAGE_DELAY_TAG)) {
                    int delay = tag.getInt(DAMAGE_DELAY_TAG);
                    if (delay > 0) {
                        tag.putInt(DAMAGE_DELAY_TAG, delay - 1);
                    } else {
                        // El contador llegó a 0, ejecutamos el daño
                        int comboStep = tag.getInt(PENDING_COMBO_TAG);
                        executeAreaDamage(player, level, comboStep, stack);

                        // Limpiamos los tags
                        tag.remove(DAMAGE_DELAY_TAG);
                        tag.remove(PENDING_COMBO_TAG);
                    }
                }
            }
        }
    }

    // --- NUEVO MÉTODO: DAÑO EN ÁREA ---
    private void executeAreaDamage(Player player, Level level, int comboStep, ItemStack stack) {
        float baseDamage = this.getDamage();
        float bonus = 0;

        // Dimensiones del área de ataque
        double rangeForward;
        double width;

        if (comboStep == 3) {
            // Ataque 3: Estocada (Largo alcance, angosto)
            rangeForward = 3.5;
            width = 1.0;
            bonus = COMBO_3_BONUS;
            // Efecto visual extra para la estocada
            if (level instanceof ServerLevel serverLevel) {
                Vec3 look = player.getLookAngle();
                for (int i = 0; i < 5; i++) {
                    serverLevel.sendParticles(ParticleTypes.SWEEP_ATTACK,
                            player.getX() + look.x * i * 0.5,
                            player.getEyeY() + look.y * i * 0.5,
                            player.getZ() + look.z * i * 0.5,
                            1, 0.1, 0.1, 0.1, 0.0);
                }
            }
        } else {
            // Ataque 1 y 2: Barrido (Corto alcance, ancho)
            rangeForward = 2.0;
            width = 2.5;
            bonus = (comboStep == 2) ? COMBO_2_BONUS : COMBO_1_BONUS;
        }

        float totalDamage = baseDamage + bonus;

        // Vector de mirada y posición
        Vec3 look = player.getLookAngle();
        Vec3 origin = player.position().add(0, player.getEyeHeight() * 0.5, 0);

        // Calculamos el centro de la caja de daño un poco adelante del jugador
        Vec3 center = origin.add(look.scale(rangeForward * 0.5));

        // Creamos la caja (AABB)
        AABB damageBox = new AABB(center, center).inflate(width * 0.5, 1.0, rangeForward * 0.5);

        List<LivingEntity> targets = level.getEntitiesOfClass(LivingEntity.class, damageBox);

        for (LivingEntity target : targets) {
            if (target != player && !target.isAlliedTo(player) && target.isAlive()) {
                // Check opcional: Asegurar que esté "frente" al jugador (producto punto)
                Vec3 dirToTarget = target.position().subtract(player.position()).normalize();
                double dot = look.dot(dirToTarget);

                // Si el producto punto es positivo, está enfrente (> 0.5 es aprox 60 grados)
                if (dot > 0.3) {
                    target.hurt(level.damageSources().playerAttack(player), totalDamage);
                    // Empuje leve
                    target.knockback(0.4F, -look.x, -look.z);
                }
            }
        }
    }

    private static final String COMBO_TAG = "CurrentCombo";
    private static final String LAST_HIT_TIME_TAG = "LastHitTime";
    private static final long COMBO_RESET_TIME_MS = 2000;

    public int updateAndGetCombo(ItemStack stack) {
        CompoundTag tag = stack.getOrCreateTag();
        long lastTime = tag.getLong(LAST_HIT_TIME_TAG);
        int currentCombo = tag.getInt(COMBO_TAG);
        long currentTime = System.currentTimeMillis();

        if (currentTime - lastTime > COMBO_RESET_TIME_MS) {
            currentCombo = 0;
        }
        currentCombo++;
        if (currentCombo > 3) currentCombo = 1;

        tag.putInt(COMBO_TAG, currentCombo);
        tag.putLong(LAST_HIT_TIME_TAG, currentTime);
        return currentCombo;
    }

    private void playPlayerAnimatorAnim(Player player, String animName) {
        var animationLayer = (ModifierLayer<IAnimation>) PlayerAnimationAccess
                .getPlayerAssociatedData((AbstractClientPlayer) player)
                .get(new ResourceLocation(BloodyHell.MODID, "animation"));

        if (animationLayer != null) {
            var anim = PlayerAnimationRegistry.getAnimation(new ResourceLocation(BloodyHell.MODID, animName));
            if (anim != null) {
                animationLayer.setAnimation(new KeyframeAnimationPlayer(anim));
            }
        }
    }

    // --- CLICK IZQUIERDO (ATAQUE) ---
    @Override
    public boolean onEntitySwing(ItemStack stack, LivingEntity entity) {
        if (entity instanceof Player player) {
            if (player.getCooldowns().isOnCooldown(this)) return true;

            CompoundTag tag = stack.getOrCreateTag();
            if (tag.contains("LastSpecialAtk")) {
                long lastSpecialTime = tag.getLong("LastSpecialAtk");
                if (System.currentTimeMillis() - lastSpecialTime < 400) {
                    return true;
                }
            }

            // 1. Lógica Combo
            int comboStep = updateAndGetCombo(stack);

            int cooldownDuration = switch (comboStep) {
                case 1 -> 20;
                case 2 -> 15;
                case 3 -> 20;
                default -> 20;
            };
            player.getCooldowns().addCooldown(this, cooldownDuration);

            // --- INICIAR DELAY DE DAÑO (Servidor) ---
            if (!player.level().isClientSide) {
                // Establecemos el delay. Cuando inventoryTick lo baje a 0, se aplicará el daño.
                tag.putInt(DAMAGE_DELAY_TAG, ATTACK_DELAY_TICKS);
                tag.putInt(PENDING_COMBO_TAG, comboStep);

                // Sonido
                float volumen = 0.5f;
                float pitch = 0.9f + (player.getRandom().nextFloat() * 0.2f);
                player.level().playSound(null, player.getX(), player.getY(), player.getZ(),
                        this.getAttackSound(), net.minecraft.sounds.SoundSource.PLAYERS, volumen, pitch);
            }

            // 3. Animaciones (Cliente)
            if (player.level().isClientSide) {
                boolean isFirstPerson = Minecraft.getInstance().options.getCameraType().isFirstPerson();

                if (isFirstPerson) {
                    String animName = switch (comboStep) {
                        case 2 -> "attack_2_trigger";
                        case 3 -> "attack_3_trigger";
                        default -> "attack_1_trigger";
                    };
                    triggerAnim(player, GeoItem.getId(stack), "Controller", animName);
                } else {
                    triggerAnim(player, GeoItem.getId(stack), "Controller", "idle_trigger");
                    playPlayerAnimatorAnim(player, "dagger_attack_" + comboStep);
                }
            }
        }
        return true; // Cancelamos el golpe vanilla porque nosotros manejamos el daño
    }

    @Override
    public InteractionResultHolder<ItemStack> use(net.minecraft.world.level.Level level, Player player, InteractionHand hand) {
        // ... (Tu código de ataque especial se mantiene igual) ...
        // Nota: Si quieres aplicar delay al especial también, deberías usar una lógica similar
        // pero con un tag distinto (ej. SPECIAL_DAMAGE_DELAY), o simplemente instanciar
        // la entidad SpecialSlashEntity con un delay interno si lo soporta.

        if (hand == InteractionHand.MAIN_HAND) {
            player.getCapability(PlayerCrimsonveilProvider.PLAYER_CRIMSONVEIL).ifPresent(playerCrimsonVeil -> {
                if (playerCrimsonVeil.getCrimsonVeil() >= 5 && !player.getCooldowns().isOnCooldown(this)) {

                    player.getCooldowns().addCooldown(this, 50);

                    if (!level.isClientSide()) {
                        playerCrimsonVeil.subCrimsomveil(5);
                        ModMessages.sendToPlayer(new CrimsonVeilDataSyncS2CPacket(playerCrimsonVeil.getCrimsonVeil()), ((ServerPlayer) player));

                        float damage = 10.0f;
                        SpecialSlashEntity slash = new SpecialSlashEntity(level, player, damage);
                        level.addFreshEntity(slash);
                    }
                }
            });
            player.level().playSound(null, player.getX(), player.getY(), player.getZ(),
                    SoundEvents.BLAZE_SHOOT, net.minecraft.sounds.SoundSource.PLAYERS, 0.5f, 0.8f + (player.getRandom().nextFloat() * 0.4f));

            boolean isFirstPerson = Minecraft.getInstance().options.getCameraType().isFirstPerson();
            if (isFirstPerson) {
                triggerAnim(player, GeoItem.getId(player.getItemInHand(hand)), "Controller", "special_attack_trigger");
            } else {
                playPlayerAnimatorAnim(player, "dagger_special_attack");
                player.getItemInHand(hand).getOrCreateTag().putLong("LastSpecialAtk", System.currentTimeMillis());
                player.getItemInHand(hand).getOrCreateTag().putLong(LAST_HIT_TIME_TAG, System.currentTimeMillis());
            }
            return InteractionResultHolder.sidedSuccess(player.getItemInHand(hand), level.isClientSide());
        }
        return super.use(level, player, hand);
    }

    // ... Resto de métodos getters y setters (isComboWindowExpired, registerControllers, etc) iguales ...

    @Override
    public boolean isComboWindowExpired(ItemStack stack, long currentTime) {
        CompoundTag tag = stack.getTag();
        if (tag != null && tag.contains(LAST_HIT_TIME_TAG)) {
            long lastHit = tag.getLong(LAST_HIT_TIME_TAG);
            return (currentTime - lastHit > COMBO_RESET_TIME_MS);
        }
        return false;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "Controller", 5, this::predicate)
                .triggerableAnim("attack_1_trigger", ATTACK_1)
                .triggerableAnim("attack_2_trigger", ATTACK_2)
                .triggerableAnim("attack_3_trigger", ATTACK_3)
                .triggerableAnim("special_attack_trigger", SPECIAL_ATK)
                .triggerableAnim("idle_trigger", IDLE_ANIM));
    }

    private PlayState predicate(AnimationState<BlasphemousTwinDaggerItem> state) {
        ItemDisplayContext context = state.getData(DataTickets.ITEM_RENDER_PERSPECTIVE);
        if (context == null) return PlayState.STOP;
        boolean isFirstPerson = (context == ItemDisplayContext.FIRST_PERSON_RIGHT_HAND ||
                context == ItemDisplayContext.FIRST_PERSON_LEFT_HAND);

        if (!isFirstPerson) {
            state.getController().forceAnimationReset();
            return PlayState.STOP;
        }

        Player player = Minecraft.getInstance().player;
        if (player != null) {
            ItemStack heldStack = player.getMainHandItem();
            if (heldStack.getItem() == this) {
                CompoundTag tag = heldStack.getTag();
                if (tag != null && tag.contains(LAST_HIT_TIME_TAG)) {
                    long lastHit = tag.getLong(LAST_HIT_TIME_TAG);
                    if (System.currentTimeMillis() - lastHit > COMBO_RESET_TIME_MS) {
                        return state.setAndContinue(IDLE_ANIM);
                    }
                }
            }
        }
        return PlayState.CONTINUE;
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() { return this.cache; }

    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(new IClientItemExtensions() {
            private BlasphemousTwinDaggersRenderer renderer;
            @Override
            public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                if (this.renderer == null) this.renderer = new BlasphemousTwinDaggersRenderer();
                return this.renderer;
            }
        });
    }

    private SoundEvent getAttackSound() {
        int random = 1 + (int)(Math.random() * ((3 - 1) + 1));
        return switch (random) {
            case 1 -> ModSounds.DAGGER_ATTACK_1.get();
            case 2 -> ModSounds.DAGGER_ATTACK_2.get();
            case 3 -> ModSounds.DAGGER_ATTACK_3.get();
            default ->ModSounds.DAGGER_ATTACK_1.get();
        };
    }
}