package net.agusdropout.bloodyhell.item.custom;

// ... tus imports existentes ...
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
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
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

// --- IMPORTS DE PLAYER ANIMATOR ---
import dev.kosmx.playerAnim.api.layered.IAnimation;
import dev.kosmx.playerAnim.api.layered.KeyframeAnimationPlayer;
import dev.kosmx.playerAnim.api.layered.ModifierLayer;
import dev.kosmx.playerAnim.minecraftApi.PlayerAnimationAccess;
import dev.kosmx.playerAnim.minecraftApi.PlayerAnimationRegistry;
import net.agusdropout.bloodyhell.BloodyHell;

import java.util.function.Consumer;

public class BlasphemousTwinDaggerItem extends SwordItem implements GeoItem, IComboWeapon {

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    // --- VARIABLES ---
    public static final float COMBO_1_BONUS = 0.0f;
    public static final float COMBO_2_BONUS = 2.0f;
    public static final float COMBO_3_BONUS = 4.0f;

    public boolean isSpecialAttackActive = false;

    // --- ANIMACIONES GECKOLIB ---
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
        if (isSelected && entity instanceof Player player) {
            ItemStack offhandStack = player.getOffhandItem();
            if (!offhandStack.isEmpty()) {
                if (!player.getInventory().add(offhandStack)) {
                    player.drop(offhandStack, true);
                }
                player.setItemInHand(InteractionHand.OFF_HAND, ItemStack.EMPTY);
            }
        }
    }

    // --- COMBO LOGIC ---
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

    // --- MÉTODO AUXILIAR PARA PLAYER ANIMATOR (CLIENTE) ---
    // Centralizamos la lógica aquí para llamarla desde click izq y der
    private void playPlayerAnimatorAnim(Player player, String animName) {
        // Doble chequeo de seguridad de cliente

            var animationLayer = (ModifierLayer<IAnimation>) PlayerAnimationAccess
                    .getPlayerAssociatedData((AbstractClientPlayer) player)
                    .get(new ResourceLocation(BloodyHell.MODID, "animation"));

            if (animationLayer != null) {
                var anim = PlayerAnimationRegistry.getAnimation(new ResourceLocation(BloodyHell.MODID, animName));
                if (anim != null) {
                    animationLayer.setAnimation(new KeyframeAnimationPlayer(anim));
                    System.out.println("Playing player animator animation: " + animName);
                } else {
                    System.out.println("Animation not found in registry: " + animName);
                }
            } else {
                System.out.println("Animation layer not found for player.");
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
                // Si pasaron menos de 400ms (0.4s) desde el especial, IGNORAMOS este swing.
                // Esto evita que el swing automático del click derecho active el combo normal.
                if (System.currentTimeMillis() - lastSpecialTime < 400) {
                    return true; // Retorna true para cancelar el procesado del golpe normal
                }
            }

            // 1. Lógica Combo (Común)
            int comboStep = updateAndGetCombo(stack);

            int cooldownDuration = switch (comboStep) {
                case 1 -> 20;
                case 2 -> 15;
                case 3 -> 20;
                default -> 20;
            };
            player.getCooldowns().addCooldown(this, cooldownDuration);

            // 2. Sonido (Server y Client predictivo)
            if (!player.level().isClientSide) {
                float volumen = 0.5f;
                float pitch = 0.9f + (player.getRandom().nextFloat() * 0.2f);
                player.level().playSound(null, player.getX(), player.getY(), player.getZ(),
                        this.getAttackSound(), net.minecraft.sounds.SoundSource.PLAYERS, volumen, pitch);
            }

            // 3. Animaciones (Cliente)
            if (player.level().isClientSide) {
                boolean isFirstPerson = Minecraft.getInstance().options.getCameraType().isFirstPerson();

                if (isFirstPerson) {
                    // GeckoLib Primera Persona
                    String animName = switch (comboStep) {
                        case 2 -> "attack_2_trigger";
                        case 3 -> "attack_3_trigger";
                        default -> "attack_1_trigger";
                    };
                    triggerAnim(player, GeoItem.getId(stack), "Controller", animName);
                } else {
                    // GeckoLib Tercera Persona (Item Idle)
                    triggerAnim(player, GeoItem.getId(stack), "Controller", "idle_trigger");

                    // --- PLAYER ANIMATOR (CUERPO ENTERO) ---
                    // Aquí llamamos a la animación de tercera persona del mod
                    playPlayerAnimatorAnim(player, "dagger_attack_" + comboStep);
                }
            }
        }
        return true;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(net.minecraft.world.level.Level level, Player player, InteractionHand hand) {


        if (hand == InteractionHand.MAIN_HAND) {
            player.getCapability(PlayerCrimsonveilProvider.PLAYER_CRIMSONVEIL).ifPresent(playerCrimsonVeil -> {
                if (playerCrimsonVeil.getCrimsonVeil() >= 5 && !player.getCooldowns().isOnCooldown(this)) {

                    // 1. APLICAMOS COOLDOWN EN AMBOS LADOS (CLIENTE Y SERVIDOR)
                    // Esto es CRUCIAL. Si solo lo haces en el server, el cliente no se entera
                    // a tiempo y el 'onEntitySwing' sobrescribe la animación especial.
                    player.getCooldowns().addCooldown(this, 50);

                    // Lógica Servidor (Datos, Daño, Entidad)
                    if (!level.isClientSide()) {
                        playerCrimsonVeil.subCrimsomveil(5);
                        ModMessages.sendToPlayer(new CrimsonVeilDataSyncS2CPacket(playerCrimsonVeil.getCrimsonVeil()), ((ServerPlayer) player));
                        System.out.println("Subtracted 5 Crimson Veil from player for special dagger attack. New amount: " + playerCrimsonVeil.getCrimsonVeil());
                        float damage = 10.0f;
                        SpecialSlashEntity slash = new SpecialSlashEntity(level, player, damage);
                        level.addFreshEntity(slash);
                    }
                }
            });
            player.level().playSound(null, player.getX(), player.getY(), player.getZ(),
                    SoundEvents.BLAZE_SHOOT, net.minecraft.sounds.SoundSource.PLAYERS, 0.5f, 0.8f + (player.getRandom().nextFloat() * 0.4f));
            if(level.isClientSide()) {
                System.out.println("Client-side special dagger attack triggered.");
            }
            boolean isFirstPerson = Minecraft.getInstance().options.getCameraType().isFirstPerson();
            if (isFirstPerson) {
                triggerAnim(player, GeoItem.getId(player.getItemInHand(hand)), "Controller", "special_attack_trigger");
                System.out.println("Playing special dagger attack animation for 1st person");
            } else {
                if (level.isClientSide){
                    //triggerAnim(player, GeoItem.getId(player.getItemInHand(hand)), "Controller", "idle_trigger");
                    System.out.println("Playing special dagger attack animation for 3rd person");
                // --- PLAYER ANIMATOR (ESPECIAL) ---
                playPlayerAnimatorAnim(player, "dagger_special_attack");
                }
                player.getItemInHand(hand).getOrCreateTag().putLong("LastSpecialAtk", System.currentTimeMillis());
                // Actualizamos LastHitTime para evitar que el TickHandler borre la animación
                player.getItemInHand(hand).getOrCreateTag().putLong(LAST_HIT_TIME_TAG, System.currentTimeMillis());
            }
            return InteractionResultHolder.sidedSuccess(player.getItemInHand(hand), level.isClientSide());
        }
        return super.use(level, player, hand);
    }

    @Override
    public boolean isComboWindowExpired(ItemStack stack, long currentTime) {
        CompoundTag tag = stack.getTag();
        if (tag != null && tag.contains(LAST_HIT_TIME_TAG)) {
            long lastHit = tag.getLong(LAST_HIT_TIME_TAG);
            // Retorna true si pasó mas tiempo del permitido (2000ms)
            return (currentTime - lastHit > COMBO_RESET_TIME_MS);
        }
        // Si no hay tag de tiempo, no hay combo activo que expirar, retornamos false
        // (o true si quieres ser estricto, pero false mantiene la lógica anterior de "no hacer nada si no hay tag")
        return false;
    }



    // ... Resto de métodos (registerControllers, predicate, renderer, getAttackSound) iguales ...
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