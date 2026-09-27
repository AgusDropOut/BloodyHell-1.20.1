package net.agusdropout.bloodyhell.block.entity.custom.engine;

import net.agusdropout.bloodyhell.item.ModItems;
import net.agusdropout.bloodyhell.particle.ModParticles;
import net.agusdropout.bloodyhell.particle.ParticleOptions.SmallGlitterParticleOptions;
import net.agusdropout.bloodyhell.particle.ParticleOptions.TinyBloomParticleOptions;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.fluids.capability.IFluidHandler;
import org.joml.Vector3f;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class SoulTetherEngineMode implements IEngineMode {

    public static final Map<UUID, GlobalPos> ACTIVE_TETHERS = new ConcurrentHashMap<>();

    private boolean isMaleficent = false;
    private UUID boundPlayerUUID = null;

    @Override
    public boolean canProcess(RhnullBloodEngineBlockEntity engine) {
        ItemStack coreItem = engine.getRenderStack();
        if (coreItem.getItem() != ModItems.BOUND_BLOOD_FLASK.get()) return false;

        CompoundTag nbt = coreItem.getTag();

        if (nbt == null || !nbt.contains("BloodOwnerName") || nbt.contains("FallenAllyData")) return false;

        if (nbt.hasUUID("BloodOwnerUUID")) {
            this.boundPlayerUUID = nbt.getUUID("BloodOwnerUUID");
            return true;
        }

        return false;
    }

    @Override
    public void tickProcess(Level level, BlockPos pos, BlockState state, RhnullBloodEngineBlockEntity engine) {
        if (level.isClientSide || this.boundPlayerUUID == null) return;

        ACTIVE_TETHERS.put(this.boundPlayerUUID, GlobalPos.of(level.dimension(), pos));

        int bloodAmount = engine.getBloodTank().getFluidAmount();
        ServerPlayer player = null;



        if (engine.getLevel().getServer() != null) {
            player = engine.getLevel().getServer().getPlayerList().getPlayer(this.boundPlayerUUID);
        }

        if (bloodAmount >= 2) {
            this.isMaleficent = false;
            engine.getBloodTank().drain(2, IFluidHandler.FluidAction.EXECUTE);

            if (player != null && player.isAlive() && player.level() == level) {
                double distSq = player.distanceToSqr(pos.getX() + 0.5, pos.getY() + 2.5, pos.getZ() + 0.5);

                if (distSq < 256.0 && level.getGameTime() % 2 == 0) {
                    ServerLevel serverLevel = (ServerLevel) level;
                    double startX = pos.getX() + 0.5;
                    double startY = pos.getY() + 2.0;
                    double startZ = pos.getZ() + 0.5;

                    double targetX = player.getX();
                    double targetY = player.getY() + 1.2;
                    double targetZ = player.getZ();

                    double dx = targetX - startX;
                    double dy = targetY - startY;
                    double dz = targetZ - startZ;

                    double distance = Math.sqrt(dx * dx + dy * dy + dz * dz);
                    if (distance > 0) {
                        dx /= distance;
                        dy /= distance;
                        dz /= distance;
                    }


                    double length = Math.min(1.5, distance - 0.5);
                    int steps = 10;
                    long time = level.getGameTime();

                    for (int i = 0; i <= steps; i++) {
                        double t = (double) i / steps;
                        double currentDist = t * length;

                        double px = startX + dx * currentDist;
                        double py = startY + dy * currentDist;
                        double pz = startZ + dz * currentDist;


                        double swayFactor = t * 0.3;
                        double swayX = Math.sin(time * 0.2 + i * 0.5) * swayFactor;
                        double swayY = Math.cos(time * 0.15 + i * 0.5) * swayFactor;
                        double swayZ = Math.sin(time * 0.25 + i * 0.5) * swayFactor;

                        px += swayX;
                        py += swayY;
                        pz += swayZ;

                        Vector3f bloomColor = new Vector3f(0.8f, 0.05f, 0.05f);
                        Vector3f glitterColor = new Vector3f(1.0f, 0.2f, 0.2f);
                        float size = 0.4f - (float)(t * 0.15f);

                        serverLevel.sendParticles(new TinyBloomParticleOptions(bloomColor, size),
                                px, py, pz, 1, 0.0, 0.0, 0.0, 0.0);

                        if (i % 2 == 0) {
                            serverLevel.sendParticles(new SmallGlitterParticleOptions(glitterColor, size, false, 10, true),
                                    px, py, pz, 1, 0.0, 0.0, 0.0, 0.0);
                        }
                    }
                }
            }
        } else {
            this.isMaleficent = true;

            if (player != null && player.isAlive() && engine.getLevel().getGameTime() % 20 == 0) {
                player.hurt(player.damageSources().magic(), 1.0f);
                System.out.println("hola");

                if (player.level() instanceof ServerLevel serverLevel) {
                    serverLevel.sendParticles(ModParticles.VICERAL_PARTICLE.get(),
                            player.getX(), player.getY() + 1.0D, player.getZ(),
                            5, 0.3D, 0.5D, 0.3D, 0.1D);
                }
            }
        }
    }

    @Override
    public void finishProcess(Level level, BlockPos pos, RhnullBloodEngineBlockEntity engine) {
    }

    @Override
    public void resetProcess() {
        if (this.boundPlayerUUID != null) {
            ACTIVE_TETHERS.remove(this.boundPlayerUUID);
        }
        this.isMaleficent = false;
    }

    @Override
    public Vector3f getCustomBaseColor() {
        return isMaleficent ? new Vector3f(0.12f, 0.01f, 0.01f) : null;
    }

    @Override
    public Vector3f getCustomGlowColor() {
        return isMaleficent ? new Vector3f(1.0f, 0.1f, 0.1f) : null;
    }
}