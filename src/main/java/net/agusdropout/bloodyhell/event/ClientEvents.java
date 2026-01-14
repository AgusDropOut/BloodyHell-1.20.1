package net.agusdropout.bloodyhell.event;

import com.mojang.math.Axis;
import dev.kosmx.playerAnim.api.layered.IAnimation;
import dev.kosmx.playerAnim.api.layered.KeyframeAnimationPlayer;
import dev.kosmx.playerAnim.api.layered.ModifierLayer;
import dev.kosmx.playerAnim.api.layered.modifier.AbstractFadeModifier;
import dev.kosmx.playerAnim.core.util.Ease;
import dev.kosmx.playerAnim.minecraftApi.PlayerAnimationAccess;
import dev.kosmx.playerAnim.minecraftApi.PlayerAnimationFactory;
import dev.kosmx.playerAnim.minecraftApi.PlayerAnimationRegistry;
import net.agusdropout.bloodyhell.BloodyHell;
import net.agusdropout.bloodyhell.animation.ModAnimations;
import net.agusdropout.bloodyhell.block.ModBlocks;
import net.agusdropout.bloodyhell.block.entity.ModBlockEntities;
import net.agusdropout.bloodyhell.client.BossBarHudOverlay;
import net.agusdropout.bloodyhell.client.CrimsonVeilHudOverlay;
import net.agusdropout.bloodyhell.client.VisceralEffectHudOverlay;
import net.agusdropout.bloodyhell.client.render.BloodDimensionRenderInfo;
import net.agusdropout.bloodyhell.entity.ModEntityTypes;
import net.agusdropout.bloodyhell.entity.client.*;
import net.agusdropout.bloodyhell.entity.custom.CyclopsEntity;
import net.agusdropout.bloodyhell.entity.effects.EntityCameraShake;
import net.agusdropout.bloodyhell.fluid.ModFluids;
import net.agusdropout.bloodyhell.item.ModItems;
import net.agusdropout.bloodyhell.item.client.OffhandDaggerLayer;
import net.agusdropout.bloodyhell.item.custom.BlasphemousTwinDaggerItem;
import net.agusdropout.bloodyhell.item.custom.IComboWeapon;
import net.agusdropout.bloodyhell.particle.ModParticles;
import net.agusdropout.bloodyhell.particle.custom.*;
import net.agusdropout.bloodyhell.screen.BloodWorkBenchScreen;
import net.agusdropout.bloodyhell.screen.ModMenuTypes;
import net.agusdropout.bloodyhell.screen.VesperScreen;
import net.agusdropout.bloodyhell.util.ClientTickHandler;
import net.agusdropout.bloodyhell.util.WindController;
import net.agusdropout.bloodyhell.worldgen.dimension.ModDimensions;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.DimensionSpecialEffects;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.*;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

public class ClientEvents {

    // --- BUS DE FORGE ---
    @Mod.EventBusSubscriber(modid = BloodyHell.MODID, value = Dist.CLIENT)
    public static class ClientForgeEvents {

        @SubscribeEvent
        public static void clientTickEvent(TickEvent.ClientTickEvent event) {
            if (event.phase == TickEvent.Phase.START) {
                ClientTickHandler.ticksInGame++;
                WindController.tick();

                LocalPlayer player = Minecraft.getInstance().player;
                if (player == null) return;

                // Acceder al sistema de animación
                var animationLayer = (ModifierLayer<IAnimation>) PlayerAnimationAccess
                        .getPlayerAssociatedData((AbstractClientPlayer) player)
                        .get(new ResourceLocation(BloodyHell.MODID, "animation"));

                // Si no hay layer, no hacemos nada
                if (animationLayer == null) return;

                ItemStack stack = player.getMainHandItem();

                // CASO 1: CAMBIO DE ÍTEM
                // Verificamos si el ítem NO es una IComboWeapon
                if (!(stack.getItem() instanceof IComboWeapon comboWeapon)) {
                    // ... y hay una animación sonando...
                    if (animationLayer.getAnimation() != null) {
                        // ... la borramos (null) para volver a la animación vanilla
                        animationLayer.setAnimation(null);
                    }
                    return;
                }

                // CASO 2: TIEMPO DE COMBO EXPIRADO
                // Como ya sabemos que es IComboWeapon (gracias al instanceof de arriba), usamos su método.
                if (comboWeapon.isComboWindowExpired(stack, System.currentTimeMillis())) {
                    // Si el arma dice que expiró y hay animación activa, la quitamos
                    if (animationLayer.getAnimation() != null) {
                        animationLayer.setAnimation(null);
                    }
                }
            }
        }



        @SubscribeEvent
        public static void onComputeFov(ViewportEvent.ComputeFov event) {
            Player player = (Player) event.getCamera().getEntity();
            if (player == null) return;

            CyclopsEntity cyclops = player.level().getEntitiesOfClass(CyclopsEntity.class, player.getBoundingBox().inflate(64.0))
                    .stream().findFirst().orElse(null);

            if (cyclops != null) {
                int chargeTicks = cyclops.getClientSideAttackTicks();
                if (chargeTicks > 0) {
                    float chargeRatio = (float) chargeTicks / (float) CyclopsEntity.ATTACK_CHARGE_TIME_TICKS;
                    float zoomIntensity = Mth.lerp(chargeRatio, 1.0f, 0.85f);
                    event.setFOV(event.getFOV() * zoomIntensity);
                }
            }
        }

        @Mod.EventBusSubscriber(modid = BloodyHell.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
        public class ClientPlayerRenderEvents {

            @SubscribeEvent
            public static void onRenderLivingPre(RenderLivingEvent.Pre<?, ?> event) {
                // 1. Verificamos si la entidad es un Jugador
                if (event.getEntity() instanceof Player player) {

                    // 2. Obtenemos el renderizador y el modelo del jugador
                    if (event.getRenderer() instanceof PlayerRenderer playerRenderer) {
                        PlayerModel<?> model = playerRenderer.getModel();

                        // 3. CASO: PECHERA (Chestplate)
                        // Chequeamos si es Blasphemite O (||) Blood (Sanguinite)
                        if (player.getItemBySlot(EquipmentSlot.CHEST).is(ModItems.BLASPHEMITE_CHESTPLATE.get()) ||
                                player.getItemBySlot(EquipmentSlot.CHEST).is(ModItems.BLOOD_CHESTPLATE.get()) || player.getItemBySlot(EquipmentSlot.CHEST).is(ModItems.RHNULL_CHESTPLATE.get())) {

                            model.jacket.visible = false;      // Capa del cuerpo
                            model.leftSleeve.visible = false;  // Manga izquierda
                            model.rightSleeve.visible = false; // Manga derecha
                        }

                        // 4. CASO: PANTALONES (Leggings) y BOTAS
                        // Chequeamos Blasphemite O Blood en piernas o pies
                        if (player.getItemBySlot(EquipmentSlot.LEGS).is(ModItems.BLASPHEMITE_LEGGINGS.get()) ||
                                player.getItemBySlot(EquipmentSlot.LEGS).is(ModItems.BLOOD_LEGGINGS.get()) ||
                                player.getItemBySlot(EquipmentSlot.FEET).is(ModItems.BLASPHEMITE_BOOTS.get()) ||
                                player.getItemBySlot(EquipmentSlot.FEET).is(ModItems.BLOOD_BOOTS.get()) ||
                                player.getItemBySlot(EquipmentSlot.LEGS).is(ModItems.RHNULL_LEGGINGS.get()) ||
                                player.getItemBySlot(EquipmentSlot.FEET).is(ModItems.RHNULL_BOOTS.get())) {

                            model.leftPants.visible = false;
                            model.rightPants.visible = false;
                        }

                        // 5. CASO: CASCO
                        if (player.getItemBySlot(EquipmentSlot.HEAD).is(ModItems.BLASPHEMITE_HELMET.get()) ||
                                player.getItemBySlot(EquipmentSlot.HEAD).is(ModItems.BLOOD_HELMET.get()) ||
                                player.getItemBySlot(EquipmentSlot.HEAD).is(ModItems.RHNULL_HELMET.get())) {

                            model.hat.visible = false; // La capa externa (Overlay)

                            // NOTA: Solo deja esta línea si el casco tapa TODA la cara.
                            // Si el casco deja ver la cara del jugador, borra 'model.head.visible = false'.
                             model.head.visible = false;
                        }
                    }
                }
            }

            @SubscribeEvent
            public static void onRenderLivingPost(RenderLivingEvent.Post<?, ?> event) {
                // Restaurar visibilidad
                if (event.getEntity() instanceof Player) {
                    if (event.getRenderer() instanceof PlayerRenderer playerRenderer) {
                        PlayerModel<?> model = playerRenderer.getModel();

                        model.jacket.visible = true;
                        model.leftSleeve.visible = true;
                        model.rightSleeve.visible = true;
                        model.leftPants.visible = true;
                        model.rightPants.visible = true;
                        model.hat.visible = true;
                        model.head.visible = true; // Restauramos la cabeza por si acaso
                    }
                }
            }
        }

        // El evento de CameraShake (lo dejé tal cual estaba)
        @SubscribeEvent
        public static void onSetupCamera(ViewportEvent.ComputeCameraAngles event) {
            Player player = Minecraft.getInstance().player;
            if (player == null) return;

            float delta = Minecraft.getInstance().getFrameTime();
            float ticksExistedDelta = player.tickCount + delta;

            if(!Minecraft.getInstance().isPaused()) {
                float shakeAmplitude = 0;
                for (EntityCameraShake cameraShake : player.level().getEntitiesOfClass(EntityCameraShake.class, player.getBoundingBox().inflate(20, 20, 20))) {
                    if (cameraShake.distanceTo(player) < cameraShake.getRadius()) {
                        shakeAmplitude += cameraShake.getShakeAmount(player, delta);
                    }
                }
                if (shakeAmplitude > 1.0f) shakeAmplitude = 1.0f;
                event.setPitch((float) (event.getPitch() + shakeAmplitude * Math.cos(ticksExistedDelta * 3 + 2) * 25));
                event.setYaw((float) (event.getYaw() + shakeAmplitude * Math.cos(ticksExistedDelta * 5 + 1) * 25));
                event.setRoll((float) (event.getRoll() + shakeAmplitude * Math.cos(ticksExistedDelta * 4) * 25));
            }
        }
    }

    // --- BUS DEL MOD (Eventos de INICIALIZACIÓN: Registro de Layers, Partículas, Overlays) ---
    @Mod.EventBusSubscriber(modid = BloodyHell.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class ClientModBusEvents {

        // --- MOVÍ ESTO AQUÍ PORQUE ES UN EVENTO DE RENDERIZADO INICIAL ---
        @SubscribeEvent
        public static void registerLayerDefinitions(EntityRenderersEvent.AddLayers event) {
            // Capa para Steve (Default)
            if (event.getSkin("default") instanceof PlayerRenderer renderer) {
                renderer.addLayer(new OffhandDaggerLayer(renderer));
            }
            // Capa para Alex (Slim)
            if (event.getSkin("slim") instanceof PlayerRenderer renderer) {
                renderer.addLayer(new OffhandDaggerLayer(renderer));
            }
        }




        @SubscribeEvent
        public static void registerParticleFactories(RegisterParticleProvidersEvent event) {
            event.registerSpriteSet(ModParticles.BLOOD_PARTICLES.get(), BloodParticles.Provider::new);
            event.registerSpriteSet(ModParticles.LIGHT_PARTICLES.get(), LightParticle.Provider::new);
            event.registerSpriteSet(ModParticles.DIRTY_BLOOD_FLOWER_PARTICLE.get(), DirtyBloodFlowerParticle.Provider::new);
            event.registerSpriteSet(ModParticles.IMPACT_PARTICLE.get(), ImpactParticle.Provider::new);
            event.registerSpriteSet(ModParticles.BLASPHEMOUS_MAGIC_RING.get(), BlasphemousMagicCircleParticle.Provider::new);
            event.registerSpriteSet(ModParticles.SLASH_PARTICLE.get(), SlashParticle.Provider::new);
            event.registerSpriteSet(ModParticles.VICERAL_PARTICLE.get(), ViceralParticle.Provider::new);
            event.registerSpriteSet(ModParticles.MAGIC_LINE_PARTICLE.get(), MagicLineParticle.Provider::new);
            event.registerSpriteSet(ModParticles.MAGIC_SIMPLE_LINE_PARTICLE.get(), MagicSimpleLineParticle.Provider::new);
            event.registerSpriteSet(ModParticles.BLASPHEMOUS_BIOME_PARTICLE.get(), BlasphemousBiomeParticle.Provider::new);
            event.registerSpecial(ModParticles.CYLINDER_PARTICLE.get(), new CylinderParticle.Provider());
            event.registerSpecial(ModParticles.STAR_EXPLOSION_PARTICLE.get(), new StarExplosionParticle.Provider());
            event.registerSpecial(ModParticles.MAGIC_WAVE_PARTICLE.get(), new MagicWaveParticle.Provider());
            event.registerSpecial(ModParticles.SIMPLE_BLOCK_PARTICLE.get(), new SimpleBlockParticle.Provider());
            event.registerSpriteSet(ModParticles.CYCLOPS_HALO_PARTICLE.get(), CyclopsHaloParticle.Provider::new);
            event.registerSpriteSet(ModParticles.EYE_PARTICLE.get(), EyeParticle.Provider::new);
            event.registerSpriteSet(ModParticles.SHOCKWAVE_RING.get(), ShockwaveParticle.Provider::new);
        }

        @SubscribeEvent
        public static void registerGuiOverlays(RegisterGuiOverlaysEvent event) {
            event.registerAbove(VanillaGuiOverlay.CROSSHAIR.id(), "mana_hud", CrimsonVeilHudOverlay.OVERLAY);
            event.registerBelow(VanillaGuiOverlay.CROSSHAIR.id(), "visceral_overlay", VisceralEffectHudOverlay.OVERLAY);
            event.registerAbove(VanillaGuiOverlay.CROSSHAIR.id(), "boss_bar", BossBarHudOverlay.OVERLAY);
        }

        @SubscribeEvent
        public static void registerEntityLayers(EntityRenderersEvent.RegisterLayerDefinitions event) {
            event.registerLayerDefinition(ModModelLayers.CRYSTAL_PILLAR, CrystalPillarModel::createBodyLayer);
            event.registerLayerDefinition(ModModelLayers.BLASPHEMOUS_IMPALER_ENTITY, BlasphemousImpalerEntityModel::createBodyLayer);
            event.registerLayerDefinition(ModModelLayers.VESPER, VesperModel::createBodyLayer);
        }

        @SubscribeEvent
        public static void registerDimensionSpecialEffects(RegisterDimensionSpecialEffectsEvent event) {
            event.register(ModDimensions.DIMENSION_RENDERER,
                    new BloodDimensionRenderInfo(-189.0F, false, DimensionSpecialEffects.SkyType.NONE, false, false));
        }
    }

}