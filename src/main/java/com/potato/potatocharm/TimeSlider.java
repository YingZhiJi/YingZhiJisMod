package com.potato.potatocharm;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class TimeSlider extends AbstractSliderButton {

    private static final int KNOB_W = 6;
    private static final int KNOB_H = 10;

    private int userInteractionTicks = 0;

    public TimeSlider(int x, int y, int w, int h, double initialValue) {
        super(x, y, w, h, Component.empty(), initialValue);
        updateMessage();
    }

    @Override
    protected void updateMessage() {
        int tod = (int)(this.value * 24000);
        this.setMessage(Component.literal(formatTime(tod)));
    }

    @Override
    protected void applyValue() {
        userInteractionTicks = 20;
    }

    public void tickSlider() {
        if (userInteractionTicks > 0) userInteractionTicks--;
    }

    public boolean isUserInteracting() {
        return userInteractionTicks > 0;
    }

    public int getTime() {
        return (int)(this.value * 24000);
    }

    public double getSliderValue() {
        return this.value;
    }

    public void setSliderValue(double v) {
        double old = this.value;
        this.value = Mth.clamp(v, 0.0, 1.0);
        if (old != this.value) {
            updateMessage();
        }
    }

    public static String formatTime(int tod) {
        int shifted = ((tod + 6000) % 24000 + 24000) % 24000;
        int hours = shifted / 1000;
        int minutes = (int)((shifted % 1000) * 60 / 1000.0);
        return String.format("%02d:%02d", hours, minutes);
    }

    @Override
    public void renderWidget(GuiGraphics gg, int mouseX, int mouseY, float partialTick) {
        int trackH = 4;
        int trackY = this.getY() + (this.height - trackH) / 2;
        gg.fill(this.getX(), trackY,
                this.getX() + this.width, trackY + trackH, 0xFF000000);
        gg.fill(this.getX() + 1, trackY + 1,
                this.getX() + this.width - 1, trackY + trackH - 1, 0xFF606060);

        int knobX = this.getX() + (int)(this.value * (double)(this.width - KNOB_W));
        int knobY = this.getY() + (this.height - KNOB_H) / 2;
        gg.fill(knobX, knobY, knobX + KNOB_W, knobY + KNOB_H, 0xFF202020);
        gg.fill(knobX + 1, knobY + 1, knobX + KNOB_W - 1, knobY + KNOB_H - 1, 0xFFD0D0D0);

        int textColor = this.active ? 0xFFFFFF : 0xA0A0A0;
        this.renderScrollingString(gg, Minecraft.getInstance().font, 2, textColor);
    }
}