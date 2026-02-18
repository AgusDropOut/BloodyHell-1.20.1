package net.agusdropout.bloodyhell.animation;

import net.minecraft.resources.ResourceLocation;
import dev.kosmx.playerAnim.core.data.KeyframeAnimation;
import dev.kosmx.playerAnim.minecraftApi.PlayerAnimationRegistry;

public class ModAnimations {


    public static final ResourceLocation DAGGER_ATTACK = new ResourceLocation("bloodyhell", "dagger_attack");

    public static KeyframeAnimation getAttackAnimation() {
        return PlayerAnimationRegistry.getAnimation(DAGGER_ATTACK);
    }
}