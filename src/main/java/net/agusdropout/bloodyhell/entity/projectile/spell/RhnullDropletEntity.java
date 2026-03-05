package net.agusdropout.bloodyhell.entity.projectile.spell;

import net.agusdropout.bloodyhell.entity.ModEntityTypes;
import net.agusdropout.bloodyhell.entity.projectile.base.AbstractColoredProjectile;
import net.agusdropout.bloodyhell.particle.ParticleOptions.RadialDistortionParticleOptions;
import net.agusdropout.bloodyhell.particle.ParticleOptions.SmallGlitterParticleOptions;
import net.agusdropout.bloodyhell.util.visuals.ParticleHelper;
import net.agusdropout.bloodyhell.util.visuals.SpellPalette;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import org.joml.Vector3f;
import net.agusdropout.bloodyhell.util.visuals.ColorHelper;

public class RhnullDropletEntity extends AbstractColoredProjectile {

    public RhnullDropletEntity(EntityType<? extends Projectile> type, Level level) {
        super(type, level);
    }

    public RhnullDropletEntity(Level level, double x, double y, double z) {
        super(ModEntityTypes.RHNULL_DROPLET_PROJECTILE.get(), level);
        this.setPos(x, y, z);
        this.setDamage(4.0f);

        int baseHex = ColorHelper.vector3fToHex(SpellPalette.RHNULL.getColor(0));
        int highlightHex = ColorHelper.vector3fToHex(SpellPalette.RHNULL.getColor(1));
        this.setProjectileColors(baseHex, highlightHex);
    }

    @Override
    protected void handleClientEffects() {
        if (this.lifeTicks == 1) {
            ParticleHelper.spawn(this.level(), new RadialDistortionParticleOptions(this.getXRot(), this.getYRot(), 10), this.getX(), this.getY(), this.getZ(), 0.0, 0.0, 0.0);
        }

        if (this.random.nextFloat() < 0.9f) {
            double vxPercent = this.getDeltaMovement().x / 15;
            double vyPercent = this.getDeltaMovement().y / 15;
            double vzPercent = this.getDeltaMovement().z / 15;

            Vector3f gradientColor = ParticleHelper.gradient3(random.nextFloat(), new Vector3f(1f, 0.97f, 0.0f), new Vector3f(1.0f, 0.8f, 0.0f), new Vector3f(1f, 0.5f, 0.0f));

            ParticleHelper.spawn(this.level(), new SmallGlitterParticleOptions(gradientColor, 0.7f, false, 10), this.getX(), this.getY(), this.getZ(), vxPercent, vyPercent, vzPercent);
        }
    }
}