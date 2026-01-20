package net.agusdropout.bloodyhell.event;

import dev.kosmx.playerAnim.api.layered.IAnimation;
import dev.kosmx.playerAnim.api.layered.ModifierLayer;
import dev.kosmx.playerAnim.minecraftApi.PlayerAnimationFactory;
import net.agusdropout.bloodyhell.BloodyHell;
import net.agusdropout.bloodyhell.CrimsonveilPower.PlayerCrimsonVeil;
import net.agusdropout.bloodyhell.CrimsonveilPower.PlayerCrimsonveilProvider;
import net.agusdropout.bloodyhell.client.render.BloodDimensionRenderInfo;
import net.agusdropout.bloodyhell.effect.ModEffects;
import net.agusdropout.bloodyhell.entity.ModEntityTypes;
import net.agusdropout.bloodyhell.entity.custom.*;
import net.agusdropout.bloodyhell.item.ModItems;
import net.agusdropout.bloodyhell.item.custom.BlasphemousTwinDaggerItem;
import net.agusdropout.bloodyhell.item.custom.IComboWeapon;
import net.agusdropout.bloodyhell.networking.ModMessages;
import net.agusdropout.bloodyhell.networking.packet.BossSyncS2CPacket;
import net.agusdropout.bloodyhell.networking.packet.CrimsonVeilDataSyncS2CPacket;
import net.agusdropout.bloodyhell.networking.packet.SyncRemoveBloodFirePacket;
import net.agusdropout.bloodyhell.particle.ModParticles;
import net.agusdropout.bloodyhell.particle.custom.*;
import net.agusdropout.bloodyhell.worldgen.dimension.ModDimensions;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.DimensionSpecialEffects;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.event.RegisterDimensionSpecialEffectsEvent;
import net.minecraftforge.client.event.RegisterParticleProvidersEvent;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.EntityLeaveLevelEvent;
import net.minecraftforge.event.entity.SpawnPlacementRegisterEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.living.MobEffectEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

import static net.agusdropout.bloodyhell.BloodyHell.MODID;
import static net.agusdropout.bloodyhell.entity.ModEntityTypes.*;


public class ModEvents {

        @Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
        public static class ModEventBusEvents {
            @SubscribeEvent
            public static void registerParticleFactories(final RegisterParticleProvidersEvent event) {
                Minecraft.getInstance().particleEngine.register(ModParticles.BLOOD_PARTICLES.get(), BloodParticles.Provider::new);
                event.registerSpriteSet(ModParticles.BLOOD_PARTICLES.get(), BloodParticles.Provider::new);
                Minecraft.getInstance().particleEngine.register(ModParticles.LIGHT_PARTICLES.get(), LightParticle.Provider::new);
                event.registerSpriteSet(ModParticles.LIGHT_PARTICLES.get(), LightParticle.Provider::new);
                Minecraft.getInstance().particleEngine.register(ModParticles.DIRTY_BLOOD_FLOWER_PARTICLE.get(), DirtyBloodFlowerParticle.Provider::new);
                event.registerSpriteSet(ModParticles.DIRTY_BLOOD_FLOWER_PARTICLE.get(), DirtyBloodFlowerParticle.Provider::new);
                Minecraft.getInstance().particleEngine.register(ModParticles.IMPACT_PARTICLE.get(), ImpactParticle.Provider::new);
                event.registerSpriteSet(ModParticles.IMPACT_PARTICLE.get(), ImpactParticle.Provider::new);
                Minecraft.getInstance().particleEngine.register(ModParticles.BLASPHEMOUS_MAGIC_RING.get(), ImpactParticle.Provider::new);
                event.registerSpriteSet(ModParticles.BLASPHEMOUS_MAGIC_RING.get(), BlasphemousMagicCircleParticle.Provider::new);
                Minecraft.getInstance().particleEngine.register(ModParticles.SLASH_PARTICLE.get(), SlashParticle.Provider::new);
                event.registerSpriteSet(ModParticles.SLASH_PARTICLE.get(), SlashParticle.Provider::new);
            }



            @SubscribeEvent
            public static void onClientSetup(FMLClientSetupEvent event)
            {
                //Set the player construct callback. It can be a lambda function.
                PlayerAnimationFactory.ANIMATION_DATA_FACTORY.registerFactory(
                        new ResourceLocation(MODID, "animation"),
                        42,
                        ModEventBusEvents::registerPlayerAnimation);
            }

            //This method will set your mods animation into the library.
            private static IAnimation registerPlayerAnimation(AbstractClientPlayer player) {
                //This will be invoked for every new player
                return new ModifierLayer<>();
            }


            @SubscribeEvent
            public static void registerSpawnPlacements(SpawnPlacementRegisterEvent event) {


            }
            private void RegisterDimensionSpecialEffectsEvent(RegisterDimensionSpecialEffectsEvent event) {
                event.register(ModDimensions.DIMENSION_RENDERER, new BloodDimensionRenderInfo(-128.0F, false, DimensionSpecialEffects.SkyType.NONE, false, false));
            }




            @SubscribeEvent
            public static void EntityAttributeEvent(EntityAttributeCreationEvent event) {
                event.put(BLOODTHIRSTYBEAST.get(), BloodThirstyBeastEntity.setAttributes());
                event.put(BLASPHEMOUS_ARM_ENTITY.get(), BlasphemousArmEntity.setAttributes());
                event.put(BLOOD_SEEKER.get(), BloodSeekerEntity.setAttributes());
                event.put(OMEN_GAZER_ENTITY.get(), OmenGazerEntity.setAttributes());
                event.put(VEINRAVER_ENTITY.get(), VeinraverEntity.setAttributes());
                event.put(BLOODY_SOUL_ENTITY.get(), BloodySoulEntity.setAttributes());
                event.put(OFFSPRING_OF_THE_UNKNOWN.get(), OffspringOfTheUnknownEntity.setAttributes());
                event.put(SELIORA.get(), SelioraEntity.setAttributes());
                event.put(BLASPHEMOUS_MALFORMATION.get(), BlasphemousMalformationEntity.setAttributes());
                event.put(GRAVE_WALKER_ENTITY.get(), GraveWalkerEntity.setAttributes());
                event.put(HORNED_WORM.get(), HornedWormEntity.setAttributes());
                event.put(CYCLOPS_ENTITY.get(), CyclopsEntity.setAttributes());
                event.put(VEIL_STALKER.get(), VeilStalkerEntity.setAttributes());
                event.put(CORRUPTED_BLOODY_SOUL_ENTITY.get(), CorruptedBloodySoulEntity.setAttributes());
                event.put(CRIMSON_RAVEN.get(), CrimsonRavenEntity.setAttributes());
                event.put(EYESHELL_SNAIL.get(), EyeshellSnailEntity.setAttributes());
                event.put(SCARLETSPECKLED_FISH.get(), ScarletSpeckledFishEntity.setAttributes());
                event.put(BLOODPIG.get(), BloodPigEntity.setAttributes());
                event.put(ONI.get(), OniEntity.setAttributes());
                event.put(VESPER.get(), VesperEntity.setAttributes());
                event.put(ModEntityTypes.RITEKEEPER.get(), RitekeeperEntity.createAttributes().build());
                event.put(ModEntityTypes.BLASPHEMOUS_TWIN_DAGGERS_CLONE.get(), BlasphemousTwinDaggersCloneEntity.createAttributes().build());


            }



            @SubscribeEvent
            public static void commonSetup(FMLCommonSetupEvent event) {
                event.enqueueWork(() -> {



                });


            }
        }
        @Mod.EventBusSubscriber(modid = MODID)
        public static class ForgeEvents{
            @SubscribeEvent
            public static void onAttachCapabilitiesPlayer(AttachCapabilitiesEvent<Entity> event) {
                if(event.getObject() instanceof Player) {
                    if(!event.getObject().getCapability(PlayerCrimsonveilProvider.PLAYER_CRIMSONVEIL).isPresent()) {
                        event.addCapability(new ResourceLocation(MODID, "properties"), new PlayerCrimsonveilProvider());
                    }
                }
            }

            @SubscribeEvent
            public static void onLivingHurt(LivingHurtEvent event) {
                // 1. Verificamos que el causante del daño sea un Jugador
                // (getEntity() devuelve al atacante, incluso si es un proyectil, pero aquí nos centramos en melee)
                if (event.getSource().getEntity() instanceof Player player) {

                    // 2. Obtenemos el ítem que tiene en la mano principal
                    ItemStack mainHandStack = player.getMainHandItem();

                    // 3. MAGIA DE LA EXTENSIBILIDAD:
                    // Verificamos si el ítem implementa nuestra interfaz genérica IComboWeapon.
                    // No importa si es Daga, Espada, Hacha o un Pescado, si tiene la interfaz, funciona.
                    if (mainHandStack.getItem() instanceof IComboWeapon comboWeapon) {

                        // 4. Le pedimos al ítem que calcule su propio bonus
                        // (El ítem revisará su NBT, su combo actual, etc. y nos dará un número)
                        float bonusDamage = comboWeapon.getComboDamageBonus(mainHandStack);

                        // 5. Si hay bonus, lo sumamos al daño total del evento
                        if (bonusDamage > 0) {
                            event.setAmount(event.getAmount() + bonusDamage);
                        }
                    }
                }
            }

            @SubscribeEvent
            public static void onPlayerAttack(AttackEntityEvent event) {
                // Obtenemos al jugador
                Player player = event.getEntity();

                // Verificamos si tiene nuestras dagas en la mano principal
                if (player.getMainHandItem().getItem() instanceof BlasphemousTwinDaggerItem) {

                    // EL FILTRO MÁGICO:
                    // Si el jugador tiene cooldown en el ítem (el que pusimos en onEntitySwing)...
                    if (player.getCooldowns().isOnCooldown(player.getMainHandItem().getItem())) {

                        // ... CANCELAMOS EL ATAQUE COMPLETAMENTE.
                        // Esto hace que:
                        // 1. La entidad no reciba daño.
                        // 2. No se envíe paquete de ataque.
                        // 3. No se reinicie la animación de swing bruscamente.
                        event.setCanceled(true);
                    }
                }
            }

            @SubscribeEvent
            public static void onPlayerCloned(PlayerEvent.Clone event) {
                if(event.isWasDeath()) {
                    event.getOriginal().reviveCaps();
                    event.getOriginal().getCapability(PlayerCrimsonveilProvider.PLAYER_CRIMSONVEIL).ifPresent(oldStore -> {
                        event.getEntity().getCapability(PlayerCrimsonveilProvider.PLAYER_CRIMSONVEIL).ifPresent(newStore -> {
                            newStore.copyFrom(oldStore);
                        });
                    });
                    event.getOriginal().invalidateCaps();
                }
            }
            @SubscribeEvent
            public static void onPlayerJoinWorld(EntityJoinLevelEvent event) {
                if(!event.getLevel().isClientSide()) {
                    if(event.getEntity() instanceof ServerPlayer player) {
                        player.getCapability(PlayerCrimsonveilProvider.PLAYER_CRIMSONVEIL).ifPresent(crimsonVeil -> {
                            ModMessages.sendToPlayer(new CrimsonVeilDataSyncS2CPacket(crimsonVeil.getCrimsonVeil()), player);
                        });
                        ModMessages.sendToPlayer(new BossSyncS2CPacket(0,0,false,false), player);
                    }
                }
            }

            @SubscribeEvent
            public static void onPlayerAbandonWorld(EntityLeaveLevelEvent event) {
                if(!event.getLevel().isClientSide()) {
                    if(event.getEntity() instanceof ServerPlayer player) {
                        ModMessages.sendToPlayer(new BossSyncS2CPacket(0,0,false,false), player);
                    }
                }
            }


            @SubscribeEvent
            public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
                if(event.side == LogicalSide.SERVER) {
                    event.player.getCapability(PlayerCrimsonveilProvider.PLAYER_CRIMSONVEIL).ifPresent(crimsonVeil -> {
                        if (crimsonVeil.getCrimsonVeil() < 100 && event.player.getRandom().nextFloat() < 0.01f) {
                            if (event.player.getInventory().contains(ModItems.GREAT_AMULET_OF_ANCESTRAL_BLOOD.get().getDefaultInstance())) {
                                crimsonVeil.addCrimsomveil(4);
                                ModMessages.sendToPlayer(new CrimsonVeilDataSyncS2CPacket(crimsonVeil.getCrimsonVeil()), ((ServerPlayer) event.player));
                            } else if (event.player.getInventory().contains(ModItems.AMULET_OF_ANCESTRAL_BLOOD.get().getDefaultInstance())) {
                                crimsonVeil.addCrimsomveil(1);
                                ModMessages.sendToPlayer(new CrimsonVeilDataSyncS2CPacket(crimsonVeil.getCrimsonVeil()), ((ServerPlayer) event.player));
                            }
                        }
                    });
                }
            }

            @SubscribeEvent
            public static void onEffectRemove(MobEffectEvent.Remove event) {
                // Check if the effect being removed is OUR effect
                if (event.getEffect() == ModEffects.BLOOD_FIRE_EFFECT.get()) {
                    LivingEntity entity = event.getEntity();

                    // Ensure we are on the Server
                    if (!entity.level().isClientSide) {
                        // Send packet to all players watching this entity
                        ModMessages.sendToPlayersTrackingEntity(new SyncRemoveBloodFirePacket(entity.getId()), entity);

                        // If the entity itself is a player, send to them too
                        if (entity instanceof net.minecraft.server.level.ServerPlayer serverPlayer) {
                            ModMessages.sendToPlayer(new SyncRemoveBloodFirePacket(entity.getId()), serverPlayer);
                        }
                    }
                }
            }

            @SubscribeEvent
            public static void onRegisterCapabilities(RegisterCapabilitiesEvent event) {
                event.register(PlayerCrimsonVeil.class);
            }


        }
}





