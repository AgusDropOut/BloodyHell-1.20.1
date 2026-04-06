package net.agusdropout.bloodyhell.item.custom;

import net.agusdropout.bloodyhell.entity.ModEntityTypes;
import net.agusdropout.bloodyhell.entity.effects.BlackHoleEntity;
import net.agusdropout.bloodyhell.entity.effects.FrenziedFireEntity;
import net.agusdropout.bloodyhell.networking.ModMessages;
import net.agusdropout.bloodyhell.networking.packet.S2CPainThronePacket;
import net.agusdropout.bloodyhell.particle.ModParticles;
import net.agusdropout.bloodyhell.particle.ParticleOptions.*;
import net.agusdropout.bloodyhell.util.bones.BoneManipulation;
import net.agusdropout.bloodyhell.util.capability.InsightHelper;
import net.agusdropout.bloodyhell.util.visuals.ParticleHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

import java.util.List;

public class EightBallItem extends Item {
    public EightBallItem(Properties properties) {
        super(properties);
    }

    private static final Vector3f COLOR_CORE = new Vector3f(1.0f, 0.6f, 0.0f);
    private static final Vector3f COLOR_FADE = new Vector3f(0.5f, 0.0f, 0.0f);

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {


        //triggerSummoningRitual(level, player.getX(), player.getY(), player.getZ(), 1.5f, 3.0f, 120, COLOR_CORE);

        if (!level.isClientSide && player instanceof ServerPlayer serverPlayer) {
           InsightHelper.addInsight( serverPlayer,10);

            //UnknownLanternEntity lantern = new UnknownLanternEntity(ModEntityTypes.UNKNOWN_LANTERN.get(), level);
            //lantern.setTargetPlayer(player.getUUID());
            //lantern.setPos(player.getX(), player.getY(), player.getZ());
            //level.addFreshEntity(lantern);


      //    BastionOfTheUnknownEntity son = new BastionOfTheUnknownEntity(ModEntityTypes.BASTION_OF_THE_UNKNOWN.get(), level);
      //  son.setOwnerUUID(player.getUUID());
      //  son.setPos(player.getX(), player.getY(), player.getZ());
      //  level.addFreshEntity(son);

      //    BurdenOfTheUnknownEntity son2 = new BurdenOfTheUnknownEntity(ModEntityTypes.BURDEN_OF_THE_UNKNOWN.get(), level);
      //     son2.setOwnerUUID(player.getUUID());
      //     son2.setPos(player.getX(), player.getY(), player.getZ());
      //    level.addFreshEntity(son2);
////
         //  WeepingOcularEntity eye = new WeepingOcularEntity(ModEntityTypes.WEEPING_OCULAR.get(), level);
         //    eye.setOwnerUUID(player.getUUID());
         //    eye.setPos(player.getX(), player.getY(), player.getZ());
         //  level.addFreshEntity(eye);
//
            Vec3 impactPos = player.getOnPos().getCenter().add(0,1,0);

            /* Instantiates and configures the hazard entity */
            FrenziedFireEntity fireHazard = new FrenziedFireEntity(ModEntityTypes.FRENZIED_FIRE.get(), level);
            fireHazard.setPos(impactPos.x(), impactPos.y(), impactPos.z());

            /* Adds the hazard to the world */
            level.addFreshEntity(fireHazard);


     //int quantity = 5;
//
      //  for (int i = 0; i < quantity; i++) {
     //     RhnullImpalerEntity impaler = new RhnullImpalerEntity(level,player, i, quantity);
     //     level.addFreshEntity(impaler);
     // }
//
//
        //  RhnullHeavySwordEntity sword = new RhnullHeavySwordEntity(level, player, 10);
//
        // // // Offset the spawn position higher up if you want it to "fall" more dramatically
       //   Vec3 spawnPos = player.position().add(0, 3, 0).add(player.getLookAngle().scale(5));
       //   sword.setPos(spawnPos.x, spawnPos.y, spawnPos.z);
//
        // //// // Add the entity to the world
       //  level.addFreshEntity(sword);

        // RhnullPainThroneEntity throne = new RhnullPainThroneEntity(ModEntityTypes.RHNULL_PAIN_THRONE.get(),level, player, player.getX(), player.getY(), player.getZ(),null);
        // level.addFreshEntity(throne);

            //RhnullOrbEmitter orb = new RhnullOrbEmitter(ModEntityTypes.RHNULL_ORB_EMITTER_ENTITY.get(), level, player, player.getX(), player.getY() + 1.5, player.getZ(), List.of());
            //level.addFreshEntity(orb);
        } else {

            //Vec3 look = player.getLookAngle();
            //Vec3 pos = player.getEyePosition().add(look.scale(2.0D));//
            //level.addParticle(
            //        new FrenziedFlameParticleOptions(1.0F, 0.7F, 0.0F, 500),
            //        pos.x, pos.y, pos.z,
            //        0.0D, 0.0D, 0.0D
            //);

           // for (int i = 0; i < 8; i++) {
           //     double offsetX = (level.random.nextDouble() - 0.5D) * 1.2D;
           //     double offsetZ = (level.random.nextDouble() - 0.5D) * 1.2D;
//
           //     level.addParticle(ModParticles.LINEAR_FRENZIED_FLAME.get(),
           //             player.getX() + offsetX,
           //             player.getY() + 0.5D,
           //             player.getZ() + offsetZ,
           //             0.0D,
           //             0.08D + (level.random.nextDouble() * 0.05D),
           //             0.0D);
           // }

           // executeFrenzyBurst(level, player.position().add(0, 1.0D, 0));

           // executeOrbitalBurst(level, player.position().add(0, 1.0D, 0));
            //executeFrenziedExplosion(level, player.position().add(0, 1.0D, 0), 4.5f);
        }
//

        return InteractionResultHolder.success(player.getItemInHand(hand));
    }

    @Override
    public boolean hurtEnemy(ItemStack itemStack, LivingEntity target, LivingEntity owner) {


        startThroneEffect(target);
        if (!owner.level().isClientSide) {

            double xSource = target.getX()+ (0.5 - owner.getRandom().nextDouble());
            double ySource = target.getY() - 2.0;
            double zSource = target.getZ()+ (0.5 - owner.getRandom().nextDouble());
            ParticleHelper.spawn(owner.level(),
                    new TetherParticleOptions(target.getUUID(), 0.6f, 0.0f, 0.0f, 1.0f, 40),
                    xSource, ySource, zSource,
                    0,
                    0, 0
            );
        }

        return true;

    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> components, TooltipFlag flag) {
        if (Screen.hasShiftDown()) {
            components.add(Component.literal("Right Click: Summon Rhnull Impalers").withStyle(ChatFormatting.RED));
            components.add(Component.literal("Left Click (Empty Hand): Fire Spear").withStyle(ChatFormatting.GOLD));
        } else {
            components.add(Component.literal("Hold [SHIFT] for spell info").withStyle(ChatFormatting.DARK_RED));
        }
        super.appendHoverText(stack, level, components, flag);
    }

    public void startThroneEffect(LivingEntity victim) {
        if (!victim.level().isClientSide) {
            ModMessages.sendToPlayersTrackingEntity(new S2CPainThronePacket(victim.getUUID(), 1000, BoneManipulation.JITTER), victim);
        }
    }

    public static void triggerSummoningRitual(Level level, double x, double y, double z, float radius, float height, int duration, Vector3f color) {
        if (!level.isClientSide) {
            BlackHoleEntity blackHole = new BlackHoleEntity(ModEntityTypes.BLACK_HOLE.get(), level);
            blackHole.setPos(x, y, z);
            blackHole.setRadius(radius);
            blackHole.setMaxAge(duration);

            int r = (int)(color.x() * 255.0F);
            int g = (int)(color.y() * 255.0F);
            int b = (int)(color.z() * 255.0F);
            int intColor = (r << 16) | (g << 8) | b;
            blackHole.setColor(intColor);

            level.addFreshEntity(blackHole);
        } else {
            level.addParticle(new MagicalRingParticleOptions(color, radius, height), x, y, z, duration, 0.0D, 0.0D);
        }
    }

    public void executeFrenzyBurst(Level level, Vec3 sourcePos) {
        if (level.isClientSide) {
            int beamCount = 6;
            for (int i = 0; i < beamCount; i++) {
                /* Circular distribution offset */
                double angle = (i / (double) beamCount) * Math.PI * 2.0D;
                double offsetX = Math.cos(angle) * 0.5D;
                double offsetZ = Math.sin(angle) * 0.5D;

                /* Outward and upward velocity */
                double velX = Math.cos(angle) * 0.4D;
                double velY = 0.6D + (level.random.nextDouble() * 0.4D);
                double velZ = Math.sin(angle) * 0.4D;

                level.addParticle(ModParticles.FRENZIED_TRAIL_PARTICLE.get(),
                        sourcePos.x() + offsetX,
                        sourcePos.y() + 1.0D,
                        sourcePos.z() + offsetZ,
                        velX, velY, velZ
                );
            }
        }
    }

    public static void executeFrenziedExplosion(Level level, Vec3 targetPos, float size) {
        if (level.isClientSide) {
            FrenziedExplosionParticleOptions options = new FrenziedExplosionParticleOptions(size);

            level.addParticle(options,
                    targetPos.x(), targetPos.y(), targetPos.z(),
                    0.0D, 0.0D, 0.0D);
        }
    }

    public void executeOrbitalBurst(Level level, Vec3 sourcePos) {
        if (level.isClientSide) {
            level.addParticle(ModParticles.ORBITAL_FRENZIED_PARTICLE.get(),
                    sourcePos.x(),
                    sourcePos.y() + 1.0D,
                    sourcePos.z(),
                    0.0D, 0.4D, 0.0D
            );
        }
    }

}