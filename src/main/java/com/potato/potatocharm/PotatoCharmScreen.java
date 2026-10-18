package com.potato.potatocharm;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@OnlyIn(Dist.CLIENT)
public class PotatoCharmScreen extends Screen {

    private static final Map<String, String> NAMES = new HashMap<>();
    static {
        NAMES.put("minecraft:overworld", "主世界");
        NAMES.put("minecraft:the_nether", "下界");
        NAMES.put("minecraft:the_end", "末地");
    }

    private static final int PER_PAGE = 12;
    private static final int ROW_H = 18;
    private static final int BTN_H = 16;
    private static final int PANEL_W = 172;
    private static final int GAP = 8;

    private final List<String> dims;
    private final String currentDim;
    private String selectedDim;
    private int page = 0;
    private boolean timeLocked;

    private EditBox inputX, inputY, inputZ;
    private TimeSlider timeSlider;
    private int pendingTime = -1;

    public PotatoCharmScreen(List<String> dims, String currentDim, boolean timeLocked) {
        super(Component.literal("土豆菜单"));
        this.dims = dims;
        this.currentDim = currentDim;
        this.selectedDim = currentDim;
        this.timeLocked = timeLocked;
    }

    private static String shortName(String dimId) {
        String zh = NAMES.get(dimId);
        if (zh != null) return zh;
        if (dimId.length() > 18) return dimId.substring(0, 16) + "…";
        return dimId;
    }

    private static String fullName(String dimId) {
        String zh = NAMES.get(dimId);
        return zh == null ? dimId : zh + " (" + dimId + ")";
    }

    @Override
    protected void init() {
        rebuild();
    }

    private void rebuild() {
        this.clearWidgets();

        int centerX = this.width / 2;
        int totalW = PANEL_W * 2 + GAP;
        int left = centerX - totalW / 2;
        int right = left + PANEL_W + GAP;
        int top = 40;

        int start = page * PER_PAGE;
        int end = Math.min(start + PER_PAGE, dims.size());

        for (int i = start; i < end; i++) {
            String dim = dims.get(i);
            int row = i - start;
            int y = top + row * ROW_H;

            boolean isCurrent = dim.equals(currentDim);
            boolean isSelected = dim.equals(selectedDim);

            String prefix;
            if (isCurrent) prefix = "▶ ";
            else if (isSelected) prefix = "◈ ";
            else prefix = "  ";

            addRenderableWidget(Button.builder(
                    Component.literal(prefix + shortName(dim)),
                    b -> { selectedDim = dim; rebuild(); }
            ).bounds(left, y, PANEL_W, BTN_H).build());
        }

        int pagY = top + PER_PAGE * ROW_H + 2;
        if (page > 0) {
            addRenderableWidget(Button.builder(Component.literal("◀"),
                            b -> { page--; rebuild(); })
                    .bounds(left, pagY, 24, BTN_H).build());
        }
        if (end < dims.size()) {
            addRenderableWidget(Button.builder(Component.literal("▶"),
                            b -> { page++; rebuild(); })
                    .bounds(left + PANEL_W - 24, pagY, 24, BTN_H).build());
        }

        int opY = top;

        addRenderableWidget(Button.builder(
                Component.literal("按当前坐标传送"),
                b -> sendTeleport(C2STeleport.MODE_CURRENT)
        ).bounds(right, opY, PANEL_W, BTN_H).build());
        opY += ROW_H + 2;

        addRenderableWidget(Button.builder(
                Component.literal("传送至世界原点"),
                b -> sendTeleport(C2STeleport.MODE_ORIGIN)
        ).bounds(right, opY, PANEL_W, BTN_H).build());
        opY += ROW_H + 6;

        int boxW = (PANEL_W - 8) / 3;
        inputX = new EditBox(this.font, right, opY, boxW, BTN_H, Component.literal("X"));
        inputX.setMaxLength(14);
        inputX.setValue("0");
        addRenderableWidget(inputX);

        inputY = new EditBox(this.font, right + boxW + 4, opY, boxW, BTN_H, Component.literal("Y"));
        inputY.setMaxLength(14);
        inputY.setValue("64");
        addRenderableWidget(inputY);

        inputZ = new EditBox(this.font, right + (boxW + 4) * 2, opY, boxW, BTN_H, Component.literal("Z"));
        inputZ.setMaxLength(14);
        inputZ.setValue("0");
        addRenderableWidget(inputZ);
        opY += ROW_H + 4;

        addRenderableWidget(Button.builder(
                Component.literal("传送至指定坐标"),
                b -> sendCustom()
        ).bounds(right, opY, PANEL_W, BTN_H).build());
        opY += ROW_H + 6;

        long currentDayTime = Minecraft.getInstance().level != null
                ? Minecraft.getInstance().level.getDayTime() : 0;
        int tod = (int)(((currentDayTime % 24000L) + 24000L) % 24000L);
        double init = tod / 24000.0;

        int sliderW = PANEL_W - 56;
        timeSlider = new TimeSlider(right, opY, sliderW, BTN_H, init);
        addRenderableWidget(timeSlider);

        addRenderableWidget(Button.builder(
                Component.literal(timeLocked ? "解锁" : "锁定"),
                b -> {
                    timeLocked = !timeLocked;
                    PotatoCharmNetwork.CHANNEL.sendToServer(
                            new C2SPotatoAction(C2SPotatoAction.ACTION_TOGGLE_TIME_LOCK));
                    rebuild();
                }
        ).bounds(right + sliderW + 4, opY, 52, BTN_H).build());

        opY += ROW_H + 6;

        int btnW = (PANEL_W - 2 * 4) / 3;
        int bx = right;

        addRenderableWidget(Button.builder(
                Component.literal("土豆?"),
                b -> PotatoCharmNetwork.CHANNEL.sendToServer(
                        new C2SPotatoAction(C2SPotatoAction.ACTION_POTATO_BURST))
        ).bounds(bx, opY, btnW, 20).build());
        bx += btnW + 4;

        addRenderableWidget(Button.builder(
                Component.literal("生存"),
                b -> PotatoCharmNetwork.CHANNEL.sendToServer(
                        new C2SPotatoAction(C2SPotatoAction.ACTION_SET_SURVIVAL))
        ).bounds(bx, opY, btnW, 20).build());
        bx += btnW + 4;

        addRenderableWidget(Button.builder(
                Component.literal("创造"),
                b -> PotatoCharmNetwork.CHANNEL.sendToServer(
                        new C2SPotatoAction(C2SPotatoAction.ACTION_SET_CREATIVE))
        ).bounds(bx, opY, btnW, 20).build());
    }

    private void sendTeleport(int mode) {
        PotatoCharmNetwork.CHANNEL.sendToServer(
                new C2STeleport(mode, selectedDim, 0, 0, 0));
    }

    private void sendCustom() {
        double x = parse(inputX, 0);
        double y = parse(inputY, 64);
        double z = parse(inputZ, 0);
        PotatoCharmNetwork.CHANNEL.sendToServer(
                new C2STeleport(C2STeleport.MODE_CUSTOM, selectedDim, x, y, z));
    }

    private double parse(EditBox box, double fallback) {
        try {
            return Double.parseDouble(box.getValue());
        } catch (NumberFormatException e) {
            return fallback;
        }
    }

    @Override
    public void tick() {
        super.tick();
        if (timeSlider == null) return;

        timeSlider.tickSlider();

        if (timeSlider.isUserInteracting()) {
            int current = timeSlider.getTime();
            if (current != pendingTime) {
                pendingTime = current;
                PotatoCharmNetwork.CHANNEL.sendToServer(
                        new C2SPotatoAction(C2SPotatoAction.ACTION_SET_TIME, current));
            }
        } else {
            long currentDayTime = Minecraft.getInstance().level != null
                    ? Minecraft.getInstance().level.getDayTime() : 0;
            int tod = (int)(((currentDayTime % 24000L) + 24000L) % 24000L);
            double worldValue = tod / 24000.0;

            if (Math.abs(worldValue - timeSlider.getSliderValue()) > 0.0001) {
                timeSlider.setSliderValue(worldValue);
                pendingTime = tod;
            }
        }
    }

    /** 跑马灯彩虹标题 */
    private void renderRainbowTitle(GuiGraphics gg, String text, int centerX, int y) {
        long tick;
        if (Minecraft.getInstance().level != null) {
            tick = Minecraft.getInstance().level.getGameTime();
        } else {
            tick = System.currentTimeMillis() / 50L;
        }

        int totalW = 0;
        for (int i = 0; i < text.length(); i++) {
            totalW += this.font.width(String.valueOf(text.charAt(i)));
        }

        int x = centerX - totalW / 2;
        for (int i = 0; i < text.length(); i++) {
            String ch = String.valueOf(text.charAt(i));
            int hue = (int)((tick * 3 + i * 30) % 360);
            int color = PotatoCharmHelper.rainbowColor(hue);
            gg.drawString(this.font, ch, x, y, color);
            x += this.font.width(ch);
        }
    }

    @Override
    public void render(GuiGraphics gg, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(gg);

        int centerX = this.width / 2;

        // 彩虹跑马灯标题
        renderRainbowTitle(gg, "土豆菜单", centerX, 12);

        int totalW = PANEL_W * 2 + GAP;
        int left = centerX - totalW / 2;
        int right = left + PANEL_W + GAP;

        gg.drawString(this.font, "当前: " + shortName(currentDim), left, 28, 0xFFFF80);
        gg.drawString(this.font, "目标: " + shortName(selectedDim), right, 28, 0xA0FFA0);

        if (inputX != null) {
            gg.drawString(this.font, "X", inputX.getX(), inputX.getY() - 9, 0xAAAAAA);
            gg.drawString(this.font, "Y", inputY.getX(), inputY.getY() - 9, 0xAAAAAA);
            gg.drawString(this.font, "Z", inputZ.getX(), inputZ.getY() - 9, 0xAAAAAA);
        }

        super.render(gg, mouseX, mouseY, partialTick);

        int listTop = 40;
        if (mouseX >= left && mouseX <= left + PANEL_W
                && mouseY >= listTop && mouseY < listTop + PER_PAGE * ROW_H) {
            int row = (mouseY - listTop) / ROW_H;
            int idx = page * PER_PAGE + row;
            if (idx >= 0 && idx < dims.size()) {
                gg.renderTooltip(this.font,
                        Component.literal(fullName(dims.get(idx))),
                        mouseX, mouseY);
            }
        }
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == org.lwjgl.glfw.GLFW.GLFW_KEY_E) {
            this.onClose();
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}