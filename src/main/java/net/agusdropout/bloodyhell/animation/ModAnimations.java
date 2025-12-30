package net.agusdropout.bloodyhell.animation;

import net.minecraft.resources.ResourceLocation;
import dev.kosmx.playerAnim.core.data.KeyframeAnimation;
import dev.kosmx.playerAnim.minecraftApi.PlayerAnimationRegistry;

public class ModAnimations {

    // Identificador de tu animación
    public static final ResourceLocation DAGGER_ATTACK = new ResourceLocation("bloodyhell", "dagger_attack");

    // Método para obtener la animación cargada (útil para validaciones)
    public static KeyframeAnimation getAttackAnimation() {
        // Player Animator carga los JSONs de la carpeta assets automáticamente al iniciar.
        // Aquí solo la pedimos por su nombre (sin .json).
        return PlayerAnimationRegistry.getAnimation(DAGGER_ATTACK);
    }
}