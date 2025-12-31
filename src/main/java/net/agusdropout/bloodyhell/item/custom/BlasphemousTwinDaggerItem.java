package net.agusdropout.bloodyhell.item.custom;

import net.agusdropout.bloodyhell.CrimsonveilPower.PlayerCrimsonveilProvider;
import net.agusdropout.bloodyhell.item.client.BlasphemousTwinDaggersRenderer;
import net.agusdropout.bloodyhell.networking.ModMessages;
import net.agusdropout.bloodyhell.networking.packet.CrimsonVeilDataSyncS2CPacket;
import net.agusdropout.bloodyhell.sound.ModSounds;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraftforge.common.ToolAction;
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

import java.util.function.Consumer;

public class BlasphemousTwinDaggerItem extends Item implements GeoItem {

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    // --- ANIMACIONES GECKOLIB ---
    private static final RawAnimation ATTACK_1 = RawAnimation.begin().thenPlayAndHold("attack_1");
    private static final RawAnimation ATTACK_2 = RawAnimation.begin().thenPlayAndHold("attack_2");
    private static final RawAnimation ATTACK_3 = RawAnimation.begin().thenPlay("attack_3");
    private static final RawAnimation SPECIAL_ATK = RawAnimation.begin().thenPlay("special_attack");
    private static final RawAnimation IDLE_ANIM = RawAnimation.begin().thenLoop("idle");

    public BlasphemousTwinDaggerItem(Properties properties) {
        super(properties);
        SingletonGeoAnimatable.registerSyncedAnimatable(this);
    }

    @Override
    public boolean isPerspectiveAware() {
        return true;
    }

    // --- LÓGICA DE COMBO (NBT) ---
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
        if (currentCombo > 3) {
            currentCombo = 1;
        }

        tag.putInt(COMBO_TAG, currentCombo);
        tag.putLong(LAST_HIT_TIME_TAG, currentTime);

        return currentCombo;
    }

    public int predictNextCombo(ItemStack stack) {
        CompoundTag tag = stack.getOrCreateTag();
        long lastTime = tag.getLong(LAST_HIT_TIME_TAG);
        int currentCombo = tag.getInt(COMBO_TAG);
        long currentTime = System.currentTimeMillis();

        if (currentTime - lastTime > COMBO_RESET_TIME_MS) {
            return 1;
        }

        int next = currentCombo + 1;
        return (next > 3) ? 1 : next;
    }


    // --- EVENTO DE GOLPE (CLICK IZQUIERDO) ---
    @Override
    public boolean onEntitySwing(ItemStack stack, LivingEntity entity) {
        if (entity instanceof Player player) {

            // 1. Check de Cooldown (Anti-Spam)
            if (player.getCooldowns().isOnCooldown(this)) {
                return true;
            }

            // 2. Calculamos el combo
            int comboStep = updateAndGetCombo(stack);

            // 3. Aplicamos el Cooldown
            int cooldownDuration = switch (comboStep) {
                case 1 -> 20;
                case 2 -> 15;
                case 3 -> 20;
                default -> 20;
            };
            player.getCooldowns().addCooldown(this, cooldownDuration);

            // --- SONIDO MEJORADO (Variación y Volumen) ---
            // Lo ejecutamos en el SERVIDOR (!isClientSide) para evitar "ecos" (doble sonido)
            // y usamos level.playSound(null, ...) para que lo escuchen todos MENOS el que lo emite
            // (el cliente lo predice o player.playSound lo maneja, pero esta es la forma segura de sync).
            if (!player.level().isClientSide) {

                // VOLUMEN: 0.4f a 0.5f es ideal para armas rápidas (no te taladra el oído).
                float volumen = 0.5f;

                // PITCH (Tono): Variamos entre 0.9 (grave) y 1.1 (agudo).
                // Esto hace que cada golpe suene único y no sea molesto.
                float pitch = 0.9f + (player.getRandom().nextFloat() * 0.2f);

                player.level().playSound(null, player.getX(), player.getY(), player.getZ(),
                        this.getAttackSound(), net.minecraft.sounds.SoundSource.PLAYERS, volumen, pitch);
            }


            // 4. Lógica de Animación (Cliente)
            boolean isFirstPerson = false;
            if (player.level().isClientSide) {
                isFirstPerson = Minecraft.getInstance().options.getCameraType().isFirstPerson();

                // Opcional: Si sientes que falta feedback inmediato en el cliente,
                // puedes poner un playSound local aquí con volumen muy bajo, pero el del server suele bastar.

                if(isFirstPerson) {
                    String animName = switch (comboStep) {
                        case 2 -> "attack_2_trigger";
                        case 3 -> "attack_3_trigger";
                        default -> "attack_1_trigger";
                    };
                    triggerAnim(player, GeoItem.getId(stack), "Controller", animName);
                } else{
                    triggerAnim(player, GeoItem.getId(stack), "Controller", "idle_trigger");
                }
            }
        }
        return true;
    }

    // --- EVENTO DE ATAQUE ESPECIAL (CLICK DERECHO) ---
    // MANTENIENDO TU LÓGICA Y CORRECCIONES INTACTAS
    @Override
    public InteractionResultHolder<ItemStack> use(net.minecraft.world.level.Level level, Player player, InteractionHand hand) {
        if (hand == InteractionHand.MAIN_HAND) {

            player.getCapability(PlayerCrimsonveilProvider.PLAYER_CRIMSONVEIL).ifPresent(playerCrimsonVeil -> {
                if(playerCrimsonVeil.getCrimsonVeil() >= 5 && !player.getCooldowns().isOnCooldown(this)) {

                    if (!level.isClientSide()) {
                        playerCrimsonVeil.subCrimsomveil(5);
                        ModMessages.sendToPlayer(new CrimsonVeilDataSyncS2CPacket(playerCrimsonVeil.getCrimsonVeil()), ((ServerPlayer) player));

                        // Cooldown del especial (50 ticks)
                        player.getCooldowns().addCooldown(this, 50);
                    }

                    // System.out.println("First Person Special Attack Triggered"); // Debug opcional


                        boolean isFirstPerson = Minecraft.getInstance().options.getCameraType().isFirstPerson();

                        if (isFirstPerson) {
                            triggerAnim(player, GeoItem.getId(player.getItemInHand(hand)), "Controller", "special_attack_trigger");
                        } else {
                            triggerAnim(player, GeoItem.getId(player.getItemInHand(hand)), "Controller", "idle_trigger");
                        }

                }
            });

            return InteractionResultHolder.sidedSuccess(player.getItemInHand(hand), level.isClientSide());
        }
        return super.use(level, player, hand);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "Controller", 0, this::predicate)
                .triggerableAnim("attack_1_trigger", ATTACK_1)
                .triggerableAnim("attack_2_trigger", ATTACK_2)
                .triggerableAnim("attack_3_trigger", ATTACK_3)
                .triggerableAnim("special_attack_trigger", SPECIAL_ATK)
                .triggerableAnim("idle_trigger", IDLE_ANIM));
    }

    private PlayState predicate(AnimationState<BlasphemousTwinDaggerItem> state) {
        ItemDisplayContext context = state.getData(DataTickets.ITEM_RENDER_PERSPECTIVE);

        // Seguridad básica
        if (context == null) return PlayState.STOP;

        // 1. Detectar si es Primera Persona
        boolean isFirstPerson = (context == ItemDisplayContext.FIRST_PERSON_RIGHT_HAND ||
                context == ItemDisplayContext.FIRST_PERSON_LEFT_HAND);

        // 2. Si es Tercera persona -> APAGAR (Resetear huesos)
        if (!isFirstPerson) {
            state.getController().forceAnimationReset();
            return PlayState.STOP;
        }

        // 3. LÓGICA DE RESETEO POR TIEMPO (NUEVO)
        // Obtenemos el jugador cliente para ver su NBT
        Player player = Minecraft.getInstance().player;
        if (player != null) {
            ItemStack heldStack = player.getMainHandItem();

            // Verificamos que sea ESTE ítem el que tiene en la mano
            if (heldStack.getItem() == this) {
                CompoundTag tag = heldStack.getTag();
                if (tag != null && tag.contains(LAST_HIT_TIME_TAG)) {
                    long lastHit = tag.getLong(LAST_HIT_TIME_TAG);

                    // Si pasaron más de 4 segundos desde el último golpe...
                    if (System.currentTimeMillis() - lastHit > COMBO_RESET_TIME_MS) {
                        // ... Forzamos la animación IDLE (o STOP) para salir del "Hold"
                        return state.setAndContinue(IDLE_ANIM);
                    }
                }
            }
        }

        // Si estamos en tiempo de combo, seguimos normal
        return PlayState.CONTINUE;
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }

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