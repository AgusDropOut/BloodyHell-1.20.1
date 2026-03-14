package net.agusdropout.bloodyhell.entity.ai.goals;

import net.agusdropout.bloodyhell.entity.custom.UnknownLanternEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;
import java.util.UUID;

public class LanternPersecutionGoal extends Goal {

    private final UnknownLanternEntity lantern;
    private Player targetPlayer;
    private int gazeTicks = 0;
    private Vec3 lastPosition = Vec3.ZERO;
    private int checkTimer = 0;
    private int stuckTicks = 0;

    private static final double MAX_VISIBLE_DISTANCE = 15.0D;

    // --- Speed Bonus Constants ---
    private static final double BASE_SPEED = 1.07D;
    private static final double MAX_SPEED_BONUS = 0.10D; // 10% faster
    private static final double MIN_BONUS_DISTANCE = 7.0D; // Starts increasing bonus past this distance
    private static final double MAX_BONUS_DISTANCE = 15.0D; // Reaches max 10% bonus at this distance

    public LanternPersecutionGoal(UnknownLanternEntity lantern) {
        this.lantern = lantern;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        if (this.lantern.isSummoning()) return false;

        UUID targetId = this.lantern.getTargetPlayer();
        if (targetId == null) return false;

        Player player = this.lantern.level().getPlayerByUUID(targetId);

        if (player == null || !player.isAlive() || player.isSpectator()) {
            if (!this.lantern.level().isClientSide()) {
                this.lantern.fail();
            }
            return false;
        }

        this.targetPlayer = player;
        return true;
    }

    @Override
    public boolean canContinueToUse() {
        return this.canUse();
    }

    @Override
    public void start() {
        this.gazeTicks = 0;
        this.lantern.setTarget(this.targetPlayer);
        this.lastPosition = this.lantern.position();
        this.checkTimer = 0;
        this.stuckTicks = 0;
    }

    @Override
    public void tick() {
        if (this.targetPlayer == null || !this.targetPlayer.isAlive()) return;

        this.lantern.getLookControl().setLookAt(this.targetPlayer, 30.0F, 30.0F);

        double distanceToPlayerSqr = this.lantern.distanceToSqr(this.targetPlayer);

        // Fail condition if it catches the player
        if (distanceToPlayerSqr <= 3.0D) {
            if (!this.lantern.level().isClientSide()) {
                this.lantern.fail();
            }
            return;
        }

        // Calculate dynamic speed based on distance
        double distance = Math.sqrt(distanceToPlayerSqr);
        double currentSpeed = BASE_SPEED;

        if (distance > MIN_BONUS_DISTANCE) {
            if (distance >= MAX_BONUS_DISTANCE) {
                currentSpeed = BASE_SPEED * (1.0D + MAX_SPEED_BONUS);
            } else {
                // Smoothly interpolate between 0% and 10% bonus
                double bonusFraction = (distance - MIN_BONUS_DISTANCE) / (MAX_BONUS_DISTANCE - MIN_BONUS_DISTANCE);
                currentSpeed = BASE_SPEED * (1.0D + (MAX_SPEED_BONUS * bonusFraction));
            }
        }

        // Massive speed multiplier if the entity is stuck in water
        if (this.lantern.isInWater()) {
            currentSpeed *= 2.5D;
        }

        this.lantern.getNavigation().moveTo(this.targetPlayer, currentSpeed);

        // Anti-stuck and block breaking logic
        this.checkTimer++;
        if (this.checkTimer >= 10) {
            double distanceMovedSqr = this.lantern.position().distanceToSqr(this.lastPosition);

            if (distanceMovedSqr < 0.05D) {
                this.stuckTicks++;
            } else {
                this.stuckTicks = 0;
            }

            this.lastPosition = this.lantern.position();
            this.checkTimer = 0;

            if (this.stuckTicks >= 3) {
                this.breakBlocksInPath();
                this.stuckTicks = 0;
            }
        }

        // Gaze damage logic
        boolean isPlayerLooking = this.isPlayerLookingAtMe(this.targetPlayer, this.lantern);

        if (isPlayerLooking) {
            this.gazeTicks++;
            this.lantern.setGazeIntensity(Math.min(1.0f, (float)this.gazeTicks / 30.0f));

            if (this.gazeTicks >= 30) {
                this.targetPlayer.hurt(this.lantern.damageSources().magic(), 4.0F);
                this.lantern.playSound(SoundEvents.ILLUSIONER_CAST_SPELL, 1.0F, 0.5F);
                this.gazeTicks = 0;
            }
        } else {
            this.gazeTicks = Math.max(0, this.gazeTicks - 1);
            this.lantern.setGazeIntensity((float)this.gazeTicks / 30.0f);
        }
    }

    @Override
    public void stop() {
        this.lantern.setGazing(false);
        this.targetPlayer = null;
        this.gazeTicks = 0;
        this.lantern.getNavigation().stop();
    }

    private void breakBlocksInPath() {
        if (!net.minecraftforge.event.ForgeEventFactory.getMobGriefingEvent(this.lantern.level(), this.lantern)) return;

        boolean brokeBlocks = false;
        Vec3 directionToPlayer = this.targetPlayer.position().subtract(this.lantern.position()).normalize();
        var destructionBox = this.lantern.getBoundingBox().move(directionToPlayer.scale(1.2D)).inflate(0.5D);

        int minX = Mth.floor(destructionBox.minX);
        int minY = Mth.floor(destructionBox.minY);
        int minZ = Mth.floor(destructionBox.minZ);
        int maxX = Mth.ceil(destructionBox.maxX);
        int maxY = Mth.ceil(destructionBox.maxY);
        int maxZ = Mth.ceil(destructionBox.maxZ);

        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                for (int z = minZ; z <= maxZ; z++) {
                    BlockPos pos = new BlockPos(x, y, z);
                    var state = this.lantern.level().getBlockState(pos);

                    if (!state.isAir() && state.getDestroySpeed(this.lantern.level(), pos) >= 0.0F && !state.hasBlockEntity()) {
                        this.lantern.getBlockMemoryManager().rememberBlock(pos, state);
                        this.lantern.level().destroyBlock(pos, false, this.lantern);
                        brokeBlocks = true;
                    }
                }
            }
        }

        if (brokeBlocks) {
            this.lantern.playSound(SoundEvents.ZOMBIE_BREAK_WOODEN_DOOR, 1.0F, 0.5F);
        }
    }

    private boolean isPlayerLookingAtMe(Player player, UnknownLanternEntity lantern) {
        Vec3 playerView = player.getViewVector(1.0F).normalize();

        Vec3 toLantern = new Vec3(
                lantern.getX() - player.getX(),
                lantern.getEyeY() + 1 - player.getEyeY(),
                lantern.getZ() - player.getZ()
        );

        double distance = toLantern.length();

        if (distance > MAX_VISIBLE_DISTANCE) {
            return false;
        }

        toLantern = toLantern.normalize();
        double dotProduct = playerView.dot(toLantern);

        if (dotProduct > 1.0D - 0.025D) {
            return player.hasLineOfSight(lantern);
        }

        return false;
    }
}