package net.agusdropout.bloodyhell.devtools;

import net.agusdropout.bloodyhell.BloodyHell;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class ShaderCycleTester {
    private static final String TAG = "[BHTEST] ";
    private static final String[] IRIS_PACKAGES = {"net.coderbot.iris", "net.irisshaders.iris"};
    private static final int RELOAD_TICKS = 60;
    private static final int STOP_TICKS = 60;
    private static final int STOPS_PER_PACK = 3;

    private final List<String> packs;
    private final Class<?> irisClass;
    private final Object config;
    private int packIndex = -1;
    private int stop = 0;
    private int ticksLeft = 0;
    private boolean disabling = false;
    private String pendingVerification;
    private boolean finished = false;
    private int failures = 0;

    public ShaderCycleTester(List<String> packs) {
        this.packs = new ArrayList<>(packs);
        Class<?> resolved = null;
        Object resolvedConfig = null;
        for (String prefix : IRIS_PACKAGES) {
            try {
                resolved = Class.forName(prefix + ".Iris");
                resolvedConfig = resolved.getMethod("getIrisConfig").invoke(null);
                break;
            } catch (ReflectiveOperationException ignored) {
                resolved = null;
            }
        }
        this.irisClass = resolved;
        this.config = resolvedConfig;
    }

    public boolean isAvailable() {
        return irisClass != null && config != null;
    }

    public boolean isFinished() {
        return finished;
    }

    public int getFailures() {
        return failures;
    }

    public void tick(Minecraft minecraft) {
        if (finished) return;
        if (ticksLeft > 0) {
            ticksLeft--;
            return;
        }
        if (pendingVerification != null) {
            verifyActive(pendingVerification);
            pendingVerification = null;
        }
        if (disabling) {
            finishRun();
            return;
        }
        if (packIndex >= 0 && stop < STOPS_PER_PACK) {
            visitStop(minecraft);
            return;
        }
        packIndex++;
        if (packIndex >= packs.size()) {
            setShaders(null);
            disabling = true;
            ticksLeft = RELOAD_TICKS;
            return;
        }
        setShaders(packs.get(packIndex));
        pendingVerification = packs.get(packIndex);
        stop = 0;
        ticksLeft = RELOAD_TICKS;
    }

    private void visitStop(Minecraft minecraft) {
        TestWorldCommand.Layout layout = TestWorldCommand.getLastLayout();
        if (layout != null && minecraft.player != null) {
            BlockPos target = switch (stop) {
                case 0 -> layout.blockOrigin();
                case 1 -> layout.entityOrigin();
                default -> layout.itemOrigin();
            };
            minecraft.player.connection.sendCommand("tp @s " + (target.getX() + 24) + " " + (target.getY() + 3) + " " + (target.getZ() + 4));
        }
        stop++;
        ticksLeft = STOP_TICKS;
    }

    private void setShaders(String pack) {
        try {
            Method enabled = config.getClass().getMethod("setShadersEnabled", boolean.class);
            enabled.invoke(config, pack != null);
            if (pack != null) {
                config.getClass().getMethod("setShaderPackName", String.class).invoke(config, pack);
            }
            config.getClass().getMethod("save").invoke(config);
            irisClass.getMethod("reload").invoke(null);
            BloodyHell.LOGGER.info(TAG + "shader switched to " + (pack == null ? "none" : pack));
        } catch (ReflectiveOperationException | RuntimeException exception) {
            failures++;
            BloodyHell.LOGGER.error(TAG + "shader switch failed for " + pack, exception);
        }
    }

    private void verifyActive(String pack) {
        try {
            Object current = irisClass.getMethod("getCurrentPackName").invoke(null);
            if (!pack.equals(String.valueOf(current))) {
                failures++;
                BloodyHell.LOGGER.error(TAG + "shader pack " + pack + " did not load, active: " + current);
            }
        } catch (ReflectiveOperationException exception) {
            failures++;
            BloodyHell.LOGGER.error(TAG + "could not read the active shader pack", exception);
        }
    }

    private void finishRun() {
        finished = true;
        BloodyHell.LOGGER.info(TAG + "shader cycle finished packs=" + packs.size() + " failures=" + failures);
    }
}
