package com.example.examplemod;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderHandEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = ExampleMod.MODID, value = Dist.CLIENT)
public class OldSaberRenderHandler {

    @SubscribeEvent
    public static void onRenderHand(RenderHandEvent event) {
        if (event.getHand() != InteractionHand.MAIN_HAND) return;

        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null) return;

        ItemStack stack = event.getItemStack();
        if (!stack.is(ExampleMod.OLD_SABER.get())) return;
        if (!player.isUsingItem()) return;
        if (player.getUseItem() != stack) return;

        event.setCanceled(true);

        PoseStack poseStack = event.getPoseStack();
        poseStack.pushPose();

        // 1.8 风格格挡变换：横刀在身前
        poseStack.translate(0.56F, -0.52F, -0.72F);
        poseStack.mulPose(Axis.YP.rotationDegrees(-90.0F));
        poseStack.mulPose(Axis.ZP.rotationDegrees(45.0F));
        poseStack.mulPose(Axis.XP.rotationDegrees(30.0F));

        // 关键：把 player 传进去，才能正确读取 drawn 属性 → 显示拔出贴图
        Minecraft.getInstance().getItemRenderer().renderStatic(
                player,                                    // ← entity
                stack,
                ItemDisplayContext.FIRST_PERSON_RIGHT_HAND,
                false,                                     // leftHand
                poseStack,
                event.getMultiBufferSource(),
                player.level(),
                event.getPackedLight(),
                OverlayTexture.NO_OVERLAY,
                player.getId()
        );

        poseStack.popPose();
    }
}