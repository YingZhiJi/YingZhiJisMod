package com.example.examplemod;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;

public class ApocalypseAnimationRenderer extends EntityRenderer<ApocalypseAnimationEntity> {

    private static final ResourceLocation TEXTURE =
            new ResourceLocation("yingzhiji_s_mod", "textures/entity/apocalypse_animation.png");

    // 平面大小（格）
    private static final float SIZE = 6.0F;

    // 贴图帧数（128x1024，每帧 128 高，所以是 8 帧）
    private static final int TOTAL_FRAMES = 8;
    // 每帧持续多少 tick
    private static final int FRAME_TIME = 2;

    public ApocalypseAnimationRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull ApocalypseAnimationEntity entity) {
        return TEXTURE;
    }

    @Override
    public boolean shouldRender(@NotNull ApocalypseAnimationEntity entity, @NotNull Frustum frustum,
                                double camX, double camY, double camZ) {
        return true;
    }

    @Override
    public void render(ApocalypseAnimationEntity entity, float entityYaw, float partialTick,
                       PoseStack poseStack, MultiBufferSource buffer, int packedLight) {

        poseStack.pushPose();

        // 平躺于地面：绕 X 轴转 90 度
        poseStack.mulPose(Axis.XP.rotationDegrees(90.0F));

        Matrix4f matrix = poseStack.last().pose();
        VertexConsumer consumer = buffer.getBuffer(
                net.minecraft.client.renderer.RenderType.entityCutoutNoCull(TEXTURE));

        int frame = (entity.tickCount / FRAME_TIME) % TOTAL_FRAMES;
        float frameHeight = 1.0F / TOTAL_FRAMES;
        float v0 = frame * frameHeight;
        float v1 = v0 + frameHeight;

        float half = SIZE / 2.0F;

        consumer.vertex(matrix, -half, -half, 0)
                .color(255, 255, 255, 255).uv(0.0F, v1)
                .overlayCoords(OverlayTexture.NO_OVERLAY).uv2(LightTexture.FULL_BRIGHT)
                .normal(0, 1, 0).endVertex();

        consumer.vertex(matrix, half, -half, 0)
                .color(255, 255, 255, 255).uv(1.0F, v1)
                .overlayCoords(OverlayTexture.NO_OVERLAY).uv2(LightTexture.FULL_BRIGHT)
                .normal(0, 1, 0).endVertex();

        consumer.vertex(matrix, half, half, 0)
                .color(255, 255, 255, 255).uv(1.0F, v0)
                .overlayCoords(OverlayTexture.NO_OVERLAY).uv2(LightTexture.FULL_BRIGHT)
                .normal(0, 1, 0).endVertex();

        consumer.vertex(matrix, -half, half, 0)
                .color(255, 255, 255, 255).uv(0.0F, v0)
                .overlayCoords(OverlayTexture.NO_OVERLAY).uv2(LightTexture.FULL_BRIGHT)
                .normal(0, 1, 0).endVertex();

        poseStack.popPose();
    }
}