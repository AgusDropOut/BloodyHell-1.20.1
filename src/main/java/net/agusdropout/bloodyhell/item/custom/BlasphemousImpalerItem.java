package net.agusdropout.bloodyhell.item.custom;

import net.agusdropout.bloodyhell.entity.projectile.BlasphemousImpalerEntity;
import net.agusdropout.bloodyhell.item.client.BlasphemousImpalerItemRenderer;
import net.agusdropout.bloodyhell.item.client.ClientItemHooks;
import net.agusdropout.bloodyhell.item.custom.base.BaseThrowableMeleeComboWeapon;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
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

public class BlasphemousImpalerItem extends BaseThrowableMeleeComboWeapon {

    private static final int COMBO_1_DURATION = 25;
    private static final int COMBO_2_DURATION = 30;
    private static final int COMBO_3_DURATION = 15;

    private static final int THROW_SPAWN_TICK = 10;
    private static final int THROW_DURATION = 20;
    private static final int THROW_COST = 10;

    private static final RawAnimation THROW_ANIM = RawAnimation.begin().thenPlay("throw");
    private static final RawAnimation ATK_1_ANIM = RawAnimation.begin().thenPlay("spear_attack_1");
    private static final RawAnimation ATK_2_ANIM = RawAnimation.begin().thenPlay("spear_attack_2");
    private static final RawAnimation ATK_3_ANIM = RawAnimation.begin().thenPlay("spear_attack_3");

    public BlasphemousImpalerItem(Tier tier, int damage, float speed, Properties props) {
        super(tier, damage, speed, props);
        SingletonGeoAnimatable.registerSyncedAnimatable(this);
    }

    @Override public int getMaxCombos() { return 3; }

    @Override
    public int getComboDuration(int combo) {
        return switch (combo) {
            case 2 -> COMBO_2_DURATION;
            case 3 -> COMBO_3_DURATION;
            default -> COMBO_1_DURATION;
        };
    }

    @Override
    public int getDamageTick(int combo) {
        return switch (combo) {
            case 2 -> 15;
            case 3 -> 10;
            default -> 15;
        };
    }

    @Override public float getComboDamageBonus(ItemStack stack) { return 0.0f; }

    @Override
    public void performComboDamage(Level level, Player player, int combo, ItemStack stack) {
        float multiplier = (combo == 3) ? 1.5f : ((combo == 2) ? 2.0f : 1.0f);

        Vec3 look = player.getLookAngle();
        Vec3 center = player.getEyePosition().add(look.scale(2.5));
        AABB damageBox = new AABB(center.x - 1.5, center.y - 1, center.z - 1.5, center.x + 1.5, center.y + 1, center.z + 1.5);

        boolean hit = false;
        for (LivingEntity target : level.getEntitiesOfClass(LivingEntity.class, damageBox)) {
            if (target != player && !target.isAlliedTo(player)) {
                target.hurt(level.damageSources().mobAttack(player), this.getDamage() * multiplier);
                hit = true;
            }
        }
        if (hit) level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.TRIDENT_HIT, net.minecraft.sounds.SoundSource.PLAYERS, 1.0f, 1.0f);
    }

    @Override
    public SoundEvent getAttackSound(int combo) {
        return SoundEvents.PLAYER_ATTACK_SWEEP;
    }

    @Override public int getThrowCost() { return THROW_COST; }
    @Override public int getThrowSpawnTick() { return THROW_SPAWN_TICK; }
    @Override public int getThrowDuration() { return THROW_DURATION; }

    @Override
    public void spawnProjectile(Level level, Player player, ItemStack stack) {
        CompoundTag tag = stack.getOrCreateTag();
        resetAllStates(tag);
        BlasphemousImpalerEntity projectile = new BlasphemousImpalerEntity(level, player, stack);
        projectile.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, 4.5F, 1.0F);
        projectile.setBaseDamage(10.0);

        level.addFreshEntity(projectile);


        stack.shrink(1);

        level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.TRIDENT_THROW, net.minecraft.sounds.SoundSource.PLAYERS, 2.0F, 0.5F);
        level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.WITHER_SHOOT, net.minecraft.sounds.SoundSource.PLAYERS, 0.8F, 0.7F);
    }

    @Override public String getChargePlayerAnim() { return "charge"; }
    @Override public String getThrowPlayerAnim() { return "throw"; }
    @Override public String getChargeGeckoAnim() { return "charge"; }
    @Override public String getThrowGeckoTrigger() { return "throw_trigger"; }

    @Override
    public String getIdleGeckoAnim() {
        return "idle";
    }

    @Override public String getComboPlayerAnim(int combo) {
        return switch(combo) {
            case 2 -> "spear_attack_2";
            case 3 -> "spear_attack_3";
            default -> "spear_attack_1";
        };
    }

    @Override public String getComboGeckoTrigger(int combo) {
        return switch(combo) {
            case 2 -> "attack_2_trigger";
            case 3 -> "attack_3_trigger";
            default -> "attack_1_trigger";
        };
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "Controller", 5, this::throwableControllerPredicate)
                .triggerableAnim("attack_1_trigger", ATK_1_ANIM)
                .triggerableAnim("attack_2_trigger", ATK_2_ANIM)
                .triggerableAnim("attack_3_trigger", ATK_3_ANIM)
                .triggerableAnim("throw_trigger", THROW_ANIM)
                .triggerableAnim("idle_trigger", IDLE_ANIM));


    }

    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(new IClientItemExtensions() {
            private BlasphemousImpalerItemRenderer renderer;
            @Override
            public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                if (this.renderer == null) this.renderer = new BlasphemousImpalerItemRenderer();
                return this.renderer;
            }
        });
    }
}