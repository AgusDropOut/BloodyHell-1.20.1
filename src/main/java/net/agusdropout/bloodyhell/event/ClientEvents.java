package net.agusdropout.bloodyhell.event;

import com.google.common.collect.ImmutableList;
import dev.kosmx.playerAnim.api.layered.IAnimation;
import dev.kosmx.playerAnim.api.layered.ModifierLayer;
import dev.kosmx.playerAnim.minecraftApi.PlayerAnimationAccess;
import net.agusdropout.bloodyhell.BloodyHell;
import net.agusdropout.bloodyhell.client.BossBarHudOverlay;
import net.agusdropout.bloodyhell.client.CrimsonVeilHudOverlay;

import net.agusdropout.bloodyhell.client.VisceralEffectHudOverlay;
import net.agusdropout.bloodyhell.client.render.BloodDimensionRenderInfo;
import net.agusdropout.bloodyhell.entity.ModEntityTypes;
import net.agusdropout.bloodyhell.entity.client.*;
import net.agusdropout.bloodyhell.entity.client.layer.BloodFireLayer;
import net.agusdropout.bloodyhell.entity.custom.CyclopsEntity;
import net.agusdropout.bloodyhell.entity.effects.EntityCameraShake;
import net.agusdropout.bloodyhell.event.handlers.RitualAmbienceHandler;
import net.agusdropout.bloodyhell.item.ModItems;
import net.agusdropout.bloodyhell.item.client.OffhandDaggerLayer;
import net.agusdropout.bloodyhell.item.custom.IComboWeapon;
import net.agusdropout.bloodyhell.particle.ModParticles;
import net.agusdropout.bloodyhell.particle.custom.*;
import net.agusdropout.bloodyhell.util.ClientTickHandler;
import net.agusdropout.bloodyhell.util.ShaderUtils;
import net.agusdropout.bloodyhell.util.WindController;
import net.agusdropout.bloodyhell.worldgen.dimension.ModDimensions;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.DimensionSpecialEffects;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.DefaultAttributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.*;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.List;
import java.util.stream.Collectors;

public class ClientEvents {

    // --- FORGE BUS EVENTS ---
    @Mod.EventBusSubscriber(modid = BloodyHell.MODID, value = Dist.CLIENT)
    public static class ClientForgeEvents {
        // ... (Keep existing ClientForgeEvents code exactly as is) ...
        @SubscribeEvent
        public static void clientTickEvent(TickEvent.ClientTickEvent event) {
            if (event.phase == TickEvent.Phase.START) {
                ClientTickHandler.ticksInGame++;
                WindController.tick();

                LocalPlayer player = Minecraft.getInstance().player;
                if (player == null) return;

                // NOTA: No llamamos a RitualAmbienceHandler.tick() aquí porque
                // esa clase tiene su propio @SubscribeEvent para el tick.

                // --- PLAYER ANIMATION LOGIC ---
                var animationLayer = (ModifierLayer<IAnimation>) PlayerAnimationAccess
                        .getPlayerAssociatedData((AbstractClientPlayer) player)
                        .get(new ResourceLocation(BloodyHell.MODID, "animation"));

                if (animationLayer == null) return;

                ItemStack stack = player.getMainHandItem();

                // CASE 1: ITEM SWITCH
                if (!(stack.getItem() instanceof IComboWeapon comboWeapon)) {
                    if (animationLayer.getAnimation() != null) {
                        animationLayer.setAnimation(null);
                    }
                    return;
                }

                // CASE 2: COMBO WINDOW EXPIRED
                if (comboWeapon.isComboWindowExpired(stack, System.currentTimeMillis())) {
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

            // 1. CYCLOPS ZOOM LOGIC (Multiplicative)
            float cyclopsZoomMultiplier = 1.0f;
            CyclopsEntity cyclops = player.level().getEntitiesOfClass(CyclopsEntity.class, player.getBoundingBox().inflate(64.0))
                    .stream().findFirst().orElse(null);

            if (cyclops != null) {
                int chargeTicks = cyclops.getClientSideAttackTicks();
                if (chargeTicks > 0) {
                    float chargeRatio = (float) chargeTicks / (float) CyclopsEntity.ATTACK_CHARGE_TIME_TICKS;
                    cyclopsZoomMultiplier = Mth.lerp(chargeRatio, 1.0f, 0.85f);
                }
            }

            // 2. RITUAL ELDRITCH LOGIC (Additive / Distortion)
            // Llamamos al helper estático de la nueva clase externa
            float ritualFovDistortion = RitualAmbienceHandler.getFovModifier(player);

            // 3. COMBINE BOTH WITHOUT CONFLICT
            // Formula: (BaseFOV * Multiplier) + AdditiveDistortion
            event.setFOV((event.getFOV() * cyclopsZoomMultiplier) + ritualFovDistortion);
        }

        @Mod.EventBusSubscriber(modid = BloodyHell.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
        public class ClientPlayerRenderEvents {

            @SubscribeEvent
            public static void onRenderLivingPre(RenderLivingEvent.Pre<?, ?> event) {
                if (event.getEntity() instanceof Player player) {
                    if (event.getRenderer() instanceof PlayerRenderer playerRenderer) {
                        PlayerModel<?> model = playerRenderer.getModel();

                        // 3. CASE: CHESTPLATE
                        if (player.getItemBySlot(EquipmentSlot.CHEST).is(ModItems.BLASPHEMITE_CHESTPLATE.get()) ||
                                player.getItemBySlot(EquipmentSlot.CHEST).is(ModItems.BLOOD_CHESTPLATE.get()) ||
                                player.getItemBySlot(EquipmentSlot.CHEST).is(ModItems.RHNULL_CHESTPLATE.get())) {

                            model.jacket.visible = false;
                            model.leftSleeve.visible = false;
                            model.rightSleeve.visible = false;
                        }

                        // 4. CASE: LEGGINGS and BOOTS
                        if (player.getItemBySlot(EquipmentSlot.LEGS).is(ModItems.BLASPHEMITE_LEGGINGS.get()) ||
                                player.getItemBySlot(EquipmentSlot.LEGS).is(ModItems.BLOOD_LEGGINGS.get()) ||
                                player.getItemBySlot(EquipmentSlot.FEET).is(ModItems.BLASPHEMITE_BOOTS.get()) ||
                                player.getItemBySlot(EquipmentSlot.FEET).is(ModItems.BLOOD_BOOTS.get()) ||
                                player.getItemBySlot(EquipmentSlot.LEGS).is(ModItems.RHNULL_LEGGINGS.get()) ||
                                player.getItemBySlot(EquipmentSlot.FEET).is(ModItems.RHNULL_BOOTS.get())) {

                            model.leftPants.visible = false;
                            model.rightPants.visible = false;
                        }

                        // 5. CASE: HELMET
                        if (player.getItemBySlot(EquipmentSlot.HEAD).is(ModItems.BLASPHEMITE_HELMET.get()) ||
                                player.getItemBySlot(EquipmentSlot.HEAD).is(ModItems.BLOOD_HELMET.get()) ||
                                player.getItemBySlot(EquipmentSlot.HEAD).is(ModItems.RHNULL_HELMET.get())) {

                            model.hat.visible = false;
                            model.head.visible = false;
                        }
                    }
                }
            }

            @SubscribeEvent
            public static void onRenderDebugText(CustomizeGuiOverlayEvent.DebugText event) {
                // Add a line to the LEFT side of the F3 screen
                event.getLeft().add(String.format("[BloodyHell] Shaders Active: %s",
                        ShaderUtils.areShadersActive() ? "§aTRUE" : "§cFALSE"));
            }

            @SubscribeEvent
            public static void onRenderLivingPost(RenderLivingEvent.Post<?, ?> event) {
                if (event.getEntity() instanceof Player) {
                    if (event.getRenderer() instanceof PlayerRenderer playerRenderer) {
                        PlayerModel<?> model = playerRenderer.getModel();
                        model.jacket.visible = true;
                        model.leftSleeve.visible = true;
                        model.rightSleeve.visible = true;
                        model.leftPants.visible = true;
                        model.rightPants.visible = true;
                        model.hat.visible = true;
                        model.head.visible = true;
                    }
                }
            }
        }

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

    // --- MOD BUS EVENTS ---
    @Mod.EventBusSubscriber(modid = BloodyHell.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class ClientModBusEvents {
        // ... (Keep existing registration events) ...

        @SubscribeEvent
        public static void registerLayerDefinitions(EntityRenderersEvent.AddLayers event) {
            if (event.getSkin("default") instanceof PlayerRenderer renderer) {
                renderer.addLayer(new OffhandDaggerLayer(renderer));
            }
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
            event.registerSpecial(ModParticles.BLACK_HOLE_PARTICLE.get(), new BlackHoleParticle.Provider());
            event.registerSpecial(ModParticles.MAGIC_WAVE_PARTICLE.get(), new MagicWaveParticle.Provider());
            event.registerSpecial(ModParticles.BLOOD_RUNE_PARTICLE.get(), new BloodRuneParticle.Provider());
            event.registerSpecial(ModParticles.SIMPLE_BLOCK_PARTICLE.get(), new SimpleBlockParticle.Provider());
            event.registerSpriteSet(ModParticles.CYCLOPS_HALO_PARTICLE.get(), CyclopsHaloParticle.Provider::new);
            event.registerSpriteSet(ModParticles.EYE_PARTICLE.get(), EyeParticle.Provider::new);
            event.registerSpriteSet(ModParticles.SHOCKWAVE_RING.get(), ShockwaveParticle.Provider::new);
            event.registerSpriteSet(ModParticles.BLOOD_PULSE_PARTICLE.get(), BloodPulseParticle.Provider::new);
            event.registerSpriteSet(ModParticles.BLOOD_FLAME.get(), BloodFlameParticle.Provider::new);
            event.registerSpriteSet(ModParticles.CHILL_FLAME_PARTICLE.get(), ChillFlameParticle.Provider::new);
            event.registerSpriteSet(ModParticles.MAGIC_PARTICLE.get(), MagicParticle.Provider::new);
            event.registerSpriteSet(ModParticles.MAGIC_FLOOR_PARTICLE.get(), MagicFloorParticle.Provider::new);
            event.registerSpriteSet(ModParticles.BLOOD_SIGIL_PARTICLE.get(), BloodSigilParticle.Provider::new);
            event.registerSpriteSet(ModParticles.SMALL_BLOOD_FLAME_PARTICLE.get(), SmallBloodFlameParticle.Provider::new);

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
            event.registerLayerDefinition(TentacleEntityModel.LAYER_LOCATION, TentacleEntityModel::createBodyLayer);
            event.registerLayerDefinition(SmallCrimsonDaggerModel.LAYER_LOCATION, SmallCrimsonDaggerModel::createBodyLayer);
            event.registerLayerDefinition(BloodFireMeteorModel.LAYER_LOCATION, BloodFireMeteorModel::createBodyLayer);

        }

        @SubscribeEvent
        public static void registerDimensionSpecialEffects(RegisterDimensionSpecialEffectsEvent event) {
            event.register(ModDimensions.DIMENSION_RENDERER,
                    new BloodDimensionRenderInfo(-189.0F, false, DimensionSpecialEffects.SkyType.NONE, false, false));
        }

        @SubscribeEvent
        public static void addEntityLayers(EntityRenderersEvent.AddLayers event) {
            System.out.println("DEBUG: Starting AddLayers event...");

            // 1. PLAYERS
            for (String skinType : event.getSkins()) {
                LivingEntityRenderer<Player, EntityModel<Player>> renderer = event.getSkin(skinType);
                if (renderer != null) {
                    renderer.addLayer(new BloodFireLayer<>(renderer));
                    System.out.println("DEBUG: Added BloodFireLayer to Player skin: " + skinType);
                }
            }

            // 2. MOBS
            List<EntityType<? extends LivingEntity>> entityTypes = ImmutableList.copyOf(
                    ForgeRegistries.ENTITY_TYPES.getValues().stream()
                            .filter(DefaultAttributes::hasSupplier)
                            .map(entityType -> (EntityType<? extends LivingEntity>) entityType)
                            .collect(Collectors.toList())
            );

            System.out.println("DEBUG: Found " + entityTypes.size() + " applicable living entities.");
            entityTypes.forEach(entityType -> addLayerIfApplicable(entityType, event));
        }

        private static void addLayerIfApplicable(EntityType<? extends LivingEntity> entityType, EntityRenderersEvent.AddLayers event) {
            LivingEntityRenderer renderer = null;
            if (entityType != EntityType.ENDER_DRAGON) {
                try {
                    renderer = event.getRenderer(entityType);
                } catch (Exception e) {
                    System.out.println("DEBUG: Failed to get renderer for " + ForgeRegistries.ENTITY_TYPES.getKey(entityType));
                }

                if (renderer != null) {
                    try {
                        renderer.addLayer(new BloodFireLayer<>(renderer));
                        // DEBUG: Success for specific mob
                         System.out.println("DEBUG: Added BloodFireLayer to: " + ForgeRegistries.ENTITY_TYPES.getKey(entityType));
                    } catch (Exception e) {
                        System.out.println("DEBUG: Error adding layer to " + ForgeRegistries.ENTITY_TYPES.getKey(entityType) + ": " + e.getMessage());
                    }
                }
            }
        }
    }
}