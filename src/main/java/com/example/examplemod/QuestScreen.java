package com.example.examplemod;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;
import org.lwjgl.glfw.GLFW;

import java.util.List;

public class QuestScreen extends Screen {

    private static final float SCALE = 0.85F;

    private static final int MARGIN = 16;
    private static final int TOP = 16;
    private static final int LIST_W = 150;
    private static final int INFO_W = 240;
    private static final int ROW_H = 15;
    private static final int DIALOG_H = 80;
    private static final int ICON_SIZE = 16;
    private static final int LINE_H = 15;
    private static final int ROTATE_TICKS = 40;

    private static final int BG = 0x99000000;
    private static final int POPUP_BG = 0xF0101010;
    private static final int BORDER = 0xFFD0D0D0;
    private static final int TEXT = 0xFFFFFF;
    private static final int MUTED = 0xAAAAAA;
    private static final int GOLD = 0xFFD700;
    private static final int GREEN = 0x66FF66;
    private static final int RED = 0xFF6666;

    private final int entityId;
    private final List<QuestView> quests;
    private String selectedId;
    private int listScroll = 0;
    private int infoScroll = 0;
    private int dialogScroll = 0;

    private QuestView.Line hoverLine = null;
    private int hoverX = 0, hoverY = 0;
    private String hoverCurrentIconId = null;

    public QuestScreen(S2COpenQuests packet) {
        super(Component.literal("任务"));
        this.entityId = packet.entityId;
        this.quests = packet.quests;
        if (!quests.isEmpty()) this.selectedId = quests.get(0).id;
    }

    public int getEntityId() { return entityId; }
    public String getSelectedId() { return selectedId; }
    public void setSelectedId(String id) { this.selectedId = id; }

    private QuestView getSelected() {
        if (selectedId == null) return quests.isEmpty() ? null : quests.get(0);
        for (QuestView q : quests) {
            if (q.id.equals(selectedId)) return q;
        }
        return quests.isEmpty() ? null : quests.get(0);
    }

    private String computeCurrentIconId(QuestView.Line line) {
        if (line.alternatives != null && !line.alternatives.isEmpty()) {
            long t;
            if (Minecraft.getInstance().level != null) {
                t = Minecraft.getInstance().level.getGameTime();
            } else {
                t = System.currentTimeMillis() / 50L;
            }
            int idx = (int) ((t / ROTATE_TICKS) % line.alternatives.size());
            return line.alternatives.get(idx);
        }
        return line.iconId;
    }

    private int textOffsetFor(QuestView.Line line) {
        if ("entity".equals(line.iconType)
                || "exp".equals(line.iconType)
                || "none".equals(line.iconType)) {
            return 4;
        }
        return (int)((ICON_SIZE + 4) * SCALE);
    }

    // ===== 缩放绘制 =====

    private void drawString85(GuiGraphics gg, String text, int x, int y, int color, boolean shadow) {
        gg.pose().pushPose();
        gg.pose().translate(x, y, 0);
        gg.pose().scale(SCALE, SCALE, 1.0F);
        gg.drawString(this.font, text, 0, 0, color, shadow);
        gg.pose().popPose();
    }

    private int scaledWidth(String text) {
        return (int)(this.font.width(text) * SCALE);
    }

    // ===== 渲染 =====

    @Override
    public void render(GuiGraphics gg, int mouseX, int mouseY, float partialTick) {
        hoverLine = null;
        hoverCurrentIconId = null;

        int listX = MARGIN;
        int listY = TOP;
        int listH = Math.min(this.height - TOP - DIALOG_H - 20, 12 * ROW_H);

        int infoX = this.width - INFO_W - MARGIN;
        int infoY = TOP;
        int infoH = this.height - TOP - DIALOG_H - 20;

        // ===== 左列表 =====
        gg.fill(listX - 2, listY - 2, listX + LIST_W + 2, listY + listH + 2, BG);
        drawBorder(gg, listX - 2, listY - 2, LIST_W + 4, listH + 4);

        int visibleRows = listH / ROW_H;
        int endIdx = Math.min(listScroll + visibleRows, quests.size());

        for (int i = listScroll; i < endIdx; i++) {
            QuestView q = quests.get(i);
            int rowY = listY + (i - listScroll) * ROW_H;
            boolean hover = mouseX >= listX && mouseX < listX + LIST_W
                    && mouseY >= rowY && mouseY < rowY + ROW_H - 1;
            boolean isSel = q.id.equals(selectedId);
            if (isSel) gg.fill(listX, rowY, listX + LIST_W, rowY + ROW_H - 1, 0x60FFFFFF);
            else if (hover) gg.fill(listX, rowY, listX + LIST_W, rowY + ROW_H - 1, 0x30FFFFFF);

            int color = switch (q.status) {
                case "completed" -> MUTED;
                case "completable" -> GREEN;
                case "in_progress" -> RED;
                default -> TEXT;
            };
            drawString85(gg, q.name, listX + 4, rowY + 4, color, false);
        }

        if (quests.size() > visibleRows) {
            int barX = listX + LIST_W + 3;
            int barW = 4;
            int barH = Math.max(20, listH * visibleRows / quests.size());
            int barY = listY + (listH - barH) * listScroll / Math.max(1, quests.size() - visibleRows);
            gg.fill(barX, listY, barX + barW, listY + listH, 0x40FFFFFF);
            gg.fill(barX, barY, barX + barW, barY + barH, 0xC0FFFFFF);
        }

        // ===== 右详情 =====
        gg.fill(infoX - 2, infoY - 2, infoX + INFO_W + 2, infoY + infoH + 2, BG);
        drawBorder(gg, infoX - 2, infoY - 2, INFO_W + 4, infoH + 4);

        QuestView q = getSelected();
        if (q != null) {
            int cx = infoX + 6;
            int availTop = infoY + 6;

            int btnW = 80;
            int btnH = 22;
            int btnX = infoX + (INFO_W - btnW) / 2;
            int btnY = infoY + infoH - btnH - 6;
            int availBottom = btnY - 6;

            // 用实际渲染间距累加
            int contentHeight = 14 + 16 + 13
                    + q.requirements.size() * LINE_H + 6 + 13
                    + q.rewards.size() * LINE_H;

            int availHeight = availBottom - availTop;
            int scrollMax = Math.max(0, contentHeight - availHeight);
            if (infoScroll > scrollMax) infoScroll = scrollMax;
            if (infoScroll < 0) infoScroll = 0;

            int cy = availTop - infoScroll;

            // ★ 裁剪
            gg.enableScissor(infoX, availTop, infoX + INFO_W, availBottom);

            String statusLabel = switch (q.status) {
                case "unaccepted" -> "未接取";
                case "in_progress" -> "进行中";
                case "completable" -> "可提交";
                case "completed" -> "已完成";
                default -> "";
            };
            int statusColor = switch (q.status) {
                case "completable" -> GREEN;
                case "completed" -> MUTED;
                case "in_progress" -> RED;
                default -> GOLD;
            };

            drawString85(gg, "[" + statusLabel + "]", cx, cy, statusColor, true);
            cy += 14;

            String repeatLabel;
            if (q.maxCompletions > 0) {
                repeatLabel = "可完成 " + q.completions + " / " + q.maxCompletions + " 次";
            } else {
                repeatLabel = "可无限完成  ·  已完成 " + q.completions + " 次";
            }
            drawString85(gg, repeatLabel, cx, cy, MUTED, false);
            cy += 16;

            drawString85(gg, "【需求】", cx, cy, MUTED, false);
            cy += 13;

            int iconDrawSize = (int)(ICON_SIZE * SCALE);

            for (QuestView.Line line : q.requirements) {
                String curId = computeCurrentIconId(line);
                int iconX = cx;
                int iconY = cy;

                boolean isClickable = "item".equals(line.iconType) || "tag".equals(line.iconType) || "entity".equals(line.iconType);
                boolean hover = isClickable
                        && mouseX >= iconX && mouseX < iconX + iconDrawSize
                        && mouseY >= iconY && mouseY < iconY + iconDrawSize
                        && iconY + iconDrawSize > availTop && iconY < availBottom;

                drawLineIcon(gg, line, curId, iconX, iconY);

                if (hover) {
                    hoverLine = line;
                    hoverX = mouseX;
                    hoverY = mouseY;
                    hoverCurrentIconId = curId;
                }

                int textX = cx + textOffsetFor(line);
                String text = line.text + "  (" + line.cur + "/" + line.total + ")";
                drawString85(gg, text, textX, cy + 3, TEXT, false);
                cy += LINE_H;
            }
            cy += 6;

            drawString85(gg, "【奖励】", cx, cy, MUTED, false);
            cy += 13;

            for (QuestView.Line line : q.rewards) {
                String curId = computeCurrentIconId(line);
                int iconX = cx;
                int iconY = cy;

                boolean isClickable = "item".equals(line.iconType) || "tag".equals(line.iconType) || "entity".equals(line.iconType);
                boolean hover = isClickable
                        && mouseX >= iconX && mouseX < iconX + iconDrawSize
                        && mouseY >= iconY && mouseY < iconY + iconDrawSize
                        && iconY + iconDrawSize > availTop && iconY < availBottom;

                drawLineIcon(gg, line, curId, iconX, iconY);

                if (hover) {
                    hoverLine = line;
                    hoverX = mouseX;
                    hoverY = mouseY;
                    hoverCurrentIconId = curId;
                }

                int textX = cx + textOffsetFor(line);
                String text;
                if ("exp".equals(line.iconType)) {
                    text = "经验 x" + line.total;
                } else {
                    text = line.text + (line.total > 1 ? " x" + line.total : "");
                }
                drawString85(gg, text, textX, cy + 3, TEXT, false);
                cy += LINE_H;
            }

            gg.disableScissor();

            // 滚动条（裁剪外）
            if (scrollMax > 0) {
                int barX = infoX + INFO_W - 3;
                int barW = 3;
                int trackH = availHeight;
                int barH = Math.max(16, trackH * trackH / (trackH + scrollMax));
                int barY = availTop + (trackH - barH) * infoScroll / scrollMax;
                gg.fill(barX, availTop, barX + barW, availBottom, 0x40FFFFFF);
                gg.fill(barX, barY, barX + barW, barY + barH, 0xC0FFFFFF);
            }

            // 按钮（裁剪外）
            if ("unaccepted".equals(q.status)) {
                drawButton(gg, btnX, btnY, btnW, btnH, "接取", mouseX, mouseY);
            } else if ("completable".equals(q.status)) {
                boolean hasItemReq = false;
                for (QuestView.Line line : q.requirements) {
                    if ("item".equals(line.iconType) || "tag".equals(line.iconType)) {
                        hasItemReq = true; break;
                    }
                }
                drawButton(gg, btnX, btnY, btnW, btnH,
                        hasItemReq ? "提交物品" : "提交任务", mouseX, mouseY);
            }
        }

        // ===== 底部描述 =====
        int boxW = (int) (this.width * 0.6);
        int boxX = (this.width - boxW) / 2;
        int boxY = this.height - DIALOG_H - 10;
        gg.fill(boxX, boxY, boxX + boxW, boxY + DIALOG_H, BG);
        drawBorder(gg, boxX, boxY, boxW, DIALOG_H);

        drawString85(gg, "图书管理员", boxX + 8, boxY + 6, GOLD, true);
        if (q != null) {
            drawString85(gg, q.name, boxX + 8, boxY + 22, TEXT, true);

            int descTop = boxY + 38;
            int descBottom = boxY + DIALOG_H - 4;
            int descH = descBottom - descTop;

            List<String> lines = wrap85(q.description, boxW - 16);
            int lineStep = 11;
            int totalH = lines.size() * lineStep;
            int descScrollMax = Math.max(0, totalH - descH);
            if (dialogScroll > descScrollMax) dialogScroll = descScrollMax;
            if (dialogScroll < 0) dialogScroll = 0;

            gg.enableScissor(boxX + 1, descTop, boxX + boxW - 1, descBottom);
            int ty = descTop - dialogScroll;
            for (String line : lines) {
                if (ty + lineStep >= descTop - lineStep && ty <= descBottom + lineStep) {
                    drawString85(gg, line, boxX + 8, ty, TEXT, false);
                }
                ty += lineStep;
            }
            gg.disableScissor();

            if (descScrollMax > 0) {
                int barX = boxX + boxW - 3;
                int barW = 3;
                int barH = Math.max(12, descH * descH / totalH);
                int barY = descTop + (descH - barH) * dialogScroll / descScrollMax;
                gg.fill(barX, descTop, barX + barW, descBottom, 0x40FFFFFF);
                gg.fill(barX, barY, barX + barW, barY + barH, 0xC0FFFFFF);
            }
        }

        super.render(gg, mouseX, mouseY, partialTick);

        if (hoverLine != null) {
            switch (hoverLine.iconType) {
                case "item" -> {
                    if (hoverCurrentIconId != null) {
                        try {
                            var item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(hoverCurrentIconId));
                            if (item != null) {
                                gg.renderTooltip(this.font,
                                        new ItemStack(item, hoverLine.total),
                                        mouseX, mouseY);
                            }
                        } catch (Throwable t) { }
                    }
                }
                case "tag" -> {
                    if (hoverLine.alternatives != null) {
                        renderTagPopup(gg, hoverLine.alternatives, mouseX, mouseY);
                    }
                }
                case "entity" -> {
                    if (hoverCurrentIconId != null) {
                        renderEntityPopup(gg, hoverCurrentIconId, mouseX, mouseY);
                    }
                }
            }
        }
    }

    private void drawLineIcon(GuiGraphics gg, QuestView.Line line, String curId, int x, int y) {
        if ("entity".equals(line.iconType) || "exp".equals(line.iconType) || "none".equals(line.iconType)) {
            return;
        }
        if (curId == null) return;
        try {
            var item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(curId));
            if (item != null) {
                ItemStack stack = new ItemStack(item, line.total);
                gg.pose().pushPose();
                gg.pose().translate(x, y, 0);
                gg.pose().scale(SCALE, SCALE, 1.0F);
                gg.renderItem(stack, 0, 0);
                if (line.total > 1) {
                    gg.renderItemDecorations(this.font, stack, 0, 0, String.valueOf(line.total));
                }
                gg.pose().popPose();
            }
        } catch (Throwable t) { }
    }

    private void renderTagPopup(GuiGraphics gg, List<String> items, int mouseX, int mouseY) {
        if (items.isEmpty()) return;

        int cols = 9;
        int cell = 18;
        int pad = 6;
        int rows = (items.size() + cols - 1) / cols;
        int w = cols * cell + pad * 2;
        int h = rows * cell + pad * 2;

        int x = mouseX + 16;
        int y = mouseY - h / 2;
        if (x + w > this.width - 4) x = mouseX - 16 - w;
        if (x < 4) x = 4;
        if (y < 4) y = 4;
        if (y + h > this.height - 4) y = this.height - h - 4;

        gg.fill(x, y, x + w, y + h, POPUP_BG);
        drawBorder(gg, x, y, w, h);

        for (int i = 0; i < items.size(); i++) {
            int col = i % cols;
            int row = i / cols;
            int ix = x + pad + col * cell + 1;
            int iy = y + pad + row * cell + 1;
            try {
                var item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(items.get(i)));
                if (item != null) {
                    gg.renderItem(new ItemStack(item), ix, iy);
                }
            } catch (Throwable t) { }
        }
    }

    private void renderEntityPopup(GuiGraphics gg, String entityId, int mouseX, int mouseY) {
        int w = 120;
        int h = 150;

        int x = mouseX + 16;
        int y = mouseY - h / 2;
        if (x + w > this.width - 4) x = mouseX - 16 - w;
        if (x < 4) x = 4;
        if (y < 4) y = 4;
        if (y + h > this.height - 4) y = this.height - h - 4;

        gg.fill(x, y, x + w, y + h, POPUP_BG);
        drawBorder(gg, x, y, w, h);

        try {
            var type = ForgeRegistries.ENTITY_TYPES.getValue(new ResourceLocation(entityId));
            if (type == null) return;

            String name = type.getDescription().getString();
            gg.drawString(this.font, name,
                    x + (w - this.font.width(name)) / 2, y + 6, 0xFFFFFF, false);

            var level = Minecraft.getInstance().level;
            if (level == null) return;
            Entity entity = type.create(level);
            if (entity == null) return;
            entity.setPos(0, 0, 0);
            if (entity instanceof LivingEntity le) {
                le.yBodyRot = 0;
                le.yHeadRot = 0;
            }
            entity.setYRot(0);
            entity.setXRot(0);
            entity.yRotO = 0;
            entity.xRotO = 0;

            EntityRenderDispatcher disp = Minecraft.getInstance().getEntityRenderDispatcher();
            var renderer = disp.getRenderer(entity);
            if (renderer == null) return;

            float angle = (System.currentTimeMillis() % 8000L) / 8000F * 360F;

            PoseStack ps = gg.pose();
            ps.pushPose();
            ps.translate(x + w / 2.0, y + h - 12, 100);
            float targetH = h - 40;
            float entityH = Math.max(entity.getBbHeight(), 0.5f);
            float scale = targetH / entityH / 1.6f;
            ps.scale(scale, -scale, scale);
            ps.mulPose(Axis.YP.rotationDegrees(angle));

            disp.setRenderShadow(false);
            MultiBufferSource.BufferSource buffer = Minecraft.getInstance().renderBuffers().bufferSource();
            disp.render(entity, 0, 0, 0, 0, 1, ps, buffer, 0xF000F0);
            buffer.endBatch();
            disp.setRenderShadow(true);

            ps.popPose();
        } catch (Throwable t) { }
    }

    private void drawButton(GuiGraphics gg, int x, int y, int w, int h, String label, int mx, int my) {
        boolean hover = mx >= x && mx < x + w && my >= y && my < y + h;
        gg.fill(x, y, x + w, y + h, hover ? 0xCC303030 : 0xCC101010);
        drawBorder(gg, x, y, w, h);
        int tw = scaledWidth(label);
        drawString85(gg, label, x + (w - tw) / 2, y + (h - (int)(8 * SCALE)) / 2,
                hover ? 0xFFFF80 : TEXT, false);
    }

    private void drawBorder(GuiGraphics gg, int x, int y, int w, int h) {
        gg.fill(x, y, x + w, y + 1, BORDER);
        gg.fill(x, y + h - 1, x + w, y + h, BORDER);
        gg.fill(x, y, x + 1, y + h, BORDER);
        gg.fill(x + w - 1, y, x + w, y + h, BORDER);
    }

    private List<String> wrap85(String text, int maxWidth) {
        int scaledMax = (int)(maxWidth / SCALE);
        List<String> result = new java.util.ArrayList<>();
        StringBuilder current = new StringBuilder();
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            String test = current.toString() + c;
            if (this.font.width(test) > scaledMax) {
                result.add(current.toString());
                current = new StringBuilder();
            }
            current.append(c);
        }
        if (current.length() > 0) result.add(current.toString());
        return result;
    }

    @Override
    public boolean mouseClicked(double mx, double my, int button) {
        int listX = MARGIN;
        int listY = TOP;
        int listH = Math.min(this.height - TOP - DIALOG_H - 20, 12 * ROW_H);

        if (mx >= listX && mx < listX + LIST_W && my >= listY && my < listY + listH) {
            int row = (int) ((my - listY) / ROW_H);
            int idx = listScroll + row;
            if (idx >= 0 && idx < quests.size()) {
                selectedId = quests.get(idx).id;
                infoScroll = 0;
                dialogScroll = 0;
                return true;
            }
        }

        QuestView q = getSelected();
        if (q != null) {
            int infoX = this.width - INFO_W - MARGIN;
            int infoY = TOP;
            int infoH = this.height - TOP - DIALOG_H - 20;
            int btnW = 80;
            int btnH = 22;
            int btnX = infoX + (INFO_W - btnW) / 2;
            int btnY = infoY + infoH - btnH - 6;
            if (mx >= btnX && mx < btnX + btnW && my >= btnY && my < btnY + btnH) {
                if ("unaccepted".equals(q.status)) {
                    ModNetwork.CHANNEL.sendToServer(new C2SQuestAction(entityId, q.id, "accept"));
                    return true;
                }
                if ("completable".equals(q.status)) {
                    ModNetwork.CHANNEL.sendToServer(new C2SQuestAction(entityId, q.id, "submit"));
                    return true;
                }
            }
        }

        return super.mouseClicked(mx, my, button);
    }

    @Override
    public boolean mouseScrolled(double mx, double my, double delta) {
        int listX = MARGIN;
        int listY = TOP;
        int listH = Math.min(this.height - TOP - DIALOG_H - 20, 12 * ROW_H);

        if (mx >= listX && mx < listX + LIST_W && my >= listY && my < listY + listH) {
            int visibleRows = listH / ROW_H;
            int max = Math.max(0, quests.size() - visibleRows);
            if (delta > 0) listScroll = Math.max(0, listScroll - 1);
            else if (delta < 0) listScroll = Math.min(max, listScroll + 1);
            return true;
        }

        int infoX = this.width - INFO_W - MARGIN;
        int infoY = TOP;
        int infoH = this.height - TOP - DIALOG_H - 20;
        int btnH = 22;
        if (mx >= infoX && mx < infoX + INFO_W && my >= infoY && my < infoY + infoH - btnH - 6) {
            if (delta > 0) infoScroll = Math.max(0, infoScroll - 8);
            else if (delta < 0) infoScroll += 8;
            return true;
        }

        int boxW = (int) (this.width * 0.6);
        int boxX = (this.width - boxW) / 2;
        int boxY = this.height - DIALOG_H - 10;
        if (mx >= boxX && mx < boxX + boxW && my >= boxY && my < boxY + DIALOG_H) {
            if (delta > 0) dialogScroll = Math.max(0, dialogScroll - 8);
            else if (delta < 0) dialogScroll += 8;
            return true;
        }

        return super.mouseScrolled(mx, my, delta);
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