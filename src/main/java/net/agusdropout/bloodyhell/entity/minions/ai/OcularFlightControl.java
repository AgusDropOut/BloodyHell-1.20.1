package net.agusdropout.bloodyhell.entity.minions.ai;


import net.agusdropout.bloodyhell.entity.minions.custom.WeepingOcularEntity;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.phys.Vec3;

public class OcularFlightControl extends MoveControl {
    private final WeepingOcularEntity ocular;

    public OcularFlightControl(WeepingOcularEntity ocular) {
        super(ocular);
        this.ocular = ocular;
    }

    @Override
    public void tick() {
        if (this.operation == MoveControl.Operation.MOVE_TO) {
            Vec3 movement = new Vec3(this.wantedX - this.ocular.getX(), this.wantedY - this.ocular.getY(), this.wantedZ - this.ocular.getZ());
            double distance = movement.length();

            if (distance < this.ocular.getBoundingBox().getSize()) {
                this.operation = MoveControl.Operation.WAIT;
                this.ocular.setDeltaMovement(this.ocular.getDeltaMovement().scale(0.5D));
            } else {
                this.ocular.setDeltaMovement(this.ocular.getDeltaMovement().add(movement.scale(this.speedModifier * 0.05D / distance)));

                if (this.ocular.getTarget() == null) {
                    Vec3 delta = this.ocular.getDeltaMovement();
                    this.ocular.setYRot(-((float) Mth.atan2(delta.x, delta.z)) * (180F / (float) Math.PI));
                    this.ocular.yBodyRot = this.ocular.getYRot();
                } else {
                    double dx = this.ocular.getTarget().getX() - this.ocular.getX();
                    double dz = this.ocular.getTarget().getZ() - this.ocular.getZ();
                    this.ocular.setYRot(-((float) Mth.atan2(dx, dz)) * (180F / (float) Math.PI));
                    this.ocular.yBodyRot = this.ocular.getYRot();
                }
            }
        }
    }
}
