package net.agusdropout.bloodyhell.item.custom;

import net.agusdropout.bloodyhell.BloodyHell;
import net.agusdropout.bloodyhell.CrimsonveilPower.PlayerCrimsonveilProvider; // IMPORTANTE
import net.agusdropout.bloodyhell.entity.ModEntityTypes;
import net.agusdropout.bloodyhell.entity.effects.EntityCameraShake;


import net.agusdropout.bloodyhell.entity.projectile.BlasphemousSpearEntity;
import net.agusdropout.bloodyhell.item.client.BlasphemousHulkingMassOfIronRenderer;
import net.agusdropout.bloodyhell.networking.ModMessages;
import net.agusdropout.bloodyhell.networking.packet.CrimsonVeilDataSyncS2CPacket;
import net.agusdropout.bloodyhell.particle.ModParticles;
import net.agusdropout.bloodyhell.sound.ModSounds;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animatable.SingletonGeoAnimatable;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.*;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

import dev.kosmx.playerAnim.api.layered.IAnimation;
import dev.kosmx.playerAnim.api.layered.KeyframeAnimationPlayer;
import dev.kosmx.playerAnim.api.layered.ModifierLayer;
import dev.kosmx.playerAnim.minecraftApi.PlayerAnimationAccess;
import dev.kosmx.playerAnim.minecraftApi.PlayerAnimationRegistry;

import java.util.List;
import java.util.function.Consumer;

public class BlasphemousHulkingMassOfIronItem extends SwordItem implements GeoItem, IComboWeapon {

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    // Tiempos Combo Normal
    private static final int COMBO_1_DURATION = 20;
    private static final int COMBO_1_DAMAGE_DELAY = 12;
    private static final int COMBO_2_DURATION = 30;
    private static final int COMBO_2_DAMAGE_DELAY = 18;
    private static final int COMBO_3_DURATION = 35;
    private static final int COMBO_3_DAMAGE_DELAY = 22;

    // TIEMPOS ATAQUE ESPECIAL
    private static final int SPECIAL_DURATION = 45; // 2.25s
    private static final int SPECIAL_PHASE_1_TICK = 15; // 0.75s (Primer impacto)
    private static final int SPECIAL_PHASE_2_TICK = 30; // 1.5s (Hundimiento profundo)

    private static final long COMBO_RESET_MS = 3000;

    // Animaciones
    private static final RawAnimation SLAM_1 = RawAnimation.begin().thenPlay("slam_1");
    private static final RawAnimation SLAM_2 = RawAnimation.begin().thenPlay("slam_2");
    private static final RawAnimation SLAM_3 = RawAnimation.begin().thenPlay("slam_3");
    private static final RawAnimation SPECIAL_ATK = RawAnimation.begin().thenPlay("special_attack"); // 1ra persona
    private static final RawAnimation IDLE = RawAnimation.begin().thenLoop("idle");

    public BlasphemousHulkingMassOfIronItem(Tier tier, int damage, float speed, Properties props) {
        super(tier, damage, speed, props);
        SingletonGeoAnimatable.registerSyncedAnimatable(this);
    }

    // --- IComboWeapon Implementation ---
    @Override
    public boolean shouldCancelStandardAttack() { return true; }
    @Override
    public float getComboDamageBonus(ItemStack stack) {
        int combo = getCombo(stack);
        return combo == 3 ? 10.0f : (combo == 2 ? 5.0f : 0.0f);
    }
    @Override
    public boolean isComboWindowExpired(ItemStack stack, long currentTime) {
        CompoundTag tag = stack.getTag();
        return tag != null && tag.contains("LastHitTime") && (currentTime - tag.getLong("LastHitTime") > COMBO_RESET_MS);
    }
    @Override
    public int getDamageDelay(int comboStep) {
        return switch (comboStep) {
            case 1 -> COMBO_1_DAMAGE_DELAY;
            case 2 -> COMBO_2_DAMAGE_DELAY;
            case 3 -> COMBO_3_DAMAGE_DELAY;
            default -> 10;
        };
    }


    // --- CLICK IZQUIERDO (Ataque Normal) ---
    @Override
    public boolean onEntitySwing(ItemStack stack, LivingEntity entity) {
        if (entity instanceof Player player) {

            // 1. CHEQUEO DE SEGURIDAD (CRUCIAL)
            // Si hay cooldown activo (puesto por el ataque especial), IGNORAR el click izquierdo.
            // Esto evita que interrumpas tu propio ataque especial con un golpe básico.
            if (player.getCooldowns().isOnCooldown(this)) {
                return true; // Cancelar swing vanilla y salir
            }

            // 2. CHEQUEO DE ESTADO ESPECIAL
            // Si por alguna razón el cooldown no llegó todavía (lag), miramos el NBT local.
            if (stack.getOrCreateTag().getBoolean("IsSpecial")) {
                return true;
            }

            // --- Lógica de Combo Normal ---
            int currentCombo = updateAndGetCombo(stack);
            int duration = switch (currentCombo) {
                case 1 -> COMBO_1_DURATION;
                case 2 -> COMBO_2_DURATION;
                case 3 -> COMBO_3_DURATION;
                default -> 20;
            };
            player.getCooldowns().addCooldown(this, duration);

            if (!player.level().isClientSide) {
                CompoundTag tag = stack.getOrCreateTag();
                tag.putBoolean("IsAttacking", true);
                tag.putBoolean("IsSpecial", false); // Marcamos explícitamente que NO es especial
                tag.putInt("AttackCombo", currentCombo);
                tag.putInt("AttackTickCounter", 0);

                player.level().playSound(null, player.getX(), player.getY(), player.getZ(),
                        SoundEvents.PLAYER_ATTACK_SWEEP, net.minecraft.sounds.SoundSource.PLAYERS, 1.0f, 0.5f);
            }

            if (player.level().isClientSide) {
                handleClientAnimations(player, stack, currentCombo, false);
            }
        }
        return true; // Cancelar swing vanilla siempre
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (hand == InteractionHand.MAIN_HAND) {
            if (player.getCooldowns().isOnCooldown(this)) {
                return InteractionResultHolder.fail(stack);
            }

            // --- CORRECCIÓN BUG 2 (PARTE A): Actualizar LastHitTime ---
            // Esto evita que el ClientTickHandler crea que el combo expiró y borre la animación
            stack.getOrCreateTag().putLong("LastHitTime", System.currentTimeMillis());

            // 1. LÓGICA CLIENTE
            if (level.isClientSide) {
                stack.getOrCreateTag().putBoolean("IsSpecial", true);
                handleClientAnimations(player, stack, 0, true);
                return InteractionResultHolder.sidedSuccess(stack, true);
            }

            // 2. LÓGICA SERVIDOR
            player.getCapability(PlayerCrimsonveilProvider.PLAYER_CRIMSONVEIL).ifPresent(veil -> {
                if (veil.getCrimsonVeil() >= 10) {
                    veil.subCrimsomveil(10);
                    ModMessages.sendToPlayer(new CrimsonVeilDataSyncS2CPacket(veil.getCrimsonVeil()), (ServerPlayer) player);

                    player.getCooldowns().addCooldown(this, SPECIAL_DURATION);

                    CompoundTag tag = stack.getOrCreateTag();
                    tag.putBoolean("IsAttacking", true);
                    tag.putBoolean("IsSpecial", true);
                    tag.putInt("AttackTickCounter", 0);
                    tag.putInt("CurrentCombo", 0);

                    level.playSound(null, player.getX(), player.getY(), player.getZ(),
                            SoundEvents.WARDEN_ATTACK_IMPACT, net.minecraft.sounds.SoundSource.PLAYERS, 1.0f, 0.5f);
                } else {
                    player.getCooldowns().addCooldown(this, 10);
                }
            });

            return InteractionResultHolder.sidedSuccess(stack, false);
        }
        return super.use(level, player, hand);
    }



    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slot, boolean isSelected) {
        if (!level.isClientSide && entity instanceof Player player && isSelected) {
            CompoundTag tag = stack.getOrCreateTag();

            if (tag.getBoolean("IsAttacking")) {
                int counter = tag.getInt("AttackTickCounter");
                counter++;
                tag.putInt("AttackTickCounter", counter);

                boolean isSpecial = tag.getBoolean("IsSpecial");

                if (isSpecial) {
                    // --- LÓGICA ESPECIAL ---
                    if (counter == SPECIAL_PHASE_1_TICK) {
                        performSpecialAttackPhase(level, player, 1);
                    }
                    if (counter == SPECIAL_PHASE_2_TICK) {
                        performSpecialAttackPhase(level, player, 2);
                    }

                    // FIN DEL ESPECIAL
                    if (counter >= SPECIAL_DURATION) {
                        tag.putBoolean("IsAttacking", false);
                        tag.putBoolean("IsSpecial", false); // <--- ¡IMPORTANTE! RESETEAR ESTO
                        tag.putInt("AttackTickCounter", 0);
                    }

                } else {
                    // --- LÓGICA NORMAL ---
                    int combo = tag.getInt("AttackCombo");
                    int delayNeeded = getDamageDelay(combo);

                    if (counter == delayNeeded) {
                        performAreaDamage(level, player, stack, combo);
                    }

                    int maxDuration = switch (combo) {
                        case 1 -> COMBO_1_DURATION;
                        case 2 -> COMBO_2_DURATION;
                        case 3 -> COMBO_3_DURATION;
                        default -> 20;
                    };

                    // FIN DEL COMBO NORMAL
                    if (counter >= maxDuration) {
                        tag.putBoolean("IsAttacking", false);
                        tag.putInt("AttackTickCounter", 0);
                    }
                }
            }
        }
    }

    private void performSpecialAttackPhase(Level level, Player player, int phase) {
        Vec3 playerPos = player.position();
        BlockPos centerPos = BlockPos.containing(playerPos);

        // AJUSTE: Radios y Densidad
        int radius = (phase == 1) ? 3 : 5;
        float density = (phase == 1) ? 0.6f : 0.4f; // Ajusta según gusto

        // Configuración de efectos
        float shakeMag = (phase == 1) ? 0.2f : 0.6f;
        float shakeRad = (phase == 1) ? 15f : 25f;
        float velocityY = (phase == 1) ? 0.3f : 0.45f;

        EntityCameraShake.cameraShake(level, playerPos, shakeRad, shakeMag, 10, 5);

        int particleCount = (phase == 1) ? 200 : 400;
        double shockSpeed = (phase == 1) ? 1 : 2;

        spawnGroundShockwave(level, playerPos, particleCount, shockSpeed);

        // Iteramos en el cuadrado
        for (int x = -radius; x <= radius; x++) {
            for (int z = -radius; z <= radius; z++) {

                // Cuadrado Hueco
                if (Math.max(Math.abs(x), Math.abs(z)) != radius) continue;

                // Densidad (Huecos aleatorios)
                if (level.random.nextFloat() > density) continue;

                BlockPos candidatePos = centerPos.offset(x, 0, z);

                // Buscar suelo
                BlockPos groundPos = null;
                BlockState groundState = null;
                for (int yOffset = 0; yOffset >= -3; yOffset--) {
                    BlockPos checkPos = candidatePos.offset(0, yOffset, 0);
                    BlockState state = level.getBlockState(checkPos);
                    if (!state.isAir() && state.isSolidRender(level, checkPos)) {
                        groundPos = checkPos;
                        groundState = state;
                        break;
                    }
                }

                if (groundPos == null) continue;

                // A. Falling Block
                net.agusdropout.bloodyhell.entity.effects.EntityFallingBlock fallingBlock =
                        new net.agusdropout.bloodyhell.entity.effects.EntityFallingBlock(
                                ModEntityTypes.ENTITY_FALLING_BLOCK.get(),
                                level,
                                groundState,
                                velocityY
                        );
                fallingBlock.setPos(groundPos.getX() + 0.5, groundPos.getY() + 1.0, groundPos.getZ() + 0.5);
                level.addFreshEntity(fallingBlock);

                // B. BLASPHEMOUS SPEAR (CON ROTACIÓN)
                float damage = (phase == 1) ? 8.0f : 12.0f;

                double dx = groundPos.getX() + 0.5 - player.getX(); // Usamos groundPos mejor
                double dz = groundPos.getZ() + 0.5 - player.getZ();

                // atan2 devuelve radianes donde 0 es Este.
                // Convertimos a grados.
                float angleDegrees = (float) (Math.atan2(dz, dx) * (180D / Math.PI));

                // Minecraft: Sur=0, Este=-90. atan2: Este=0.
                // Restamos 90 para alinear.
                float finalYaw = angleDegrees - 90f;

                // CREAR ENTIDAD CON YAW
                BlasphemousSpearEntity spear =
                        new BlasphemousSpearEntity(
                                level,
                                groundPos.getX() + 0.5,
                                groundPos.getY() + 1.0,
                                groundPos.getZ() + 0.5,
                                damage,
                                player.getUUID(),
                                finalYaw // <--- Pasamos el Yaw calculado
                        );

                level.addFreshEntity(spear);



                // C. Partículas
                if (level instanceof net.minecraft.server.level.ServerLevel serverLevel) {
                    serverLevel.sendParticles(net.agusdropout.bloodyhell.particle.ModParticles.MAGIC_LINE_PARTICLE.get(),
                            groundPos.getX() + 0.5, groundPos.getY() + 1.2, groundPos.getZ() + 0.5,
                            3, 0.2, 0.5, 0.2, 0.05);
                }
            }
        }

        // Sonidos
        if (phase == 1) {
            level.playSound(null, playerPos.x, playerPos.y, playerPos.z, SoundEvents.GENERIC_EXPLODE, net.minecraft.sounds.SoundSource.PLAYERS, 0.5f, 1.2f);
        } else {
            level.playSound(null, playerPos.x, playerPos.y, playerPos.z, SoundEvents.GENERIC_EXPLODE, net.minecraft.sounds.SoundSource.PLAYERS, 1.0f, 0.8f);
            level.playSound(null, playerPos.x, playerPos.y, playerPos.z, SoundEvents.ZOMBIE_BREAK_WOODEN_DOOR, net.minecraft.sounds.SoundSource.PLAYERS, 1.0f, 0.5f);
        }
    }

    // Método Helper para la onda expansiva
    private void spawnGroundShockwave(Level level, Vec3 center, int count, double speed) {
        if (level instanceof net.minecraft.server.level.ServerLevel serverLevel) {
            // 1. Obtenemos el bloque debajo del centro
            BlockPos underPos = BlockPos.containing(center).below();
            BlockState state = level.getBlockState(underPos);

            // Si es aire, usamos piedra por defecto para que no falle
            if (state.isAir()) state = net.minecraft.world.level.block.Blocks.STONE.defaultBlockState();

            // 2. Creamos la partícula de bloque
            net.minecraft.core.particles.ParticleOptions particle =
                    new net.minecraft.core.particles.BlockParticleOption(net.minecraft.core.particles.ParticleTypes.BLOCK, state);

            // 3. Generamos el anillo
            for (int i = 0; i < count; i++) {
                double angle = 2 * Math.PI * i / count;
                double offsetX = Math.cos(angle) * 1.5; // Radio inicial de 1.5 bloques
                double offsetZ = Math.sin(angle) * 1.5;

                // Velocidad hacia afuera
                double velX = Math.cos(angle) * speed;
                double velZ = Math.sin(angle) * speed;

                serverLevel.sendParticles(particle,
                        center.x + offsetX, center.y + 0.1, center.z + offsetZ,
                        0, // Count 0 en sendParticles permite definir velocidad manual (vX, vY, vZ)
                        velX, 0.1, velZ,
                        1.0); // Velocidad base
            }
        }
    }

    private void performAreaDamage(Level level, Player player, ItemStack stack, int comboStep) {
        // 1. Hitbox
        double reach;
        double width;

        if (comboStep == 2) {
            reach = 6.0D;
            width = 1.0D;
        } else {
            reach = 4.0D;
            width = 3.5D;
        }

        // 2. Calcular posición
        Vec3 look = player.getLookAngle();
        Vec3 lookFlat = new Vec3(look.x, 0, look.z).normalize();
        Vec3 startPos = player.position().add(0, player.getEyeHeight() * 0.5, 0);
        Vec3 endPos = startPos.add(lookFlat.scale(reach));
        AABB damageBox = new AABB(startPos, endPos).inflate(width / 2.0, 1.0, width / 2.0);

        // 3. Daño
        List<LivingEntity> entities = level.getEntitiesOfClass(LivingEntity.class, damageBox);
        float baseDamage = (float) player.getAttributeValue(net.minecraft.world.entity.ai.attributes.Attributes.ATTACK_DAMAGE);
        float bonus = getComboDamageBonus(stack);
        float totalDamage = baseDamage + bonus;

        boolean hitSomething = false;

        for (LivingEntity target : entities) {
            if (target != player && !target.isAlliedTo(player)) {
                target.hurt(level.damageSources().playerAttack(player), totalDamage);
                double kbStrength = (comboStep == 3) ? 1.5 : 0.5;
                target.knockback(kbStrength, player.getX() - target.getX(), player.getZ() - target.getZ());
                hitSomething = true;
            }
        }

        // 4. Efectos
        if (hitSomething || comboStep == 3) {
            level.playSound(null, player.getX(), player.getY(), player.getZ(),
                    SoundEvents.ANVIL_LAND, net.minecraft.sounds.SoundSource.PLAYERS, 1.0f, 0.5f);

            if (comboStep == 3) {
                Vec3 shockwavePos = player.position().add(lookFlat.scale(reach));
                spawnFallingBlocks(level, shockwavePos);

                // --- CORRECCIÓN BUG 3: AÑADIDO CAMERA SHAKE ---
                EntityCameraShake.cameraShake(level, shockwavePos, 15.0f, 0.3f, 10, 5);
            }
        }
    }

    private void spawnFallingBlocks(Level level, Vec3 pos) {
        BlockPos centerPos = BlockPos.containing(pos.x, pos.y, pos.z);
        int radius = 3;

        for (int x = -radius; x <= radius; x++) {
            for (int z = -radius; z <= radius; z++) {
                BlockPos targetPos = centerPos.offset(x, -1, z);
                double distance = Math.sqrt(x*x + z*z);
                if (distance > radius) continue;

                BlockState state = level.getBlockState(targetPos);
                if (state.isAir() || !state.getFluidState().isEmpty() || state.getDestroySpeed(level, targetPos) < 0) continue;

                float velocityY = (distance <= 1.5) ? 0.4f : 0.7f;

                // REEMPLAZA CON TU REGISTRO REAL DE ENTIDAD
                net.agusdropout.bloodyhell.entity.effects.EntityFallingBlock fallingBlock =
                        new net.agusdropout.bloodyhell.entity.effects.EntityFallingBlock(
                                ModEntityTypes.ENTITY_FALLING_BLOCK.get(), // <--- TU MOD ENTITY TYPES
                                level,
                                state,
                                velocityY
                        );

                fallingBlock.setPos(targetPos.getX() + 0.5, targetPos.getY() + 1, targetPos.getZ() + 0.5);
                level.addFreshEntity(fallingBlock);
            }
        }
    }

    private void handleClientAnimations(Player player, ItemStack stack, int combo, boolean isSpecial) {
        boolean isFirstPerson = Minecraft.getInstance().options.getCameraType().isFirstPerson();

        if (isSpecial) {
            if (isFirstPerson) {
                triggerAnim(player, GeoItem.getId(stack), "Controller", "special_attack");
            } else {
                // --- CORRECCIÓN BUG 2 (PARTE B): Descomentar esto ---
                // Esto pone el item en modo "quieto" mientras el cuerpo hace la animación especial
                triggerAnim(player, GeoItem.getId(stack), "Controller", "idle_trigger");

                playPlayerAnimatorAnim(player, "special_slam_attack");
            }
        } else {
            // Lógica normal
            if (isFirstPerson) {
                String animName = switch (combo) {
                    case 2 -> "slam_2_trigger";
                    case 3 -> "slam_3_trigger";
                    default -> "slam_1_trigger";
                };
                triggerAnim(player, GeoItem.getId(stack), "Controller", animName);
            } else {
                triggerAnim(player, GeoItem.getId(stack), "Controller", "idle_trigger");
                String paAnim = switch (combo) {
                    case 2 -> "slam_2";
                    case 3 -> "slam_3";
                    default -> "slam_1";
                };
                playPlayerAnimatorAnim(player, paAnim);
            }
        }
    }

    private int updateAndGetCombo(ItemStack stack) {
        CompoundTag tag = stack.getOrCreateTag();
        long lastTime = tag.getLong("LastHitTime");
        int currentCombo = tag.getInt("CurrentCombo");
        long currentTime = System.currentTimeMillis();

        // Si pasó mucho tiempo, reseteamos a 0
        if (currentTime - lastTime > COMBO_RESET_MS) {
            currentCombo = 0;
        }

        currentCombo++;
        if (currentCombo > 3) currentCombo = 1;

        tag.putInt("CurrentCombo", currentCombo);
        tag.putLong("LastHitTime", currentTime);
        return currentCombo;
    }

    private int getCombo(ItemStack stack) { return stack.getOrCreateTag().getInt("CurrentCombo"); }

    private void playPlayerAnimatorAnim(Player player, String animName) {
        // Obtenemos la capa de animación del jugador
        var animationLayer = (ModifierLayer<IAnimation>) PlayerAnimationAccess
                .getPlayerAssociatedData((AbstractClientPlayer) player)
                .get(new ResourceLocation(BloodyHell.MODID, "animation"));

        // Si existe la capa...
        if (animationLayer != null) {
            // Buscamos la animación en el registro
            var anim = PlayerAnimationRegistry.getAnimation(new ResourceLocation(BloodyHell.MODID, animName));
            if (anim != null) {
                // La reproducimos
                animationLayer.setAnimation(new KeyframeAnimationPlayer(anim));
            } else {
                System.out.println("No se encontró la animación: " + animName);
            }
        }
    }

    // --- CONTROLLERS ---
    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "Controller", 5, state -> PlayState.CONTINUE)
                .triggerableAnim("slam_1_trigger", SLAM_1)
                .triggerableAnim("slam_2_trigger", SLAM_2)
                .triggerableAnim("slam_3_trigger", SLAM_3)
                .triggerableAnim("special_attack", SPECIAL_ATK) // Nuevo trigger
                .triggerableAnim("idle_trigger", IDLE));
    }
    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(new IClientItemExtensions() {
            private BlasphemousHulkingMassOfIronRenderer renderer;
            @Override
            public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                if (this.renderer == null) this.renderer = new BlasphemousHulkingMassOfIronRenderer();
                return this.renderer;
            }
        });
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() { return this.cache; }
}