package net.agusdropout.bloodyhell.item.custom;

import net.agusdropout.bloodyhell.entity.projectile.SpecialSlashEntity;
import net.agusdropout.bloodyhell.item.client.BlasphemousTwinDaggersRenderer;
import net.agusdropout.bloodyhell.item.custom.base.BaseMeleeComboWeapon;
import net.agusdropout.bloodyhell.sound.ModSounds;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import software.bernie.geckolib.animatable.SingletonGeoAnimatable;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;

import java.util.List;
import java.util.function.Consumer;

public class BlasphemousTwinDaggerItem extends BaseMeleeComboWeapon {

    public static final float COMBO_2_BONUS = 2.0f;
    public static final float COMBO_3_BONUS = 4.0f;

    private static final RawAnimation ATTACK_1 = RawAnimation.begin().thenPlay("attack_1");
    private static final RawAnimation ATTACK_2 = RawAnimation.begin().thenPlay("attack_2");
    private static final RawAnimation ATTACK_3 = RawAnimation.begin().thenPlay("attack_3");
    private static final RawAnimation SPECIAL_ATK = RawAnimation.begin().thenPlay("special_attack");

    public BlasphemousTwinDaggerItem(Tier tier, int attackDamageIn, float attackSpeedIn, Properties builder) {
        super(tier, attackDamageIn, attackSpeedIn, builder);
        SingletonGeoAnimatable.registerSyncedAnimatable(this);
    }

    @Override public int getMaxCombos() { return 3; }
    @Override public int getSpecialCost() { return 5; }
    @Override public int getSpecialDuration() { return 40; }

    @Override
    public int getComboDuration(int combo) {
        return switch (combo) {
            case 2 -> 18;
            case 3 -> 25;
            default -> 18;
        };
    }

    @Override
    public int getDamageTick(int combo) {
        return switch (combo) {
            case 2 -> 8;
            case 3 -> 10;
            default -> 8;
        };
    }

    @Override
    public float getComboDamageBonus(ItemStack stack) {
        int combo = stack.getOrCreateTag().getInt("CurrentCombo");
        return switch (combo) {
            case 2 -> COMBO_2_BONUS;
            case 3 -> COMBO_3_BONUS;
            default -> 0.0f;
        };
    }

    @Override
    public void performComboDamage(Level level, Player player, int combo, ItemStack stack) {
        float baseDamage = this.getDamage();
        float bonus = 0;
        double rangeForward;
        double width;

        if (combo == 3) {
            rangeForward = 3.5;
            width = 1.0;
            bonus = COMBO_3_BONUS;
            if (level instanceof ServerLevel serverLevel) {
                Vec3 look = player.getLookAngle();
                for (int i = 0; i < 5; i++) {
                    serverLevel.sendParticles(ParticleTypes.SWEEP_ATTACK,
                            player.getX() + look.x * i * 0.5,
                            player.getEyeY() + look.y * i * 0.5,
                            player.getZ() + look.z * i * 0.5,
                            1, 0.1, 0.1, 0.1, 0.0);
                }
            }
        } else {
            rangeForward = 2.0;
            width = 2.5;
            bonus = (combo == 2) ? COMBO_2_BONUS : 0.0f;
        }

        float totalDamage = baseDamage + bonus;
        Vec3 look = player.getLookAngle();
        Vec3 origin = player.position().add(0, player.getEyeHeight() * 0.5, 0);
        Vec3 center = origin.add(look.scale(rangeForward * 0.5));
        AABB damageBox = new AABB(center, center).inflate(width * 0.5, 1.0, rangeForward * 0.5);
        List<LivingEntity> targets = level.getEntitiesOfClass(LivingEntity.class, damageBox);

        for (LivingEntity target : targets) {
            if (target != player && !target.isAlliedTo(player) && target.isAlive()) {
                Vec3 dirToTarget = target.position().subtract(player.position()).normalize();
                if (look.dot(dirToTarget) > 0.3) {
                    target.hurt(level.damageSources().playerAttack(player), totalDamage);
                    target.knockback(0.4F, -look.x, -look.z);
                }
            }
        }
    }

    @Override
    public void performSpecialTickLogic(Level level, Player player, ItemStack stack, int currentTick) {
        if (currentTick == 10) {
            float damage = 10.0f;
            SpecialSlashEntity slash = new SpecialSlashEntity(level, player, damage);
            level.addFreshEntity(slash);
        }
    }

    @Override
    public void playSpecialStartSound(Level level, Player player) {
        level.playSound(null, player.getX(), player.getY(), player.getZ(),
                SoundEvents.BLAZE_SHOOT, net.minecraft.sounds.SoundSource.PLAYERS, 0.5f, 0.8f + (player.getRandom().nextFloat() * 0.4f));
    }

    @Override public String getComboGeckoTrigger(int combo) { return "attack_" + combo + "_trigger"; }
    @Override public String getSpecialGeckoTrigger() { return "special_attack_trigger"; }
    @Override public String getComboPlayerAnim(int combo) { return "dagger_attack_" + combo; }
    @Override public String getSpecialPlayerAnim() { return "dagger_special_attack"; }

    @Override
    public SoundEvent getAttackSound(int combo) {
        return switch (combo) {
            case 2 -> ModSounds.DAGGER_ATTACK_2.get();
            case 3 -> ModSounds.DAGGER_ATTACK_3.get();
            default -> ModSounds.DAGGER_ATTACK_1.get();
        };
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "Controller", 5, state -> state.setAndContinue(IDLE_ANIM))
                .triggerableAnim("attack_1_trigger", ATTACK_1)
                .triggerableAnim("attack_2_trigger", ATTACK_2)
                .triggerableAnim("attack_3_trigger", ATTACK_3)
                .triggerableAnim("special_attack_trigger", SPECIAL_ATK)
                .triggerableAnim("idle_trigger", IDLE_ANIM));
    }

    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(new IClientItemExtensions() {
            private BlasphemousTwinDaggersRenderer renderer;
            @Override
            public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                if (this.renderer == null) this.renderer = new BlasphemousTwinDaggersRenderer();
                return this.renderer;
            }
        });
    }
}