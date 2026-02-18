package net.agusdropout.bloodyhell.event;

import net.agusdropout.bloodyhell.CrimsonveilPower.PlayerCrimsonVeil;
import net.agusdropout.bloodyhell.CrimsonveilPower.PlayerCrimsonveilProvider;
import net.agusdropout.bloodyhell.effect.ModEffects;
import net.agusdropout.bloodyhell.entity.ModEntityTypes;
import net.agusdropout.bloodyhell.entity.custom.*;
import net.agusdropout.bloodyhell.event.handlers.BloodHarvestHandler;
import net.agusdropout.bloodyhell.item.ModItems;
import net.agusdropout.bloodyhell.item.custom.BlasphemousTwinDaggerItem;
import net.agusdropout.bloodyhell.item.custom.base.IComboWeapon;
import net.agusdropout.bloodyhell.networking.ModMessages;
import net.agusdropout.bloodyhell.networking.packet.CrimsonVeilDataSyncS2CPacket;
import net.agusdropout.bloodyhell.networking.packet.SyncRemoveBloodFirePacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.EntityLeaveLevelEvent;
import net.minecraftforge.event.entity.SpawnPlacementRegisterEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.living.MobEffectEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

import static net.agusdropout.bloodyhell.BloodyHell.MODID;
import static net.agusdropout.bloodyhell.entity.ModEntityTypes.*;

public class ModEvents {

    @Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class ModEventBusEvents {

        @SubscribeEvent
        public static void registerSpawnPlacements(SpawnPlacementRegisterEvent event) {

        }

        @SubscribeEvent
        public static void EntityAttributeEvent(EntityAttributeCreationEvent event) {
            // Existing Mobs
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
            event.put(CINDER_ACOLYTE.get(), CinderAcolyteEntity.setAttributes());
            event.put(ModEntityTypes.RITEKEEPER.get(), RitekeeperEntity.createAttributes().build());
            event.put(ModEntityTypes.BLASPHEMOUS_TWIN_DAGGERS_CLONE.get(), BlasphemousTwinDaggersCloneEntity.createAttributes().build());
            event.put(ModEntityTypes.FAILED_REMNANT.get(), FailedRemnantEntity.setAttributes().build());
        }

        public static AttributeSupplier.Builder createGenericAttributes() {
            return Monster.createMonsterAttributes()
                    .add(Attributes.MAX_HEALTH, 10.0D)
                    .add(Attributes.MOVEMENT_SPEED, 0.25D)
                    .add(Attributes.ATTACK_DAMAGE, 1.0D);
        }

        @SubscribeEvent
        public static void commonSetup(FMLCommonSetupEvent event) {
            event.enqueueWork(() -> {
                // Common setup work
            });
        }
    }

    @Mod.EventBusSubscriber(modid = MODID)
    public static class ForgeEvents {
        @SubscribeEvent
        public static void onAttachCapabilitiesPlayer(AttachCapabilitiesEvent<Entity> event) {
            if (event.getObject() instanceof Player) {
                if (!event.getObject().getCapability(PlayerCrimsonveilProvider.PLAYER_CRIMSONVEIL).isPresent()) {
                    event.addCapability(new ResourceLocation(MODID, "properties"), new PlayerCrimsonveilProvider());
                }
            }
        }



        @SubscribeEvent
        public static void onLivingDeath(LivingDeathEvent event) {
            BloodHarvestHandler.onLivingDeath(event);
        }

        @SubscribeEvent
        public static void onLivingHurt(LivingHurtEvent event) {
            if (event.getSource().getEntity() instanceof Player player) {
                ItemStack mainHandStack = player.getMainHandItem();
                if (mainHandStack.getItem() instanceof IComboWeapon comboWeapon) {
                    float bonusDamage = comboWeapon.getComboDamageBonus(mainHandStack);
                    if (bonusDamage > 0) {
                        event.setAmount(event.getAmount() + bonusDamage);
                    }
                }
            }
        }

        @SubscribeEvent
        public static void onPlayerAttack(AttackEntityEvent event) {
            Player player = event.getEntity();
            ItemStack stack = player.getMainHandItem();

            if (stack.getItem() instanceof IComboWeapon comboWeapon) {
                if (comboWeapon.shouldCancelStandardAttack()) {
                    if (player.getCooldowns().isOnCooldown(stack.getItem())) {
                        event.setCanceled(true);
                    }
                }
            }
        }

        @SubscribeEvent
        public static void onPlayerCloned(PlayerEvent.Clone event) {
            if (event.isWasDeath()) {
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
            if (!event.getLevel().isClientSide()) {
                if (event.getEntity() instanceof ServerPlayer player) {
                    player.getCapability(PlayerCrimsonveilProvider.PLAYER_CRIMSONVEIL).ifPresent(crimsonVeil -> {
                        ModMessages.sendToPlayer(new CrimsonVeilDataSyncS2CPacket(crimsonVeil.getCrimsonVeil()), player);
                    });
                }
            }
        }

        @SubscribeEvent
        public static void onPlayerAbandonWorld(EntityLeaveLevelEvent event) {
            if (!event.getLevel().isClientSide()) {
                if (event.getEntity() instanceof ServerPlayer player) {
                    // Cleanup if needed
                }
            }
        }

        @SubscribeEvent
        public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
            if (event.side == LogicalSide.SERVER) {
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
            if (event.getEffect() == ModEffects.BLOOD_FIRE_EFFECT.get()) {
                LivingEntity entity = event.getEntity();
                if (!entity.level().isClientSide) {
                    ModMessages.sendToPlayersTrackingEntity(new SyncRemoveBloodFirePacket(entity.getId()), entity);
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