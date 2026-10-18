package com.example.examplemod.mixin;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.BossHealthOverlay;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.BossEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BossHealthOverlay.class)
public class BossHealthOverlayMixin {

    @Unique
    private static final ResourceLocation COMMANDER_BAR =
            new ResourceLocation("yingzhiji_s_mod", "textures/gui/commander_boss_bar.png");

    // 贴图整体尺寸
    @Unique
    private static final int TEX_WIDTH = 228;
    @Unique
    private static final int TEX_HEIGHT = 30;

    // 实际显示的有效血条区域（左右各留 1px 边框，上下各 15px）
    @Unique
    private static final int BAR_OFFSET_X = 1;   // 贴图左边框
    @Unique
    private static final int BAR_WIDTH    = 225; // 有效宽度
    @Unique
    private static final int BAR_HEIGHT   = 15;  // 单条高度

    // 原版血条宽度，用于居中修正
    @Unique
    private static final int VANILLA_WIDTH = 182;

    @Inject(method = "drawBar", at = @At("HEAD"), cancellable = true)
    private void onDrawBar(GuiGraphics guiGraphics, int x, int y, BossEvent bossEvent,
                           CallbackInfo ci) {

        if (!isCommander(bossEvent)) return;

        // 居中修正：把 225 宽的血条在原来 182 宽的位置上重新居中
        int drawX = x - (BAR_WIDTH - VANILLA_WIDTH) / 2;

        float ratio = bossEvent.getProgress();
        int filled = (int) (BAR_WIDTH * ratio);

        // ===== 背景（空血条）：贴图 v=0 那 15px =====
        guiGraphics.blit(COMMANDER_BAR,
                drawX, y,                       // 屏幕坐标
                BAR_OFFSET_X, 0,                // 贴图 u,v
                BAR_WIDTH, BAR_HEIGHT,          // 显示宽高
                TEX_WIDTH, TEX_HEIGHT);         // 贴图总尺寸

        // ===== 前景（满血条）：贴图 v=15 那 15px，按 filled 裁剪 =====
        if (filled > 0) {
            guiGraphics.blit(COMMANDER_BAR,
                    drawX, y,
                    BAR_OFFSET_X, BAR_HEIGHT,   // v = 15
                    filled, BAR_HEIGHT,
                    TEX_WIDTH, TEX_HEIGHT);
        }

        ci.cancel();
    }

    @Unique
    private boolean isCommander(BossEvent bossEvent) {
        String name = bossEvent.getName().getString();
        return name.equals(
                Component.translatable("entity.yingzhiji_s_mod.zomperium_commander").getString());
    }
}