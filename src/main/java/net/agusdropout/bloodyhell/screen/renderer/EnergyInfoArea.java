package net.agusdropout.bloodyhell.screen.renderer;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.network.chat.Component;
import net.minecraftforge.energy.IEnergyStorage;

import java.util.List;
public class EnergyInfoArea extends InfoArea{
    private final IEnergyStorage energy;



    public EnergyInfoArea(int xMin, int yMin, IEnergyStorage energy)  {
        this(xMin, yMin, energy,16,62);
    }

    public EnergyInfoArea(int xMin, int yMin, IEnergyStorage energy, int width, int height)  {
        super(new Rect2i(xMin, yMin, width, height));
        this.energy = energy;
    }

    public List<Component> getTooltips() {
        return List.of(Component.literal(energy.getEnergyStored()+"/"+energy.getMaxEnergyStored()+" BLOOD"));
    }

    @Override
    public void draw(GuiGraphics transform) {
        final int height = area.getHeight();
        int stored = (int)(height * (energy.getEnergyStored() / (float)energy.getMaxEnergyStored()));

        transform.fill( area.getX(), area.getY() + (height - stored), area.getX() + area.getWidth(), area.getY() + area.getHeight(), 0xffffc400);
    }

}

