package net.agusdropout.bloodyhell.item.custom;

import net.agusdropout.bloodyhell.entity.ModEntityTypes;
import net.agusdropout.bloodyhell.entity.projectile.spell.RhnullHeavySwordEntity;
import net.agusdropout.bloodyhell.entity.projectile.spell.RhnullImpalerEntity;
import net.agusdropout.bloodyhell.entity.projectile.spell.RhnullOrbEmitter;
import net.agusdropout.bloodyhell.entity.projectile.spell.RhnullPainThroneEntity;
import net.agusdropout.bloodyhell.networking.ModMessages;
import net.agusdropout.bloodyhell.networking.packet.S2CPainThronePacket;
import net.agusdropout.bloodyhell.particle.ModParticles;
import net.agusdropout.bloodyhell.particle.ParticleOptions.HollowRectangleOptions;
import net.agusdropout.bloodyhell.particle.ParticleOptions.TetherParticleOptions;
import net.agusdropout.bloodyhell.util.bones.BoneManipulation;
import net.agusdropout.bloodyhell.util.visuals.ParticleHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
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
        if (!level.isClientSide) {

          int quantity = 5;
//
           for (int i = 0; i < quantity; i++) {
               RhnullImpalerEntity impaler = new RhnullImpalerEntity(level,player, i, quantity);
               level.addFreshEntity(impaler);
           }
//
//
           RhnullHeavySwordEntity sword = new RhnullHeavySwordEntity(level, player, 10);
//
          // // Offset the spawn position higher up if you want it to "fall" more dramatically
            Vec3 spawnPos = player.position().add(0, 3, 0).add(player.getLookAngle().scale(5));
            sword.setPos(spawnPos.x, spawnPos.y, spawnPos.z);
//
          //// // Add the entity to the world
           level.addFreshEntity(sword);

            RhnullPainThroneEntity throne = new RhnullPainThroneEntity(ModEntityTypes.RHNULL_PAIN_THRONE.get(),level, player, player.getX(), player.getY(), player.getZ(),null);
            level.addFreshEntity(throne);

            //RhnullOrbEmitter orb = new RhnullOrbEmitter(ModEntityTypes.RHNULL_ORB_EMITTER_ENTITY.get(), level, player, player.getX(), player.getY() + 1.5, player.getZ(), List.of());
            //level.addFreshEntity(orb);
        }//

        // Visual Feedback (Client Side)
        if (level.isClientSide) {
            ParticleHelper.spawnRing(level, ModParticles.BLOOD_PARTICLES.get(), player.position().add(0, 1, 0), 1.5, 20, 0.05);
        }

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

}