package com.example.examplemod;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.lwjgl.glfw.GLFW;

public class LibrarianMenuScreen extends Screen {

    private static final int MARGIN_BOTTOM = 30;
    private static final int BOX_H = 80;
    private static final int PADDING = 8;
    private static final int BTN_H = 20;
    private static final int BTN_GAP = 4;

    private static final int BOX_BG = 0x99000000;
    private static final int BORDER = 0xFFD0D0D0;
    private static final int NAME_COLOR = 0xFFD700;
    private static final int TEXT_COLOR = 0xFFFFFF;

    private final int entityId;

    public LibrarianMenuScreen(int entityId) {
        super(Component.literal("图书管理员"));
        this.entityId = entityId;
    }

    private int boxWidth() { return (int) (this.width * 0.5); }

    @Override
    protected void init() {
        super.init();

        int boxW = boxWidth();
        int boxX = (this.width - boxW) / 2;
        int boxY = this.height - MARGIN_BOTTOM - BOX_H;

        String[] labels = { "对话", "商店", "任务" };
        Runnable[] actions = {
                () -> ModNetwork.CHANNEL.sendToServer(new C2SOpenDialogNode(entityId, "start")),
                () -> ModNetwork.CHANNEL.sendToServer(new C2SRequestTrade(entityId)),
                () -> ModNetwork.CHANNEL.sendToServer(new C2SRequestQuests(entityId))
        };

        int n = labels.length;
        int totalGap = (n - 1) * BTN_GAP;
        int btnW = (boxW - totalGap) / n;
        int btnY = boxY - BTN_H - 6;

        for (int i = 0; i < n; i++) {
            final Runnable action = actions[i];
            int x = boxX + i * (btnW + BTN_GAP);
            addRenderableWidget(new TextButton(x, btnY, btnW, BTN_H,
                    Component.literal(labels[i]), action));
        }
    }

    @Override
    public void render(GuiGraphics gg, int mouseX, int mouseY, float partialTick) {
        int boxW = boxWidth();
        int boxX = (this.width - boxW) / 2;
        int boxY = this.height - MARGIN_BOTTOM - BOX_H;

        gg.fill(boxX, boxY, boxX + boxW, boxY + BOX_H, BOX_BG);
        gg.fill(boxX, boxY, boxX + boxW, boxY + 1, BORDER);
        gg.fill(boxX, boxY + BOX_H - 1, boxX + boxW, boxY + BOX_H, BORDER);
        gg.fill(boxX, boxY, boxX + 1, boxY + BOX_H, BORDER);
        gg.fill(boxX + boxW - 1, boxY, boxX + boxW, boxY + BOX_H, BORDER);

        gg.drawString(this.font, "图书管理员", boxX + PADDING, boxY + PADDING, NAME_COLOR, true);
        gg.drawString(this.font, "有什么我可以帮你的吗？",
                boxX + PADDING, boxY + PADDING + 15, TEXT_COLOR, false);

        super.render(gg, mouseX, mouseY, partialTick);
    }

    @Override
    public boolean keyPressed(int key, int scan, int mod) {
        if (key == GLFW.GLFW_KEY_ESCAPE || key == GLFW.GLFW_KEY_E) {
            onClose();
            return true;
        }
        return super.keyPressed(key, scan, mod);
    }

    @Override
    public boolean isPauseScreen() { return false; }
}