package net.agusdropout.bloodyhell.item.custom;

import dev.kosmx.playerAnim.api.layered.IAnimation;
import dev.kosmx.playerAnim.api.layered.KeyframeAnimationPlayer;
import dev.kosmx.playerAnim.api.layered.ModifierLayer;
import dev.kosmx.playerAnim.minecraftApi.PlayerAnimationAccess;
import dev.kosmx.playerAnim.minecraftApi.PlayerAnimationRegistry;
import net.agusdropout.bloodyhell.BloodyHell;
import net.agusdropout.bloodyhell.CrimsonveilPower.PlayerCrimsonveilProvider; // IMPORT NECESARIO
import net.agusdropout.bloodyhell.entity.projectile.BlasphemousImpalerEntity;
import net.agusdropout.bloodyhell.item.client.BlasphemousImpalerItemRenderer;
import net.agusdropout.bloodyhell.networking.ModMessages; // IMPORT NECESARIO
import net.agusdropout.bloodyhell.networking.packet.CrimsonVeilDataSyncS2CPacket; // IMPORT NECESARIO
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer; // IMPORT NECESARIO
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animatable.SingletonGeoAnimatable;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.*;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.function.Consumer;

public class BlasphemousImpalerItem extends SwordItem implements GeoItem, IComboWeapon {

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    // --- CONFIGURACIÓN DE TIEMPOS ---
    private static final int COMBO_1_DURATION = 25;
    private static final int COMBO_2_DURATION = 30;
    private static final int COMBO_3_DURATION = 15;
    private static final long COMBO_RESET_MS = 2000;

    // COSTO DE MANA
    private static final int SPECIAL_ATTACK_COST = 10;

    // --- NOMBRES DE ANIMACIONES ---
    private static final RawAnimation IDLE = RawAnimation.begin().thenLoop("idle");
    private static final RawAnimation CHARGE = RawAnimation.begin().thenLoop("charge");
    private static final RawAnimation THROW_ANIM = RawAnimation.begin().thenPlay("throw");
    private static final RawAnimation ATK_1_ANIM = RawAnimation.begin().thenPlay("spear_attack_1");
    private static final RawAnimation ATK_2_ANIM = RawAnimation.begin().thenPlay("spear_attack_2");
    private static final RawAnimation ATK_3_ANIM = RawAnimation.begin().thenPlay("spear_attack_3");

    private static final String PA_CHARGE = "charge";
    private static final String PA_THROW = "throw";
    private static final String PA_ATK_1 = "spear_attack_1";
    private static final String PA_ATK_2 = "spear_attack_2";
    private static final String PA_ATK_3 = "spear_attack_3";

    public BlasphemousImpalerItem(Tier tier, int damage, float speed, Properties props) {
        super(tier, damage, speed, props);
        SingletonGeoAnimatable.registerSyncedAnimatable(this);
    }

    // --- IComboWeapon Implementation ---
    @Override public boolean shouldCancelStandardAttack() { return true; }
    @Override public float getComboDamageBonus(ItemStack stack) { return 0; }
    @Override public boolean isComboWindowExpired(ItemStack stack, long currentTime) {
        CompoundTag tag = stack.getTag();
        return tag != null && tag.contains("LastHitTime") && (currentTime - tag.getLong("LastHitTime") > COMBO_RESET_MS);
    }

    // --- CLICK IZQUIERDO (Ataque Melee) ---
    @Override
    public boolean onEntitySwing(ItemStack stack, LivingEntity entity) {
        if (entity instanceof Player player) {
            if (player.getCooldowns().isOnCooldown(this) || stack.getOrCreateTag().getBoolean("IsThrowing")) {
                return true;
            }

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
                tag.putInt("AttackCombo", currentCombo);
                tag.putInt("AttackTickCounter", 0);

                player.level().playSound(null, player.getX(), player.getY(), player.getZ(),
                        SoundEvents.PLAYER_ATTACK_SWEEP, net.minecraft.sounds.SoundSource.PLAYERS, 1.0f, 0.5f);
            }

            if (player.level().isClientSide) {
                handleClientAnimations(player, stack, currentCombo, false);
            }
        }
        return true;
    }

    // --- CLICK DERECHO (Inicio Carga) ---
    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (player.getCooldowns().isOnCooldown(this)) return InteractionResultHolder.fail(stack);

        player.startUsingItem(hand);

        if (level.isClientSide) {
            playPlayerAnimatorAnim(player, PA_CHARGE);
        }
        return InteractionResultHolder.consume(stack);
    }

    @Override
    public void onUseTick(Level level, LivingEntity entity, ItemStack stack, int count) {
        super.onUseTick(level, entity, stack, count);
        if (level.isClientSide && entity instanceof Player player) {
            playPlayerAnimatorAnim(player, PA_CHARGE);
        }
    }

    // --- SOLTAR CLICK (Lanzamiento) ---
    @Override
    public void releaseUsing(ItemStack stack, Level level, LivingEntity entity, int timeLeft) {
        if (entity instanceof Player player) {
            int duration = this.getUseDuration(stack) - timeLeft;

            if (duration >= 10) {
                CompoundTag tag = stack.getOrCreateTag();

                // 1. INICIAR ESTADO DE LANZAMIENTO (SIEMPRE, para que haya animación)
                tag.putBoolean("IsThrowing", true);
                tag.putInt("ThrowTickCounter", 0);

                // 2. ANIMACIÓN CLIENTE (SIEMPRE)
                if (level.isClientSide) {
                    System.out.println("Releasing use - playing throw animation");
                    playPlayerAnimatorAnim(player, PA_THROW);
                }

                // 3. LÓGICA DE CRIMSON VEIL (SERVIDOR)
                // Determina si realmente sale el proyectil o si es un "tiro falso"
                if (!level.isClientSide) {
                    player.getCapability(PlayerCrimsonveilProvider.PLAYER_CRIMSONVEIL).ifPresent(veil -> {
                        // Verificamos si tiene mana
                        if (veil.getCrimsonVeil() >= SPECIAL_ATTACK_COST) {
                            // SI TIENE: Consumimos y marcamos que puede spawnear
                            veil.subCrimsomveil(SPECIAL_ATTACK_COST);
                            ModMessages.sendToPlayer(new CrimsonVeilDataSyncS2CPacket(veil.getCrimsonVeil()), (ServerPlayer) player);
                            tag.putBoolean("CanSpawnProjectile", true);
                        } else {
                            // NO TIENE: Marcamos que NO puede spawnear (pero la animación ya arrancó)
                            tag.putBoolean("CanSpawnProjectile", false);
                        }
                    });
                }
            }
        }
    }

    // --- LOGICA DEL SERVIDOR (Tick Loop) ---
    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slot, boolean isSelected) {
        if (!level.isClientSide && entity instanceof Player player) {
            if (!isSelected && player.getOffhandItem() != stack) return;

            CompoundTag tag = stack.getOrCreateTag();

            // 1. PROCESAR LANZAMIENTO
            if (tag.getBoolean("IsThrowing")) {
                int counter = tag.getInt("ThrowTickCounter");
                counter++;
                tag.putInt("ThrowTickCounter", counter);

                // Tick 10: Momento del spawn del proyectil
                if (counter == 10) {
                    // Verificar si tenía mana para disparar
                    if (tag.getBoolean("CanSpawnProjectile")) {
                        performThrowEntity(level, player, stack);
                    } else {
                        // NO TENÍA MANA: Solo sonido de aire (fallo visual)
                        level.playSound(null, player.getX(), player.getY(), player.getZ(),
                                SoundEvents.PLAYER_ATTACK_SWEEP, net.minecraft.sounds.SoundSource.PLAYERS, 1.0f, 0.5f);
                    }
                }

                // Tick 20: Fin del estado
                if (counter >= 20) {
                    tag.putBoolean("IsThrowing", false);
                    tag.putBoolean("CanSpawnProjectile", false); // Reset del flag
                }
            }

            // 2. PROCESAR ATAQUE MELEE
            if (tag.getBoolean("IsAttacking")) {
                int counter = tag.getInt("AttackTickCounter");
                counter++;
                tag.putInt("AttackTickCounter", counter);

                int combo = tag.getInt("AttackCombo");
                checkAndDealDamage(level, player, stack, combo, counter);

                int maxDuration = switch (combo) {
                    case 1 -> COMBO_1_DURATION;
                    case 2 -> COMBO_2_DURATION;
                    case 3 -> COMBO_3_DURATION;
                    default -> 20;
                };

                if (counter >= maxDuration) {
                    tag.putBoolean("IsAttacking", false);
                    tag.putInt("AttackTickCounter", 0);
                }
            }
        }
    }

    // --- HELPERS LÓGICOS ---

    private void checkAndDealDamage(Level level, Player player, ItemStack stack, int combo, int timer) {
        if (combo == 1 && timer == 15) performAreaDamage(level, player, 1.0f);
        if (combo == 2) {
            if (timer == 15) performAreaDamage(level, player, 1.0f);
            if (timer == 22) performAreaDamage(level, player, 2.0f);
        }
        if (combo == 3 && timer == 10) performAreaDamage(level, player, 1.5f);
    }

    private void performAreaDamage(Level level, Player player, float multiplier) {
        Vec3 look = player.getLookAngle();
        Vec3 center = player.getEyePosition().add(look.scale(2.5));
        AABB damageBox = new AABB(center.x - 1.5, center.y - 1, center.z - 1.5, center.x + 1.5, center.y + 1, center.z + 1.5);

        boolean hit = false;
        for (LivingEntity target : level.getEntitiesOfClass(LivingEntity.class, damageBox)) {
            if (target != player && !target.isAlliedTo(player)) {
                target.hurt(level.damageSources().mobAttack(player), 8.0f * multiplier);
                hit = true;
            }
        }
        if (hit) level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.TRIDENT_HIT, net.minecraft.sounds.SoundSource.PLAYERS, 1.0f, 1.0f);
    }

    private void performThrowEntity(Level level, Player player, ItemStack stack) {
        BlasphemousImpalerEntity projectile = new BlasphemousImpalerEntity(level, player, stack);
        projectile.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, 4.5F, 1.0F);
        projectile.setBaseDamage(10.0);

        if (!player.getAbilities().instabuild) {
            stack.shrink(1);
            projectile.pickup = AbstractArrow.Pickup.ALLOWED;
        } else {
            projectile.pickup = AbstractArrow.Pickup.CREATIVE_ONLY;
        }

        level.addFreshEntity(projectile);
        player.getCooldowns().addCooldown(this, 20);

        // Sonidos épicos
        level.playSound(null, player.getX(), player.getY(), player.getZ(),
                SoundEvents.TRIDENT_THROW,
                net.minecraft.sounds.SoundSource.PLAYERS, 2.0F, 0.5F);
        level.playSound(null, player.getX(), player.getY(), player.getZ(),
                SoundEvents.WITHER_SHOOT,
                net.minecraft.sounds.SoundSource.PLAYERS, 0.8F, 0.7F);
        level.playSound(null, player.getX(), player.getY(), player.getZ(),
                SoundEvents.CHAIN_BREAK,
                net.minecraft.sounds.SoundSource.PLAYERS, 1.5F, 2.0F);

        stack.getOrCreateTag().putLong("LastThrowTime", level.getGameTime());
    }

    private int updateAndGetCombo(ItemStack stack) {
        CompoundTag tag = stack.getOrCreateTag();
        long currentTime = System.currentTimeMillis();
        if (currentTime - tag.getLong("LastHitTime") > COMBO_RESET_MS) tag.putInt("CurrentCombo", 0);

        int currentCombo = tag.getInt("CurrentCombo") + 1;
        if (currentCombo > 3) currentCombo = 1;

        tag.putInt("CurrentCombo", currentCombo);
        tag.putLong("LastHitTime", currentTime);
        return currentCombo;
    }

    // --- GESTIÓN VISUAL (Cliente) ---

    private void handleClientAnimations(Player player, ItemStack stack, int combo, boolean isThrow) {
        boolean isFirstPerson = Minecraft.getInstance().options.getCameraType().isFirstPerson();

        if (isThrow) {
            if (isFirstPerson) triggerAnim(player, GeoItem.getId(stack), "controller", "throw_trigger");
            else {
                triggerAnim(player, GeoItem.getId(stack), "controller", "idle_trigger");
                System.out.println("Handling throw animation - playing PA_THROW");
                playPlayerAnimatorAnim(player, PA_THROW);
            }
        } else {
            if (isFirstPerson) {
                String animName = switch (combo) {
                    case 2 -> "attack_2_trigger";
                    case 3 -> "attack_3_trigger";
                    default -> "attack_1_trigger";
                };
                triggerAnim(player, GeoItem.getId(stack), "controller", animName);
            } else {
                triggerAnim(player, GeoItem.getId(stack), "controller", "idle_trigger");
                String paAnim = switch (combo) {
                    case 2 -> PA_ATK_2;
                    case 3 -> PA_ATK_3;
                    default -> PA_ATK_1;
                };
                playPlayerAnimatorAnim(player, paAnim);
            }
        }
    }

    private void playPlayerAnimatorAnim(Player player, String animName) {
        var animationLayer = (ModifierLayer<IAnimation>) PlayerAnimationAccess
                .getPlayerAssociatedData((AbstractClientPlayer) player)
                .get(new ResourceLocation(BloodyHell.MODID, "animation"));

        if (animationLayer != null) {
            var anim = PlayerAnimationRegistry.getAnimation(new ResourceLocation(BloodyHell.MODID, animName));
            if (anim != null) {
                if (animationLayer.getAnimation() instanceof KeyframeAnimationPlayer current) {
                    if (current.getData().equals(anim)) {
                        return;
                    }
                }
                animationLayer.setAnimation(new KeyframeAnimationPlayer(anim));
            }
        }
    }

    // --- GECKOLIB CONTROLLERS (Item) ---
    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "controller", 2, state -> {
            Player player = Minecraft.getInstance().player;
            if (player == null) return PlayState.STOP;

            boolean isFirstPerson = Minecraft.getInstance().options.getCameraType().isFirstPerson();
            if (!isFirstPerson) return state.setAndContinue(IDLE);

            ItemStack stack = state.getData(DataTickets.ITEMSTACK);
            if (stack != null && stack.hasTag() &&
                    (stack.getTag().getBoolean("IsAttacking") || stack.getTag().getBoolean("IsThrowing"))) {
                return PlayState.CONTINUE;
            }

            if (player.isUsingItem() && player.getUseItem().getItem() == this) {
                return state.setAndContinue(CHARGE);
            }

            return state.setAndContinue(IDLE);
        })
                .triggerableAnim("attack_1_trigger", ATK_1_ANIM)
                .triggerableAnim("attack_2_trigger", ATK_2_ANIM)
                .triggerableAnim("attack_3_trigger", ATK_3_ANIM)
                .triggerableAnim("throw_trigger", THROW_ANIM)
                .triggerableAnim("idle_trigger", IDLE));
    }

    // --- RENDERIZADO ---
    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(new IClientItemExtensions() {
            private BlasphemousImpalerItemRenderer renderer;
            @Override
            public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                if (this.renderer == null) this.renderer = new BlasphemousImpalerItemRenderer();
                return this.renderer;
            }

        });
    }

    @Override public int getUseDuration(ItemStack stack) { return 72000; }
    @Override public UseAnim getUseAnimation(ItemStack stack) { return UseAnim.NONE; }
    @Override public AnimatableInstanceCache getAnimatableInstanceCache() { return cache; }
}