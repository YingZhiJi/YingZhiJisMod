package com.example.examplemod;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;

public class TextButton extends AbstractWidget {

    private static final int BG = 0x99000000;
    private static final int BORDER = 0xFFD0D0D0;
    private static final int TEXT_NORMAL = 0xFFFFFF;
    private static final int TEXT_HOVER = 0xFFFF80;

    private Runnable onClick;

    public TextButton(int x, int y, int w, int h, Component text, Runnable onClick) {
        super(x, y, w, h, text);
        this.onClick = onClick;
    }

    @Override
    public void onClick(double mx, double my) {
        if (onClick != null) onClick.run();
    }

    @Override
    protected void renderWidget(GuiGraphics gg, int mouseX, int mouseY, float partialTick) {
        int x = getX(), y = getY(), w = getWidth(), h = getHeight();

        gg.fill(x, y, x + w, y + h, BG);
        gg.fill(x, y, x + w, y + 1, BORDER);
        gg.fill(x, y + h - 1, x + w, y + h, BORDER);
        gg.fill(x, y, x + 1, y + h, BORDER);
        gg.fill(x + w - 1, y, x + w, y + h, BORDER);

        Minecraft mc = Minecraft.getInstance();
        String text = getMessage().getString();
        int color = isHovered ? TEXT_HOVER : TEXT_NORMAL;
        int tx = x + (w - mc.font.width(text)) / 2;
        int ty = y + (h - 8) / 2;
        gg.drawString(mc.font, text, tx, ty, color, false);
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput out) {
        this.defaultButtonNarrationText(out);
    }
}