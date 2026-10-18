package com.example.examplemod;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraftforge.fluids.FluidStack;

public class CrystallizerScreen extends AbstractContainerScreen<CrystallizerMenu> {

    private static final int TANK_X = 8;
    private static final int TANK_Y = 17;
    private static final int TANK_W = 12;
    private static final int TANK_H = 52;

    // 主背景贴图
    private static final ResourceLocation TEX_GUI =
            new ResourceLocation("yingzhiji_s_mod", "textures/gui/crystallizer.png");

    // 储罐液体前景图
    private static final ResourceLocation TEX_TANK_ASHBLAZITE =
            new ResourceLocation("yingzhiji_s_mod", "textures/gui/tank_ashblazite.png");
    private static final ResourceLocation TEX_TANK_DRAGON_BREATH =
            new ResourceLocation("yingzhiji_s_mod", "textures/gui/tank_dragon_breath.png");

    // ===== 文字颜色（ARGB） =====
    private static final int TITLE_COLOR = 0xFFFFFF;       // "结晶器"
    private static final int INVENTORY_COLOR = 0xFFFFFF;   // "背包"

    public CrystallizerScreen(CrystallizerMenu menu, Inventory inv, Component title) {
        super(menu, inv, title);
        this.imageWidth = 176;
        this.imageHeight = 166;
    }

    @Override
    protected void renderBg(GuiGraphics gui, float partialTick, int mouseX, int mouseY) {
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

        // 1. 主背景（含槽位边框、储罐空罐、进度条底槽）
        gui.blit(TEX_GUI,
                leftPos, topPos,
                0, 0,
                imageWidth, imageHeight,
                imageWidth, imageHeight);

        // 2. 液体前景
        renderFluid(gui);

        // 3. 进度条填充
        renderMeltBar(gui);
        renderCrystallizeBar(gui);
    }

    @Override
    protected void renderLabels(GuiGraphics gui, int mouseX, int mouseY) {
        gui.drawString(this.font, this.title,
                this.titleLabelX, this.titleLabelY, TITLE_COLOR, false);
        gui.drawString(this.font, this.playerInventoryTitle,
                this.inventoryLabelX, this.inventoryLabelY, INVENTORY_COLOR, false);
    }

    /**
     * 液体前景：从贴图底部裁一段画到储罐底部
     */
    private void renderFluid(GuiGraphics gui) {
        int fluidAmount = menu.getFluidAmount();
        int capacity = menu.getFluidCapacity();
        if (capacity <= 0 || fluidAmount <= 0) return;

        CrystallizerBlockEntity be = menu.getBlockEntity();
        if (be == null) return;
        FluidStack fluid = be.getFluidTank().getFluid();
        if (fluid.isEmpty()) return;

        ResourceLocation fluidTex = getFluidTexture(fluid);
        if (fluidTex == null) return;

        int fluidHeight = (int) Math.ceil((double) fluidAmount / capacity * TANK_H);
        if (fluidHeight > TANK_H) fluidHeight = TANK_H;
        if (fluidHeight <= 0) return;

        int srcV = TANK_H - fluidHeight;

        gui.blit(fluidTex,
                leftPos + TANK_X, topPos + TANK_Y + srcV,
                0, srcV,
                TANK_W, fluidHeight,
                TANK_W, TANK_H);
    }

    private ResourceLocation getFluidTexture(FluidStack fluid) {
        if (fluid.getFluid() == ModFluids.MOLTEN_ASHBLAZITE.get()) {
            return TEX_TANK_ASHBLAZITE;
        }
        if (fluid.getFluid() == ModFluids.MOLTEN_DRAGON_BREATH.get()) {
            return TEX_TANK_DRAGON_BREATH;
        }
        return null;
    }

    /**
     * 融化进度条填充（底槽在背景贴图里）
     */
    private void renderMeltBar(GuiGraphics gui) {
        int progress = menu.getMeltProgress();
        int maxProgress = menu.getMeltMaxProgress();
        if (maxProgress <= 0 || progress <= 0) return;

        int barX = leftPos + 25;
        int barY = topPos + 38;
        int barW = 18;
        int barH = 3;

        int fill = (int) ((float) progress / maxProgress * barW);
        gui.fill(barX, barY, barX + fill, barY + barH, 0xFFFFD700);
    }

    /**
     * 结晶进度条填充（底槽在背景贴图里）
     */
    private void renderCrystallizeBar(GuiGraphics gui) {
        int progress = menu.getCrystallizeProgress();
        int maxProgress = menu.getCrystallizeMaxProgress();
        if (maxProgress <= 0 || progress <= 0) return;

        int barX = leftPos + 49;
        int barY = topPos + 59;
        int barW = 78;
        int barH = 4;

        int fill = (int) ((float) progress / maxProgress * barW);
        gui.fill(barX, barY, barX + fill, barY + barH, 0xFF00CC00);
    }

    @Override
    public void render(GuiGraphics gui, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(gui);
        super.render(gui, mouseX, mouseY, partialTick);
        this.renderTooltip(gui, mouseX, mouseY);

        if (isMouseOverFluidTank(mouseX, mouseY)) {
            CrystallizerBlockEntity be = menu.getBlockEntity();
            if (be != null) {
                FluidStack fluid = be.getFluidTank().getFluid();
                if (!fluid.isEmpty()) {
                    gui.renderTooltip(this.font,
                            Component.literal(fluid.getDisplayName().getString()
                                    + ": " + fluid.getAmount() + " / "
                                    + menu.getFluidCapacity() + " mB"),
                            mouseX, mouseY);
                } else {
                    gui.renderTooltip(this.font,
                            Component.literal("0 / " + menu.getFluidCapacity() + " mB"),
                            mouseX, mouseY);
                }
            }
        }
    }

    private boolean isMouseOverFluidTank(int mouseX, int mouseY) {
        int x = leftPos + TANK_X;
        int y = topPos + TANK_Y;
        return mouseX >= x && mouseX < x + TANK_W
                && mouseY >= y && mouseY < y + TANK_H;
    }
}