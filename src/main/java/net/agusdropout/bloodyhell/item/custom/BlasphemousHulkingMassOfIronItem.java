package net.agusdropout.bloodyhell.item.custom;

import net.agusdropout.bloodyhell.entity.ModEntityTypes;
import net.agusdropout.bloodyhell.entity.effects.EntityCameraShake;
import net.agusdropout.bloodyhell.entity.projectile.BlasphemousSpearEntity;
import net.agusdropout.bloodyhell.item.client.BlasphemousHulkingMassOfIronRenderer;
import net.agusdropout.bloodyhell.item.custom.base.BaseMeleeComboWeapon;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import net.minecraft.world.item.Tier;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import software.bernie.geckolib.animatable.SingletonGeoAnimatable;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;

import java.util.List;
import java.util.function.Consumer;

public class BlasphemousHulkingMassOfIronItem extends BaseMeleeComboWeapon {

    private static final RawAnimation SLAM_1 = RawAnimation.begin().thenPlay("slam_1");
    private static final RawAnimation SLAM_2 = RawAnimation.begin().thenPlay("slam_2");
    private static final RawAnimation SLAM_3 = RawAnimation.begin().thenPlay("slam_3");
    private static final RawAnimation SPECIAL_ATK = RawAnimation.begin().thenPlay("special_attack");

    public BlasphemousHulkingMassOfIronItem(Tier tier, int damage, float speed, Properties props) {
        super(tier, damage, speed, props);
        SingletonGeoAnimatable.registerSyncedAnimatable(this);
    }

    @Override public int getMaxCombos() { return 3; }
    @Override public int getSpecialCost() { return 10; }
    @Override public int getSpecialDuration() { return 45; }

    @Override
    public int getComboDuration(int combo) {
        return switch (combo) {
            case 2 -> 30;
            case 3 -> 35;
            default -> 20;
        };
    }

    @Override
    public int getDamageTick(int combo) {
        return switch (combo) {
            case 1 -> 12;
            case 2 -> 18;
            case 3 -> 22;
            default -> 12;
        };
    }

    @Override
    public float getComboDamageBonus(ItemStack stack) {
        int combo = stack.getOrCreateTag().getInt("CurrentCombo");
        return combo == 3 ? 10.0f : (combo == 2 ? 5.0f : 0.0f);
    }

    @Override
    public void performComboDamage(Level level, Player player, int combo, ItemStack stack) {
        double reach = (combo == 2) ? 6.0D : 4.0D;
        double width = (combo == 2) ? 1.0D : 3.5D;

        Vec3 look = player.getLookAngle();
        Vec3 lookFlat = new Vec3(look.x, 0, look.z).normalize();
        Vec3 startPos = player.position().add(0, player.getEyeHeight() * 0.5, 0);
        Vec3 endPos = startPos.add(lookFlat.scale(reach));
        AABB damageBox = new AABB(startPos, endPos).inflate(width / 2.0, 1.0, width / 2.0);

        List<LivingEntity> entities = level.getEntitiesOfClass(LivingEntity.class, damageBox);
        float baseDamage = (float) player.getAttributeValue(net.minecraft.world.entity.ai.attributes.Attributes.ATTACK_DAMAGE);
        float bonus = getComboDamageBonus(stack);
        float totalDamage = baseDamage + bonus;

        boolean hitSomething = false;

        for (LivingEntity target : entities) {
            if (target != player && !target.isAlliedTo(player)) {
                target.hurt(level.damageSources().playerAttack(player), totalDamage);
                double kbStrength = (combo == 3) ? 1.5 : 0.5;
                target.knockback(kbStrength, player.getX() - target.getX(), player.getZ() - target.getZ());
                hitSomething = true;
            }
        }

        if (hitSomething || combo == 3) {
            level.playSound(null, player.getX(), player.getY(), player.getZ(),
                    SoundEvents.ANVIL_LAND, net.minecraft.sounds.SoundSource.PLAYERS, 1.0f, 0.5f);

            if (combo == 3) {
                Vec3 shockwavePos = player.position().add(lookFlat.scale(reach));
                spawnFallingBlocks(level, shockwavePos);
                EntityCameraShake.cameraShake(level, shockwavePos, 15.0f, 0.3f, 10, 5);
            }
        }
    }

    @Override
    public void performSpecialTickLogic(Level level, Player player, ItemStack stack, int currentTick) {
        if (currentTick == 15) performSpecialAttackPhase(level, player, 1);
        if (currentTick == 30) performSpecialAttackPhase(level, player, 2);
    }

    private void performSpecialAttackPhase(Level level, Player player, int phase) {
        Vec3 playerPos = player.position();
        BlockPos centerPos = BlockPos.containing(playerPos);
        int radius = (phase == 1) ? 3 : 5;
        float density = (phase == 1) ? 0.6f : 0.4f;
        float shakeMag = (phase == 1) ? 0.2f : 0.6f;
        float shakeRad = (phase == 1) ? 15f : 25f;
        float velocityY = (phase == 1) ? 0.3f : 0.45f;

        EntityCameraShake.cameraShake(level, playerPos, shakeRad, shakeMag, 10, 5);
        spawnGroundShockwave(level, playerPos, (phase == 1) ? 200 : 400, (phase == 1) ? 1 : 2);

        for (int x = -radius; x <= radius; x++) {
            for (int z = -radius; z <= radius; z++) {
                if (Math.max(Math.abs(x), Math.abs(z)) != radius) continue;
                if (level.random.nextFloat() > density) continue;
                BlockPos candidatePos = centerPos.offset(x, 0, z);
                BlockPos groundPos = null;
                for (int yOffset = 0; yOffset >= -3; yOffset--) {
                    BlockPos checkPos = candidatePos.offset(0, yOffset, 0);
                    BlockState state = level.getBlockState(checkPos);
                    if (!state.isAir() && state.isSolidRender(level, checkPos)) {
                        groundPos = checkPos;
                        break;
                    }
                }
                if (groundPos == null) continue;

                net.agusdropout.bloodyhell.entity.effects.EntityFallingBlock fallingBlock = new net.agusdropout.bloodyhell.entity.effects.EntityFallingBlock(
                        ModEntityTypes.ENTITY_FALLING_BLOCK.get(), level, level.getBlockState(groundPos), velocityY);
                fallingBlock.setPos(groundPos.getX() + 0.5, groundPos.getY() + 1.0, groundPos.getZ() + 0.5);
                level.addFreshEntity(fallingBlock);

                float damage = (phase == 1) ? 8.0f : 12.0f;
                double dx = groundPos.getX() + 0.5 - player.getX();
                double dz = groundPos.getZ() + 0.5 - player.getZ();
                float angleDegrees = (float) (Math.atan2(dz, dx) * (180D / Math.PI));
                float finalYaw = angleDegrees - 90f;

                BlasphemousSpearEntity spear = new BlasphemousSpearEntity(level, groundPos.getX() + 0.5, groundPos.getY() + 1.0, groundPos.getZ() + 0.5, damage, player.getUUID(), finalYaw);
                level.addFreshEntity(spear);

                if (level instanceof net.minecraft.server.level.ServerLevel serverLevel) {
                    serverLevel.sendParticles(net.agusdropout.bloodyhell.particle.ModParticles.MAGIC_LINE_PARTICLE.get(), groundPos.getX() + 0.5, groundPos.getY() + 1.2, groundPos.getZ() + 0.5, 3, 0.2, 0.5, 0.2, 0.05);
                }
            }
        }
        if (phase == 1) level.playSound(null, playerPos.x, playerPos.y, playerPos.z, SoundEvents.GENERIC_EXPLODE, net.minecraft.sounds.SoundSource.PLAYERS, 0.5f, 1.2f);
        else {
            level.playSound(null, playerPos.x, playerPos.y, playerPos.z, SoundEvents.GENERIC_EXPLODE, net.minecraft.sounds.SoundSource.PLAYERS, 1.0f, 0.8f);
            level.playSound(null, playerPos.x, playerPos.y, playerPos.z, SoundEvents.ZOMBIE_BREAK_WOODEN_DOOR, net.minecraft.sounds.SoundSource.PLAYERS, 1.0f, 0.5f);
        }
    }

    private void spawnGroundShockwave(Level level, Vec3 center, int count, double speed) {
        if (level instanceof net.minecraft.server.level.ServerLevel serverLevel) {
            BlockPos underPos = BlockPos.containing(center).below();
            BlockState state = level.getBlockState(underPos);
            if (state.isAir()) state = net.minecraft.world.level.block.Blocks.STONE.defaultBlockState();
            net.minecraft.core.particles.ParticleOptions particle = new net.minecraft.core.particles.BlockParticleOption(net.minecraft.core.particles.ParticleTypes.BLOCK, state);
            for (int i = 0; i < count; i++) {
                double angle = 2 * Math.PI * i / count;
                double offsetX = Math.cos(angle) * 1.5;
                double offsetZ = Math.sin(angle) * 1.5;
                double velX = Math.cos(angle) * speed;
                double velZ = Math.sin(angle) * speed;
                serverLevel.sendParticles(particle, center.x + offsetX, center.y + 0.1, center.z + offsetZ, 0, velX, 0.1, velZ, 1.0);
            }
        }
    }

    private void spawnFallingBlocks(Level level, Vec3 pos) {
        BlockPos centerPos = BlockPos.containing(pos.x, pos.y, pos.z);
        int radius = 3;
        for (int x = -radius; x <= radius; x++) {
            for (int z = -radius; z <= radius; z++) {
                BlockPos targetPos = centerPos.offset(x, -1, z);
                double distance = Math.sqrt(x*x + z*z);
                if (distance > radius) continue;
                BlockState state = level.getBlockState(targetPos);
                if (state.isAir() || !state.getFluidState().isEmpty() || state.getDestroySpeed(level, targetPos) < 0) continue;
                float velocityY = (distance <= 1.5) ? 0.4f : 0.7f;
                net.agusdropout.bloodyhell.entity.effects.EntityFallingBlock fallingBlock = new net.agusdropout.bloodyhell.entity.effects.EntityFallingBlock(ModEntityTypes.ENTITY_FALLING_BLOCK.get(), level, state, velocityY);
                fallingBlock.setPos(targetPos.getX() + 0.5, targetPos.getY() + 1, targetPos.getZ() + 0.5);
                level.addFreshEntity(fallingBlock);
            }
        }
    }

    @Override
    public void playSpecialStartSound(Level level, Player player) {
        level.playSound(null, player.getX(), player.getY(), player.getZ(),
                SoundEvents.WARDEN_ATTACK_IMPACT, net.minecraft.sounds.SoundSource.PLAYERS, 1.0f, 0.5f);
    }

    @Override public String getComboGeckoTrigger(int combo) { return "slam_" + combo + "_trigger"; }
    @Override public String getSpecialGeckoTrigger() { return "special_attack"; }
    @Override public String getComboPlayerAnim(int combo) { return "slam_" + combo; }
    @Override public String getSpecialPlayerAnim() { return "special_slam_attack"; }

    @Override
    public SoundEvent getAttackSound(int combo) {
        return SoundEvents.PLAYER_ATTACK_SWEEP;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "Controller", 5, state -> state.setAndContinue(IDLE_ANIM))
                .triggerableAnim("slam_1_trigger", SLAM_1)
                .triggerableAnim("slam_2_trigger", SLAM_2)
                .triggerableAnim("slam_3_trigger", SLAM_3)
                .triggerableAnim("special_attack", SPECIAL_ATK)
                .triggerableAnim("idle_trigger", IDLE_ANIM));
    }

    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(new IClientItemExtensions() {
            private BlasphemousHulkingMassOfIronRenderer renderer;
            @Override
            public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                if (this.renderer == null) this.renderer = new BlasphemousHulkingMassOfIronRenderer();
                return this.renderer;
            }
        });
    }
}