package com.example.examplemod.mixin;

import com.example.examplemod.OldSaberBeltRenderer;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntityRenderer.class)
public class PlayerRendererMixin {

    @Inject(
            method = "render(Lnet/minecraft/world/entity/LivingEntity;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/mojang/blaze3d/vertex/PoseStack;popPose()V",
                    shift = At.Shift.BEFORE
            )
    )
    private void yingzhiji$renderBelt(LivingEntity entity, float entityYaw, float partialTicks,
                                      PoseStack poseStack, MultiBufferSource buffer, int packedLight,
                                      CallbackInfo ci) {

        if (!(entity instanceof AbstractClientPlayer player)) return;

        ItemStack beltItem = OldSaberBeltRenderer.getBeltItem(player);
        if (beltItem.isEmpty()) return;

        LivingEntityRenderer<?, ?> renderer = (LivingEntityRenderer<?, ?>) (Object) this;
        if (!(renderer.getModel() instanceof PlayerModel<?> model)) return;

        // 独立 buffer，避免污染玩家渲染状态
        MultiBufferSource.BufferSource beltBuffer = Minecraft.getInstance().renderBuffers().bufferSource();

        poseStack.pushPose();

        // 死绑躯干
        model.body.translateAndRotate(poseStack);

        // 偏移到左腰
        poseStack.translate(0.26F, 0.67F, 0.2F);

        // 刀鞘角度
        poseStack.mulPose(Axis.XP.rotationDegrees(215.0F));
        poseStack.mulPose(Axis.ZP.rotationDegrees(0.0F));
        poseStack.mulPose(Axis.YP.rotationDegrees(90.0F));

        // 渲染物品（用独立 buffer）
        Minecraft.getInstance().getItemRenderer().renderStatic(
                (LivingEntity) null,
                beltItem,
                ItemDisplayContext.FIXED,
                false,
                poseStack,
                beltBuffer,
                player.level(),
                packedLight,
                OverlayTexture.NO_OVERLAY,
                player.getId()
        );

        // 立即刷出，不干扰后续渲染
        beltBuffer.endBatch();

        poseStack.popPose();
    }
}