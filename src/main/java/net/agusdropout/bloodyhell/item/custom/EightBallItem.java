package net.agusdropout.bloodyhell.item.custom;

import net.agusdropout.bloodyhell.entity.ModEntityTypes;
import net.agusdropout.bloodyhell.entity.custom.UnknownLanternEntity;
import net.agusdropout.bloodyhell.entity.effects.BlackHoleEntity;
import net.agusdropout.bloodyhell.entity.minions.custom.BastionOfTheUnknownEntity;
import net.agusdropout.bloodyhell.entity.minions.custom.BurdenOfTheUnknownEntity;
import net.agusdropout.bloodyhell.entity.minions.custom.FailedSonOfTheUnknown;
import net.agusdropout.bloodyhell.entity.minions.custom.WeepingOcularEntity;
import net.agusdropout.bloodyhell.entity.projectile.spell.RhnullHeavySwordEntity;
import net.agusdropout.bloodyhell.entity.projectile.spell.RhnullImpalerEntity;
import net.agusdropout.bloodyhell.entity.projectile.spell.RhnullOrbEmitter;
import net.agusdropout.bloodyhell.entity.projectile.spell.RhnullPainThroneEntity;
import net.agusdropout.bloodyhell.networking.ModMessages;
import net.agusdropout.bloodyhell.networking.packet.S2CPainThronePacket;
import net.agusdropout.bloodyhell.particle.ModParticles;
import net.agusdropout.bloodyhell.particle.ParticleOptions.*;
import net.agusdropout.bloodyhell.util.bones.BoneManipulation;
import net.agusdropout.bloodyhell.util.capability.InsightHelper;
import net.agusdropout.bloodyhell.util.visuals.ParticleHelper;
import net.agusdropout.bloodyhell.util.visuals.SpellPalette;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
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


            BastionOfTheUnknownEntity son = new BastionOfTheUnknownEntity(ModEntityTypes.BASTION_OF_THE_UNKNOWN.get(), level);
          son.setOwnerUUID(player.getUUID());
          son.setPos(player.getX(), player.getY(), player.getZ());
          level.addFreshEntity(son);
//
         //  WeepingOcularEntity eye = new WeepingOcularEntity(ModEntityTypes.WEEPING_OCULAR.get(), level);
         //    eye.setOwnerUUID(player.getUUID());
         //    eye.setPos(player.getX(), player.getY(), player.getZ());
         //  level.addFreshEntity(eye);
//



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

            // Visual Feedback (Client Side)
         //   Vector3f color = new Vector3f(1f, 0.9f, 0.0f);
         //   float radius = 1.5f;
         //   float height = 3.0f;
         //   double maxLifeTime = 120.0;
      // org.joml.Vector3f color = new org.joml.Vector3f(0.8f, 0.1f, 0.1f);
      // NoiseSphereParticleOptions options =
      //          new NoiseSphereParticleOptions(color, 1.5f, 60);
//
      //  level.addParticle(options,
      //          player.getX() + 0.5, player.getY() + 1.5, player.getZ() + 0.5, 0.0, 0.0, 0.0)  ;
//
////
      //   //   level.addParticle(new MagicalRingParticleOptions(color, radius, height),
      //   //           player.getX(), player.getY(), player.getZ(),
      //   //           maxLifeTime, 0.0D, 0.0D);
      //     // System.out.println("Spawned Ethereal Swirl Particle at " + player.getX() + ", " + player.getY() + ", " + player.getZ());
      //      //ParticleHelper.spawn(level, new EtherealSwirlOptions(SpellPalette.RHNULL.getColor(1), 300, 1.0f), player.position().add(0,1.5,0), 0, 0, 0);
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

}