package com.example.examplemod;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;

public class DialogueScreen extends Screen {

    private static final int MARGIN_BOTTOM = 30;
    private static final int BOX_H = 80;
    private static final int PADDING = 8;
    private static final int LINE_H = 11;
    private static final int MAX_LINES = 3;
    private static final int BTN_H = 20;
    private static final int BTN_GAP = 4;

    private static final int BOX_BG = 0x99000000;
    private static final int BORDER = 0xFFD0D0D0;
    private static final int NAME_COLOR = 0xFFD700;
    private static final int TEXT_COLOR = 0xFFFFFF;
    private static final int HINT_COLOR = 0xAAAAAA;

    private final int entityId;
    private final String speaker;
    private final String text;
    private final String action;
    private final List<String> optionTexts;
    private final List<String> optionNexts;

    private final List<String> currentPages = new ArrayList<>();
    private int pageIndex = 0;

    public DialogueScreen(S2COpenDialogue packet) {
        super(Component.literal("Dialogue"));
        this.entityId = packet.entityId;
        this.speaker = packet.speaker;
        this.text = packet.text;
        this.action = packet.action;
        this.optionTexts = packet.optionTexts;
        this.optionNexts = packet.optionNexts;
    }

    private int boxWidth() { return (int) (this.width * 0.5); }

    private void drawSmall(GuiGraphics gg, String text, int x, int y, int color, boolean shadow) {
        PoseStack pose = gg.pose();
        pose.pushPose();
        pose.translate(x, y, 0);
        pose.scale(0.5f, 0.5f, 1f);
        gg.drawString(this.font, text, 0, 0, color, shadow);
        pose.popPose();
    }

    @Override
    protected void init() {
        super.init();
        buildPages();

        if (!optionTexts.isEmpty()) {
            int boxW = boxWidth();
            int boxX = (this.width - boxW) / 2;
            int boxY = this.height - MARGIN_BOTTOM - BOX_H;

            int n = optionTexts.size();
            int totalGap = (n - 1) * BTN_GAP;
            int btnW = (boxW - totalGap) / n;
            int btnY = boxY - BTN_H - 6;
            int startX = boxX;

            for (int i = 0; i < n; i++) {
                final String next = optionNexts.get(i);
                int x = startX + i * (btnW + BTN_GAP);
                addRenderableWidget(new TextButton(x, btnY, btnW, BTN_H,
                        Component.literal(optionTexts.get(i)),
                        () -> ModNetwork.CHANNEL.sendToServer(
                                new C2SOpenDialogNode(entityId, next))));
            }
        }
    }

    private void buildPages() {
        currentPages.clear();
        pageIndex = 0;
        int maxWidth = boxWidth() - 2 * PADDING;
        List<String> lines = wrapText(text, maxWidth);
        for (int i = 0; i < lines.size(); i += MAX_LINES) {
            StringBuilder sb = new StringBuilder();
            for (int j = i; j < Math.min(i + MAX_LINES, lines.size()); j++) {
                if (sb.length() > 0) sb.append("\n");
                sb.append(lines.get(j));
            }
            currentPages.add(sb.toString());
        }
        if (currentPages.isEmpty()) currentPages.add("");
    }

    private List<String> wrapText(String text, int maxWidth) {
        List<String> result = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            if (c == '\n') {
                result.add(current.toString());
                current = new StringBuilder();
                continue;
            }
            String test = current.toString() + c;
            if (this.font.width(test) > maxWidth * 2) {
                result.add(current.toString());
                current = new StringBuilder();
            }
            current.append(c);
        }
        if (current.length() > 0) result.add(current.toString());
        return result;
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

        drawSmall(gg, speaker, boxX + PADDING, boxY + PADDING, NAME_COLOR, true);

        if (pageIndex < currentPages.size()) {
            int textY = boxY + PADDING + LINE_H + 4;
            for (String line : currentPages.get(pageIndex).split("\n", -1)) {
                drawSmall(gg, line, boxX + PADDING, textY, TEXT_COLOR, false);
                textY += LINE_H;
            }
        }

        if (optionTexts.isEmpty()) {
            boolean lastPage = pageIndex >= currentPages.size() - 1;
            String hint = lastPage
                    ? (action != null ? actionHint() : "▶ 点击 / 空格 结束")
                    : "▶ 点击 / 空格 继续";
            int hintW = this.font.width(hint) / 2;
            drawSmall(gg, hint,
                    boxX + boxW - PADDING - hintW,
                    boxY + BOX_H - PADDING - 8, HINT_COLOR, false);
        }

        super.render(gg, mouseX, mouseY, partialTick);
    }

    private String actionHint() {
        return switch (action) {
            case "open_trade" -> "▶ 点击 / 空格 交易";
            case "open_quests" -> "▶ 点击 / 空格 查看任务";
            default -> "▶ 点击 / 空格 结束";
        };
    }

    @Override
    public boolean mouseClicked(double mx, double my, int button) {
        if (super.mouseClicked(mx, my, button)) return true;
        if (optionTexts.isEmpty()) {
            advance();
            return true;
        }
        return false;
    }

    @Override
    public boolean keyPressed(int key, int scan, int mod) {
        if (key == GLFW.GLFW_KEY_ESCAPE || key == GLFW.GLFW_KEY_E) {
            onClose();
            return true;
        }
        if (optionTexts.isEmpty()
                && (key == GLFW.GLFW_KEY_SPACE || key == GLFW.GLFW_KEY_ENTER)) {
            advance();
            return true;
        }
        return super.keyPressed(key, scan, mod);
    }

    private void advance() {
        if (pageIndex < currentPages.size() - 1) { pageIndex++; return; }
        if (action != null) {
            switch (action) {
                case "open_trade" -> {
                    onClose();
                    ModNetwork.CHANNEL.sendToServer(new C2SRequestTrade(entityId));
                    return;
                }
                case "open_quests" -> {
                    onClose();
                    ModNetwork.CHANNEL.sendToServer(new C2SRequestQuests(entityId));
                    return;
                }
                default -> { onClose(); return; }
            }
        }
        onClose();
    }

    @Override
    public boolean isPauseScreen() { return false; }
}