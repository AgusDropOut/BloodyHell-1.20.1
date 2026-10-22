package net.agusdropout.bloodyhell.entity.ai.goals;

import net.agusdropout.bloodyhell.entity.ModEntityTypes;
import net.agusdropout.bloodyhell.entity.custom.OmenGazerEntity;
import net.agusdropout.bloodyhell.entity.custom.VeinraverEntity;
import net.agusdropout.bloodyhell.entity.effects.EntityFallingBlock;
import net.agusdropout.bloodyhell.sound.ModSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.List;
import java.util.Random;

public class VeinRaverSlamAttackGoal extends Goal {

    private final VeinraverEntity veinraverEntity;

    private int goalTime;
    private BlockPos impactPos;

    public VeinRaverSlamAttackGoal(VeinraverEntity veinraverEntity) {
        this.veinraverEntity = veinraverEntity;
    }


    @Override
    public void start() {
        veinraverEntity.setSlamAttack(true);
        veinraverEntity.getNavigation().stop();
        this.goalTime = 60;
        if(veinraverEntity.getTarget() != null) {
            this.impactPos = veinraverEntity.getTarget().blockPosition().below();
        }
    }

    @Override
    public boolean canUse() {
        LivingEntity target = this.veinraverEntity.getTarget();
        if (target != null) {
            double distance = this.veinraverEntity.distanceTo(target);
            return distance <= 4.0 && !veinraverEntity.isThrowing() && !veinraverEntity.isSlashAttack() && !veinraverEntity.isSlamAttack() && veinraverEntity.getSlamAttackCooldown() == 0;
        }
        return false;
    }

    @Override
    public boolean canContinueToUse() {
        return goalTime > 0
                && veinraverEntity.getTarget() != null;
    }

    @Override
    public void stop() {
        super.stop();
        this.veinraverEntity.setSlamAttack(false);
        this.veinraverEntity.setSlamAttackCooldown(120);
    }

    public boolean requiresUpdateEveryTick() {
        return true;
    }
    @Override
    public void tick() {
        goalTime--;
        if (goalTime > 0) {
            if (veinraverEntity.getTarget() != null) {
                veinraverEntity.getNavigation().stop();
            }
            if(goalTime == 15){
                performSlamAttack();
            }

        } else {
            stop();
        }
    }
    public void performSlamAttack() {
        this.veinraverEntity.level().playSound(null, this.veinraverEntity.blockPosition(),
                ModSounds.VEINRAVER_SLAM.get(), SoundSource.HOSTILE, 1.0F, 1.0F);

        if (impactPos == null) return;

        BlockPos[] affectedPositions = new BlockPos[]{
                impactPos.north(), impactPos.south(), impactPos.east(), impactPos.west(),
                impactPos.north().east(), impactPos.north().west(), impactPos.south().east(), impactPos.south().west(),
                impactPos.north(2), impactPos.south(2), impactPos.east(2), impactPos.west(2),
                impactPos.north(2).east(), impactPos.north(2).west(), impactPos.south(2).east(), impactPos.south(2).west(),
                impactPos.east(2).north(1), impactPos.east(2).south(1), impactPos.west(2).north(1), impactPos.west(2).south(1),
                impactPos.north(2).east(2), impactPos.north(2).west(2), impactPos.south(2).east(2), impactPos.south(2).west(2)
        };

        int blockNumber = 0;
        for (BlockPos pos : affectedPositions) {

            BlockState blockState = this.veinraverEntity.level().getBlockState(pos);


            if (blockState.isAir()) {
                blockState = Blocks.STONE.defaultBlockState();
            }
            Random random = new Random();

            if (blockNumber < 8) {
                float velocity = 0.15f + (random.nextFloat() * 0.1f - 0.05f);
                EntityFallingBlock fallingBlock = new EntityFallingBlock(ModEntityTypes.ENTITY_FALLING_BLOCK.get(),
                        this.veinraverEntity.level(), blockState, velocity);
                fallingBlock.setPos(pos.getX() + 0.5, pos.getY() + 1, pos.getZ() + 0.5);
                this.veinraverEntity.level().addFreshEntity(fallingBlock);
            } else {
                float velocity = 0.30f + (random.nextFloat() * 0.1f - 0.05f);
                EntityFallingBlock fallingBlock = new EntityFallingBlock(ModEntityTypes.ENTITY_FALLING_BLOCK.get(),
                        this.veinraverEntity.level(), blockState, velocity);
                fallingBlock.setPos(pos.getX() + 0.5, pos.getY() + 1, pos.getZ() + 0.5);
                this.veinraverEntity.level().addFreshEntity(fallingBlock);
            }



            blockNumber++;
        }


        AABB hitbox = new AABB(impactPos).inflate(2);
        List<Entity> entitiesHit = this.veinraverEntity.level().getEntities(
                this.veinraverEntity, hitbox, entity -> entity instanceof LivingEntity && entity != veinraverEntity
        );
        for (Entity entity : entitiesHit) {
            entity.hurt(this.veinraverEntity.damageSources().mobAttack(veinraverEntity), 5.0f);
        }
    }
}
