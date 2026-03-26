package net.agusdropout.bloodyhell.event;

import net.agusdropout.bloodyhell.capability.crimsonveilPower.PlayerCrimsonVeil;
import net.agusdropout.bloodyhell.capability.crimsonveilPower.PlayerCrimsonveilProvider;
import net.agusdropout.bloodyhell.capability.insight.PlayerInsight;
import net.agusdropout.bloodyhell.effect.ModEffects;
import net.agusdropout.bloodyhell.entity.ModEntityTypes;
import net.agusdropout.bloodyhell.entity.custom.*;
import net.agusdropout.bloodyhell.entity.minions.custom.BurdenOfTheUnknownEntity;
import net.agusdropout.bloodyhell.entity.minions.custom.FailedSonOfTheUnknown;
import net.agusdropout.bloodyhell.entity.minions.custom.WeepingOcularEntity;
import net.agusdropout.bloodyhell.event.handlers.BloodHarvestHandler;
import net.agusdropout.bloodyhell.event.handlers.PlayerCapabilityHandler;
import net.agusdropout.bloodyhell.item.ModItems;
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
            // --- BOSSES & ELITES ---
            event.put(UNKNOWN_LANTERN.get(), UnknownLanternEntity.createAttributes().build());
            event.put(SELIORA.get(), SelioraEntity.createAttributes().build());
            event.put(BLASPHEMOUS_ARM_ENTITY.get(), BlasphemousArmEntity.createAttributes().build());
            event.put(RITEKEEPER.get(), RitekeeperEntity.createAttributes().build());
            event.put(BLASPHEMOUS_MALFORMATION.get(), BlasphemousMalformationEntity.createAttributes().build());

            // --- HOSTILE MONSTERS ---
            event.put(BLOODTHIRSTYBEAST.get(), BloodThirstyBeastEntity.createAttributes().build());
            event.put(OMEN_GAZER_ENTITY.get(), OmenGazerEntity.createAttributes().build());
            event.put(VEINRAVER_ENTITY.get(), VeinraverEntity.createAttributes().build());
            event.put(GRAVE_WALKER_ENTITY.get(), GraveWalkerEntity.createAttributes().build());
            event.put(HORNED_WORM.get(), HornedWormEntity.createAttributes().build());
            event.put(VEIL_STALKER.get(), VeilStalkerEntity.createAttributes().build());
            event.put(ONI.get(), OniEntity.createAttributes().build());
            event.put(VESPER.get(), VesperEntity.createAttributes().build());
            event.put(CINDER_ACOLYTE.get(), CinderAcolyteEntity.createAttributes().build());
            event.put(FAILED_REMNANT.get(), FailedRemnantEntity.createAttributes().build());

            // --- PASSIVE & NEUTRAL CREATURES ---
            event.put(BLOOD_SEEKER.get(), BloodSeekerEntity.createAttributes().build());
            event.put(BLOODY_SOUL_ENTITY.get(), BloodySoulEntity.createAttributes().build());
            event.put(CORRUPTED_BLOODY_SOUL_ENTITY.get(), CorruptedBloodySoulEntity.createAttributes().build());
            event.put(OFFSPRING_OF_THE_UNKNOWN.get(), OffspringOfTheUnknownEntity.createAttributes().build());
            event.put(CRIMSON_RAVEN.get(), CrimsonRavenEntity.createAttributes().build());
            event.put(EYESHELL_SNAIL.get(), EyeshellSnailEntity.createAttributes().build());
            event.put(SCARLETSPECKLED_FISH.get(), ScarletSpeckledFishEntity.createAttributes().build());
            event.put(BLOODPIG.get(), BloodPigEntity.createAttributes().build());

            // --- MINIONS ---
            event.put(FAILED_SON_OF_THE_UNKNOWN.get(), FailedSonOfTheUnknown.createAttributes().build());
            event.put(WEEPING_OCULAR.get(), WeepingOcularEntity.createAttributes().build());
            event.put(BURDEN_OF_THE_UNKNOWN.get(), BurdenOfTheUnknownEntity.createAttributes().build());
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
            PlayerCapabilityHandler.handleAttachCapabilities(event);
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
            PlayerCapabilityHandler.handlePlayerCloned(event);
        }

        @SubscribeEvent
        public static void onPlayerJoinWorld(EntityJoinLevelEvent event) {
            PlayerCapabilityHandler.handlePlayerJoinLevel(event);
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
            event.register(PlayerInsight.class);
        }
    }
}