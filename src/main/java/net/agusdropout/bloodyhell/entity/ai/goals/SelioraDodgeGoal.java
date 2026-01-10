package net.agusdropout.bloodyhell.entity.ai.goals;

import net.agusdropout.bloodyhell.entity.custom.SelioraEntity;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;
import java.util.List;
import java.util.Random;

public class SelioraDodgeGoal extends Goal {
    private final SelioraEntity seliora;
    private int dodgeTimer;
    private final int MAX_DODGE_DURATION = 15; // 0.75 segundos

    // CAMBIO: Aumentado de 1.5f a 2.3f para que recorra más distancia rápido
    private final float DODGE_SPEED = 2.3f;

    public SelioraDodgeGoal(SelioraEntity seliora) {
        this.seliora = seliora;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.JUMP));
    }

    @Override
    public boolean canUse() {
        if (!seliora.canUseDodge() || seliora.getTarget() == null) {
            return false;
        }

        // A. Jugador demasiado cerca (Melee) - Aumentado rango de reacción a 5
        if (seliora.distanceTo(seliora.getTarget()) < 5.0D) {
            return true;
        }

        // B. Proyectiles
        AABB perceptionBox = seliora.getBoundingBox().inflate(5.0D);
        List<Projectile> projectiles = seliora.level().getEntitiesOfClass(Projectile.class, perceptionBox);

        for (Projectile p : projectiles) {
            if (p.getOwner() != seliora && p.getDeltaMovement().lengthSqr() > 0.1) {
                Vec3 toSeliora = seliora.position().subtract(p.position()).normalize();
                Vec3 projectileDir = p.getDeltaMovement().normalize();
                if (projectileDir.dot(toSeliora) > 0.8) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public boolean canContinueToUse() {
        return dodgeTimer > 0;
    }

    @Override
    public void start() {
        this.dodgeTimer = MAX_DODGE_DURATION;
        this.seliora.setDodgeActive(true);
        this.seliora.getNavigation().stop();

        LivingEntity target = seliora.getTarget();
        Vec3 lookVec = seliora.getLookAngle();
        Vec3 dodgeVec;

        Random rand = new Random();
        int direction = rand.nextInt(3); // 0: Izquierda, 1: Derecha, 2: Atrás

        if (target != null && direction == 2) {
            Vec3 toTarget = target.position().subtract(seliora.position()).normalize();
            dodgeVec = toTarget.scale(-1);
        } else {
            Vec3 right = lookVec.cross(new Vec3(0, 1, 0)).normalize();
            if (direction == 0) dodgeVec = right.scale(-1);
            else dodgeVec = right;
        }

        // CAMBIO: Agregado 0.4 en Y para un pequeño "salto" que reduce fricción y alarga la distancia
        seliora.setDeltaMovement(dodgeVec.x * DODGE_SPEED, 0, dodgeVec.z * DODGE_SPEED);

        if (target != null) {
            seliora.getLookControl().setLookAt(target, 30.0F, 30.0F);
        }

        // Sonido de "dash" mágico
        seliora.playSound(SoundEvents.PLAYER_ATTACK_SWEEP, 1.0f, 2.0f);
    }

    @Override
    public void tick() {
        // CAMBIO: Mantener inercia los primeros ticks para que no frene de golpe
        if (dodgeTimer > 5) {
            // Reducimos la fricción del aire artificialmente para que deslice más
            Vec3 current = seliora.getDeltaMovement();
            seliora.setDeltaMovement(current.x * 0.95, current.y, current.z * 0.95);
        }
        this.dodgeTimer--;
    }

    @Override
    public void stop() {
        this.seliora.setDodgeActive(false);
        this.seliora.setDodgeCooldown(60); // 3 segundos de cooldown
    }
}